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
import java.util.Random;

/**
 * Implements a random search algorithm.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RandomCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    final public static String P_EVALS_PER_GEN = "evalsPerGeneration";
    final public static String P_INITIALIZER = "initializer";
    final public static String P_COMPARATOR = "fitnessComparator";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_METRICS = "metrics";
    final public static String P_RANDOM = "random";
    public final static String P_IS_DYNAMIC = "isDynamic";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
        
    final private int evalsPerGeneration;
    final private Initializer<T> initializer;
    final private FitnessComparator<T> fitnessComparator;
    final private ObjectiveFunction<P> objective;
    final private Option<List<PopulationMetric<T>>> metrics;
    final private Random random;
    private final StoppingCondition<T> stoppingCondition;
    private final boolean isDynamic;
    
    public RandomCircleOfLife(final Parameters parameters, final String base) {
        this.evalsPerGeneration = parameters.getIntParameter(Parameters.push(base, P_EVALS_PER_GEN));
        this.initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        this.fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        this.metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        if (parameters.isDefined(Parameters.push(base, P_IS_DYNAMIC)))
            isDynamic = parameters.getBooleanParameter(Parameters.push(base, P_IS_DYNAMIC));
        else
            isDynamic = true;
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T> evolve(int run) {
        assert(run >= 0);
        reset();
        List<T> population = initializer.generatePopulation();
        T bestSoFarInd = null;
        int i = 0;
        while (!stoppingCondition.stop(population, i)) {
            // Tell the problem what generation we're on (in case it's a dynamic landscape)
            if (isDynamic)
                objective.setGeneration(i);
            
            // Take measurements
            if (metrics.isDefined())
                for (PopulationMetric<T> metric : metrics.get())
                    metric.measurePopulation(run, i, population);
            
            population = initializer.generatePopulation();
            
            // Update our local best-so-far variable
            final T bestOfGen = Statistics.max(population, fitnessComparator);
            if (fitnessComparator.betterThan(bestOfGen, bestSoFarInd)) 
                bestSoFarInd = bestOfGen;
            
            flushMetrics();
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
            for (PopulationMetric<T> metric : metrics.get())
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
        return evalsPerGeneration > 0
                && metrics != null
                && initializer != null
                && fitnessComparator != null
                && objective != null
                && random != null
                && !(metrics.isDefined() && Misc.containsNulls(metrics.get()))
                && stoppingCondition != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RandomCircleOfLife))
            return false;
        final RandomCircleOfLife ref = (RandomCircleOfLife) o;
        return evalsPerGeneration == ref.evalsPerGeneration
                && isDynamic == ref.isDynamic
                && initializer.equals(ref.initializer)
                && fitnessComparator.equals(ref.fitnessComparator)
                && objective.equals(ref.objective)
                && random.equals(ref.random)
                && metrics.equals(ref.metrics)
                && stoppingCondition.equals(ref.stoppingCondition);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.evalsPerGeneration;
        hash = 97 * hash + (this.initializer != null ? this.initializer.hashCode() : 0);
        hash = 97 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 97 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 97 * hash + (this.metrics != null ? this.metrics.hashCode() : 0);
        hash = 97 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 97 * hash + (this.stoppingCondition != null ? this.stoppingCondition.hashCode() : 0);
        hash = 97 * hash + (this.isDynamic ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_EVALS_PER_GEN, evalsPerGeneration,
                P_IS_DYNAMIC, isDynamic,
                P_INITIALIZER, initializer,
                P_COMPARATOR, fitnessComparator,
                P_METRICS, metrics,
                P_OBJECTIVE, objective,
                P_RANDOM, random,
                P_STOPPING_CONDITION, stoppingCondition);
    }
    // </editor-fold>
    
    
}
