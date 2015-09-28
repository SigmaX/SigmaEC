package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.meta.CircleOfLife;
import SigmaEC.meta.Operator;
import SigmaEC.meta.Population;
import SigmaEC.meta.StoppingCondition;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * 
 * @author Eric O. Scott
 */
public class IslandModelCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    public final static String P_RANDOM = "random";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_TOPOLOGY = "topology";
    public final static String P_OBJECTIVE = "objective";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_OPERATORS = "operators";
    public final static String P_METRICS = "metrics";
    public final static String P_IS_DYNAMIC = "isDynamic";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    
    private final SRandom random;
    private final Topology topology;
    private final Initializer<T> initializer;
    private final List<Operator<T>> operators;
    private final ObjectiveFunction<P> objective;
    private final FitnessComparator<T> fitnessComparator;
    private final Option<List<PopulationMetric<T>>> metrics;
    private final StoppingCondition<T> stoppingCondition;
    private final boolean isDynamic;
    
    public IslandModelCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        topology = parameters.getInstanceFromParameter(Parameters.push(base, P_TOPOLOGY), Topology.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        operators = parameters.getInstancesFromParameter(Parameters.push(base, P_OPERATORS), Operator.class);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        isDynamic = parameters.getOptionalBooleanParameter(Parameters.push(base, P_IS_DYNAMIC), true);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T> evolve(final int run) {
        assert(run >= 0);
        reset();
        
        // Initialize populations
        final Population<T> population = new Population<>(topology.numIslands());
        for (int i = 0; i < population.numSuppopulations(); i++)
            population.setSubpopulation(i, initializer.generatePopulation());
        
        final ExecutorService executor = Executors.newFixedThreadPool(topology.numIslands());
        int step = 0;
        T bestSoFarInd = null;
        while (!(stoppingCondition.stop(population, step))) {
            // Take measurements
            if (metrics.isDefined())
                for (PopulationMetric<T> metric : metrics.get())
                    metric.measurePopulation(run, step, population);
            
            // Execute each island in parallel
            for (int i = 0; i < topology.numIslands(); i++)
                executor.execute(new IslandStepper(run, step, population, i));
            executor.shutdown();
            while (!executor.isTerminated()) { }

            if (isDynamic)
                objective.setGeneration(step);
            
            // Update our local best-so-far variable
            final T bestOfGen = population.getBest(fitnessComparator);
            if (fitnessComparator.betterThan(bestOfGen, bestSoFarInd)) 
                bestSoFarInd = bestOfGen;
            flushMetrics();
            step++;
        }
        
        // Measure final population
        if (metrics.isDefined())
            for (PopulationMetric<T> metric : metrics.get())
                metric.measurePopulation(run, step, population);
        
        assert(repOK());
        return new EvolutionResult<>(population, bestSoFarInd, bestSoFarInd.getFitness());
    }
    
    private class IslandStepper extends ContractObject implements Runnable {
        private final Population<T> population;
        private final int subpopulation;
        private final int run;
        private final int generation;
        
        IslandStepper(final int run, final int generation, final Population<T> population, final int populationID) {
            assert(run >= 0);
            assert(generation >= 0);
            assert(population != null);
            this.run = run;
            this.generation = generation;
            this.population = population;
            this.subpopulation = populationID;
            assert(repOK());
        }
        
        @Override
        public void run() {
            // Apply operators
            for (Operator<T> gen : operators) {
                final List<T> newSubpop = gen.operate(run, generation, population.getSubpopulation(subpopulation));
                population.setSubpopulation(subpopulation, newSubpop);
            }
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return run >= 0
                    && generation >= 0
                    && population != null
                    && subpopulation >= 0
                    && subpopulation < population.numSuppopulations();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof IslandModelCircleOfLife.IslandStepper))
                return false;
            final IslandStepper ref = (IslandStepper)o;
            return run == ref.run
                    && generation == ref.generation
                    && subpopulation == ref.subpopulation
                    && population.equals(ref.population);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.population);
            hash = 79 * hash + this.subpopulation;
            hash = 79 * hash + this.run;
            hash = 79 * hash + this.generation;
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: run=%d, generation=%d, populationID=%s, population=%s]", IslandStepper.class.getSimpleName(), run, generation, subpopulation, population);
        }
        // </editor-fold>
    }

    
    /** Flush I/O buffers. */
    private void flushMetrics() {
        if (metrics.isDefined())
            for (final PopulationMetric<T> metric: metrics.get())
                metric.flush();
    }
    
    private void reset() {
        stoppingCondition.reset();
        if (metrics.isDefined())
            for (final PopulationMetric<T> metric : metrics.get())
                    metric.reset();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_IS_DYNAMIC != null
                && !P_IS_DYNAMIC.isEmpty()
                && P_METRICS != null
                && !P_METRICS.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_OPERATORS != null
                && !P_OPERATORS.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && P_TOPOLOGY != null
                && !P_TOPOLOGY.isEmpty()
                && random != null
                && topology != null
                && initializer != null
                && operators != null
                && !operators.isEmpty()
                && Misc.containsNulls(operators)
                && objective != null
                && fitnessComparator != null
                && metrics != null
                && !(metrics.isDefined() && (metrics.get().isEmpty() || Misc.containsNulls(metrics.get())))
                && stoppingCondition != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IslandModelCircleOfLife))
            return false;
        final IslandModelCircleOfLife ref = (IslandModelCircleOfLife)o;
        return isDynamic == ref.isDynamic
                && random.equals(ref.random)
                && topology.equals(ref.topology)
                && initializer.equals(ref.initializer)
                && operators.equals(ref.operators)
                && objective.equals(ref.objective)
                && fitnessComparator.equals(ref.fitnessComparator)
                && metrics.equals(ref.metrics)
                && stoppingCondition.equals(ref.stoppingCondition);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.random);
        hash = 13 * hash + Objects.hashCode(this.topology);
        hash = 13 * hash + Objects.hashCode(this.initializer);
        hash = 13 * hash + Objects.hashCode(this.operators);
        hash = 13 * hash + Objects.hashCode(this.objective);
        hash = 13 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 13 * hash + Objects.hashCode(this.metrics);
        hash = 13 * hash + Objects.hashCode(this.stoppingCondition);
        hash = 13 * hash + (this.isDynamic ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%d]", this.getClass().getSimpleName(),
                P_RANDOM, random,
                P_TOPOLOGY, topology,
                P_INITIALIZER, initializer,
                P_OPERATORS, operators,
                P_COMPARATOR, fitnessComparator,
                P_METRICS, metrics,
                P_STOPPING_CONDITION, stoppingCondition,
                P_IS_DYNAMIC, isDynamic);
    }
    // </editor-fold>
}
