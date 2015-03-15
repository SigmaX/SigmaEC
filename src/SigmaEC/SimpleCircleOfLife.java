package SigmaEC;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.List;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    public final static String P_INITIALIZER = "initializer";
    public final static String P_GENERATORS = "generators";
    public final static String P_OBJECTIVE = "objective";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_METRICS = "metrics";
    public final static String P_RANDOM = "random";
    public final static String P_IS_DYNAMIC = "isDynamic";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    
    private final Initializer<T> initializer;
    private final List<Generator<T>> generators;
    private final FitnessComparator<T> fitnessComparator;
    private final Option<List<PopulationMetric<T>>> metrics;
    private final ObjectiveFunction<P> objective;
    private final StoppingCondition<T> stoppingCondition;
    private final boolean isDynamic;
    
    public SimpleCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        generators = parameters.getInstancesFromParameter(Parameters.push(base, P_GENERATORS), Generator.class);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        if (parameters.isDefined(Parameters.push(base, P_IS_DYNAMIC)))
            isDynamic = parameters.getBooleanParameter(Parameters.push(base, P_IS_DYNAMIC));
        else
            isDynamic = true;
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T> evolve(final int run) {
        assert(run >= 0);
        reset();
        List<T> population = initializer.generatePopulation();
        T bestSoFarInd = null;
        int i = 0;
        while (!stoppingCondition.stop(population, i)) {
            // Take measurements
            if (metrics.isDefined())
                for (PopulationMetric<T> metric : metrics.get())
                    metric.measurePopulation(run, i, population);
            
            // Apply operators
            for (Generator<T> gen : generators)
                population = gen.produceGeneration(population);
            
            // Tell the problem what generation we're on (if it's a dynamic landscape)
            if (isDynamic)
                objective.setGeneration(i);
            
            // Update our local best-so-far variable
            final T bestOfGen = Statistics.max(population, fitnessComparator);
            if (fitnessComparator.betterThan(bestOfGen, bestSoFarInd)) 
                bestSoFarInd = bestOfGen;
            
            flushMetrics();
            i++;
        }
        
        // Measure final population
        if (metrics.isDefined())
            for (PopulationMetric<T> metric : metrics.get())
                metric.measurePopulation(run, i, population);
        
        return new EvolutionResult<T>(population, bestSoFarInd, bestSoFarInd.getFitness());
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
    final public boolean repOK() {
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_GENERATORS != null
                && !P_GENERATORS.isEmpty()
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_IS_DYNAMIC != null
                && !P_IS_DYNAMIC.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_METRICS != null
                && !P_METRICS.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && generators != null
                && initializer != null
                && fitnessComparator != null
                && stoppingCondition != null
                && objective != null
                && metrics != null
                && !generators.isEmpty()
                && !Misc.containsNulls(generators)
                && !(metrics.isDefined() && Misc.containsNulls(metrics.get()));
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_IS_DYNAMIC, isDynamic,
                P_INITIALIZER, initializer,
                P_STOPPING_CONDITION, stoppingCondition,
                P_OBJECTIVE, objective,
                P_COMPARATOR, fitnessComparator,
                P_METRICS, metrics,
                P_GENERATORS, generators);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        final SimpleCircleOfLife ref = (SimpleCircleOfLife) o;
        return isDynamic == ref.isDynamic
                && initializer.equals(ref.initializer)
                && stoppingCondition.equals(ref.stoppingCondition)
                && generators.equals(ref.generators)
                && objective.equals(ref.objective)
                && fitnessComparator.equals(ref.fitnessComparator)
                && metrics.equals(ref.metrics);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.initializer != null ? this.initializer.hashCode() : 0);
        hash = 31 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 31 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 31 * hash + (this.metrics != null ? this.metrics.hashCode() : 0);
        hash = 31 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 31 * hash + (this.stoppingCondition != null ? this.stoppingCondition.hashCode() : 0);
        hash = 31 * hash + (this.isDynamic ? 1 : 0);
        return hash;
    }
    //</editor-fold>
}
