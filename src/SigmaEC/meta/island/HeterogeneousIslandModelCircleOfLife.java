package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.SRandom;
import SigmaEC.evaluate.EvaluationOperator;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric O. Scott
 * @param <T> The genotype of individuals.
 * @param <P> The phenotype of individuals.
 */
public class HeterogeneousIslandModelCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    public final static String P_RANDOM = "random";
    public final static String P_TOPOLOGY = "topology";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    public final static String P_NUM_THREADS = "numThreads";
    public final static String P_MIGRATION = "migrationPolicy";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_METRICS = "metrics";
    
    // Default configuration parameters for all islands
    public final static String P_DEFAULT_COMPARATOR = "defaultFitnessComparator";
    public final static String P_DEFAULT_OBJECTIVE = "defaultObjective";
    public final static String P_DEFAULT_EVALUATOR = "defaultEvaluator";
    public final static String P_DEFAULT_OPERATORS = "defaultOperators";
    public final static String P_DEFAULT_IS_DYNAMIC = "defaultIsDynamic";
    
    // IslandConfiguration-specific parameter names
    public final static String P_ISLAND = "island";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_OBJECTIVE = "objective";
    public final static String P_EVALUATOR = "evaluator";
    public final static String P_OPERATORS = "operators";
    public final static String P_IS_DYNAMIC = "isDynamic";
    
    private final SRandom random;
    private final Topology topology;
    private final MigrationPolicy<T> migrationPolicy;
    private final Initializer<T> initializer;
    private final StoppingCondition<T> stoppingCondition;
    private final Option<List<PopulationMetric<T>>> metrics;
    private final int numThreads;
    
    private final List<HeterogeneousIslandConfiguration> islands;
    
    public List<IslandConfiguration> getIslands() {
        final ArrayList<IslandConfiguration> result = new ArrayList<>();
        result.addAll(islands);
        assert(repOK());
        return result;
    }
    
    public HeterogeneousIslandModelCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        topology = parameters.getInstanceFromParameter(Parameters.push(base, P_TOPOLOGY), Topology.class);
        if (topology.numIslands() <= 1)
            throw new IllegalStateException(String.format("%s: must have at least two islands in '%s'.", this.getClass().getSimpleName(), Parameters.push(base, P_TOPOLOGY)));
        migrationPolicy = parameters.getInstanceFromParameter(Parameters.push(base, P_MIGRATION), MigrationPolicy.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        numThreads = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_THREADS), topology.numIslands());
        metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        islands = new ArrayList<HeterogeneousIslandConfiguration>(topology.numIslands()) {{
                for (int i = 0; i < topology.numIslands(); i++)
                    add(new HeterogeneousIslandConfiguration(i, parameters, base));
        }};
        assert(repOK());
    }
    
    @Override
    public EvolutionResult evolve(final int run) {
        assert(run >= 0);
            reset();
            int step = 0;

            // Initialize subpopulations
            final Population<T> population = new Population<>(topology.numIslands(), initializer);
            // Evaluate fitness of initial subpopulations
            for (int i = 0; i < population.numSuppopulations(); i++)
                population.setSubpopulation(i, islands.get(i).getEvaluator().operate(run, step, population.getSubpopulation(i)));

            List<T> bestSoFarInds = getBestsOfStep(population);
            
            final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            while (!(stoppingCondition.stop(population, step))) {
                // Take measurements
                if (metrics.isDefined())
                    for (PopulationMetric<T> metric : metrics.get())
                        metric.measurePopulation(run, step, population);

                // Execute each island in parallel
                final Collection<Callable<Void>> tasks = new ArrayList<>(topology.numIslands());
                for (final HeterogeneousIslandConfiguration isl : islands)
                    tasks.add(new HeterogeneousIslandModelCircleOfLife.IslandStepper(run, step, isl, population));
                try {
                    final List<Future<Void>> results = executor.invokeAll(tasks);
                    for (final Future<Void> f : results)
                        f.get(); // Throw exception if a thread dies
                } catch (final Exception ex) {
                    Logger.getLogger(HeterogeneousIslandModelCircleOfLife.class.getName()).log(Level.SEVERE, null, ex);
                }

                migrationPolicy.migrateAll(step, population, topology, new Option(getIslands()));

                // Update our local best-so-far variable
                final List<T> bestOfStepInds = getBestsOfStep(population);
                bestSoFarInds = updateBestSoFars(bestOfStepInds, bestSoFarInds);
                
                flushMetrics();
                step++;
            }
            executor.shutdown();

            // Measure final population
            if (metrics.isDefined())
                for (PopulationMetric<T> metric : metrics.get())
                    metric.measurePopulation(run, step, population);

            assert(repOK());
            return new EvolutionResult<>(population, bestSoFarInds, getFitnesses(bestSoFarInds));
    }
    
    // <editor-fold defaultstate="collapsed" desc="CircleOfLife helpers">
    private List<T> getBestsOfStep(final Population<T> population) {
        final List<T> bests = new ArrayList<>(population.numSuppopulations());
        for (int i = 0; i < population.numSuppopulations(); i++)
            bests.add(population.getBest(i, islands.get(i).getFitnessComparator()));
        assert(!Misc.containsNulls(bests));
        return bests;
    }
    
    private List<T> updateBestSoFars(final List<T> bestOfStep, final List<T> previousBestSoFars) {
        assert(bestOfStep != null);
        assert(!Misc.containsNulls(bestOfStep));
        assert(previousBestSoFars != null);
        assert(!Misc.containsNulls(previousBestSoFars));
        assert(bestOfStep.size() == previousBestSoFars.size());
        final List<T> newBests = new ArrayList<>(bestOfStep.size());
        for (int i = 0; i < bestOfStep.size(); i++) {
            if (islands.get(i).getFitnessComparator().betterThan(bestOfStep.get(i), previousBestSoFars.get(i)))
                newBests.add(bestOfStep.get(i));
            else
                newBests.add(previousBestSoFars.get(i));
        }
        assert(!Misc.containsNulls(newBests));
        return newBests;
    }
    
    private List<Double> getFitnesses(final List<T> inds) {
        assert(inds != null);
        assert(!Misc.containsNulls(inds));
        return new ArrayList<Double>() {{
            for (final T ind : inds)
                add(ind.getFitness());
        }};
    }
    // </editor-fold>
    
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
    
    private class IslandStepper extends ContractObject implements Callable<Void> {
        private final Population<T> population;
        private final HeterogeneousIslandConfiguration island;
        private final int run;
        private final int step;
        
        public IslandStepper(final int run, final int step, final HeterogeneousIslandConfiguration island, final Population<T> population) {
            assert(island != null);
            assert(population != null);
            this.island = island;
            this.population = population;
            this.run = run;
            this.step = step;
            assert(repOK());
        }

        @Override
        public Void call() throws Exception {
            island.step(run, step, population);
            assert(repOK());
            return null;
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return run >= 0
                    && step >= 0
                    && island != null
                    && population != null;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof HeterogeneousIslandModelCircleOfLife.IslandStepper))
                return false;
            final IslandStepper ref = (IslandStepper)o;
            return step == ref.step
                    && run == ref.run
                    && population.equals(ref.population)
                    && island.equals(ref.island);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.population);
            hash = 71 * hash + Objects.hashCode(this.island);
            hash = 71 * hash + this.run;
            hash = 71 * hash + this.step;
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: run=%d, step=%d, population=%s, island=%s]", HeterogeneousIslandModelCircleOfLife.IslandStepper.class.getSimpleName(), run, step, population, island);
        }
        // </editor-fold>
    }
    
    private class HeterogeneousIslandConfiguration extends IslandConfiguration {
        private final int islandID;
        private final EvaluationOperator<T, P> evaluator;
        private final List<Operator<T>> operators;
        private final ObjectiveFunction<P> objective;
        private final FitnessComparator<T> fitnessComparator;
        private final boolean isDynamic;
        
        @Override
        public EvaluationOperator<T, P> getEvaluator() {
            return evaluator;
        }
        
        @Override
        public FitnessComparator<T> getFitnessComparator() {
            return fitnessComparator;
        }
        
        public HeterogeneousIslandConfiguration(final int islandID, final Parameters parameters, final String base) {
            assert(islandID >= 0);
            assert(islandID < topology.numIslands());
            assert(parameters != null);
            assert(base != null);
            this.islandID = islandID;
            final String localBase = Parameters.push(Parameters.push(base, P_ISLAND), String.valueOf(islandID));
            evaluator = parameters.getOptionalNewInstanceFromParameter(Parameters.push(localBase, P_EVALUATOR), Parameters.push(base, P_DEFAULT_EVALUATOR), EvaluationOperator.class);
            objective = parameters.getOptionalNewInstanceFromParameter(Parameters.push(localBase, P_OBJECTIVE), Parameters.push(base, P_DEFAULT_OBJECTIVE), ObjectiveFunction.class);
            isDynamic = parameters.getOptionalBooleanParameter(Parameters.push(localBase, P_IS_DYNAMIC), Parameters.push(base, P_DEFAULT_IS_DYNAMIC));
            fitnessComparator = parameters.getOptionalNewInstanceFromParameter(Parameters.push(localBase, P_COMPARATOR), Parameters.push(base, P_DEFAULT_COMPARATOR), FitnessComparator.class);
            operators = parameters.getOptionalNewInstancesFromParameter(Parameters.push(localBase, P_OPERATORS), Parameters.push(base, P_DEFAULT_OPERATORS), Operator.class);
            assert(repOK());
        }

        public void step(final int run, final int step, final Population<T> population) {
            assert(population != null);
            assert(population.numSuppopulations() == topology.numIslands());
            /*
            // Evaluate any new individuals that have been inserted into this island's subpopulation
            final List<T> subPop = population.getSubpopulation(islandID);
            for (int i = 0; i < subPop.size(); i++) {
                final T ind = subPop.get(i);
                if (!ind.isEvaluated())
                    subPop.set(i, evaluator.evaluate(ind));
            }*/
            
            // Apply operators
            for (final Operator<T> gen : operators) {
                final List<T> newSubpop = gen.operate(run, step, population.getSubpopulation(islandID));
                    population.setSubpopulation(islandID, newSubpop);
            }
            
            if (isDynamic)
                objective.setStep(step);
            assert(repOK());
        }
        
        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return islandID >= 0
                    && islandID < topology.numIslands()
                    && evaluator != null
                    && operators != null
                    && !operators.isEmpty()
                    && !Misc.containsNulls(operators)
                    && objective != null
                    && fitnessComparator != null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof HeterogeneousIslandModelCircleOfLife.HeterogeneousIslandConfiguration))
                return false;
            final HeterogeneousIslandConfiguration ref = (HeterogeneousIslandConfiguration)o;
            return islandID == ref.islandID
                    && isDynamic == ref.isDynamic
                    && fitnessComparator.equals(ref.fitnessComparator)
                    && evaluator.equals(ref.evaluator)
                    && operators.equals(ref.operators)
                    && objective.equals(ref.objective);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + this.islandID;
            hash = 41 * hash + Objects.hashCode(this.evaluator);
            hash = 41 * hash + Objects.hashCode(this.operators);
            hash = 41 * hash + Objects.hashCode(this.objective);
            hash = 41 * hash + Objects.hashCode(this.fitnessComparator);
            hash = 41 * hash + (this.isDynamic ? 1 : 0);
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: islandID=%d, %s=%B, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                    islandID,
                    P_IS_DYNAMIC, isDynamic,
                    P_COMPARATOR, fitnessComparator,
                    P_EVALUATOR, evaluator,
                    P_OPERATORS, operators,
                    P_OBJECTIVE, objective);
        }
        // </editor-fold>
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_TOPOLOGY != null
                && !P_TOPOLOGY.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && P_NUM_THREADS != null
                && !P_NUM_THREADS.isEmpty()
                && P_MIGRATION != null
                && !P_MIGRATION.isEmpty()
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_METRICS != null
                && !P_METRICS.isEmpty()
                && P_DEFAULT_COMPARATOR != null
                && !P_DEFAULT_COMPARATOR.isEmpty()
                && P_DEFAULT_EVALUATOR != null
                && !P_DEFAULT_EVALUATOR.isEmpty()
                && P_DEFAULT_IS_DYNAMIC != null
                && !P_DEFAULT_IS_DYNAMIC.isEmpty()
                && P_DEFAULT_OBJECTIVE != null
                && !P_DEFAULT_OBJECTIVE.isEmpty()
                && P_DEFAULT_OPERATORS != null
                && !P_DEFAULT_OPERATORS.isEmpty()
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
                && P_OPERATORS != null
                && !P_OPERATORS.isEmpty()
                && P_IS_DYNAMIC != null
                && !P_IS_DYNAMIC.isEmpty()
                && random != null
                && topology != null
                && topology.numIslands() > 1
                && migrationPolicy != null
                && initializer != null
                && stoppingCondition != null
                && metrics != null
                && numThreads > 0
                && islands != null
                && !islands.isEmpty()
                && !Misc.containsNulls(islands);
    }
            
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HeterogeneousIslandModelCircleOfLife))
            return false;
        final HeterogeneousIslandModelCircleOfLife ref = (HeterogeneousIslandModelCircleOfLife)o;
        return numThreads == ref.numThreads
                && random.equals(ref.random)
                && topology.equals(ref.topology)
                && migrationPolicy.equals(ref.migrationPolicy)
                && initializer.equals(ref.initializer)
                && stoppingCondition.equals(ref.stoppingCondition)
                && metrics.equals(ref.metrics)
                && islands.equals(ref.islands);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.random);
        hash = 89 * hash + Objects.hashCode(this.topology);
        hash = 89 * hash + Objects.hashCode(this.migrationPolicy);
        hash = 89 * hash + Objects.hashCode(this.initializer);
        hash = 89 * hash + Objects.hashCode(this.stoppingCondition);
        hash = 89 * hash + Objects.hashCode(this.metrics);
        hash = 89 * hash + this.numThreads;
        hash = 89 * hash + Objects.hashCode(this.islands);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, islands=%s]", this.getClass().getSimpleName(),
                P_NUM_THREADS, numThreads,
                P_RANDOM, random,
                P_TOPOLOGY, topology,
                P_MIGRATION, migrationPolicy,
                P_INITIALIZER, initializer,
                P_STOPPING_CONDITION, stoppingCondition,
                P_METRICS, metrics,
                islands);
    }
    // </editor-fold>
}
