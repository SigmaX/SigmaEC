package SigmaEC;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implements a random search algorithm.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RandomCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    final public static String P_NUM_GENERATIONS = "numGenerations";
    final public static String P_EVALS_PER_GEN = "evalsPerGeneration";
    final public static String P_INITIALIZER = "initializer";
    final public static String P_COMPARATOR = "fitnessComparator";
    final public static String P_DECODER = "decoder";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_PRE_METRICS = "preMetrics";
    final public static String P_POST_METRICS = "postMetrics";
    final public static String P_RANDOM = "random";
        
    final private int numGenerations;
    final private int evalsPerGeneration;
    final private Initializer<T> initializer;
    final private Comparator<Double> fitnessComparator;
    final private Decoder<T, P> decoder;
    final private ObjectiveFunction<P> objective;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Random random;

    
    public RandomCircleOfLife(final Parameters parameters, final String base) {
        this.numGenerations = parameters.getIntParameter(Parameters.push(base, P_NUM_GENERATIONS));
        this.evalsPerGeneration = parameters.getIntParameter(Parameters.push(base, P_EVALS_PER_GEN));
        this.initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        this.fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), Comparator.class);
        this.decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        this.preOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_PRE_METRICS), PopulationMetric.class);
        this.postOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_POST_METRICS), PopulationMetric.class);
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T> evolve(int run, List<T> population) {
        
        T bestIndividual = null;
        for (int i = 0; i < numGenerations; i++)
        {
            // Tell the problem what generation we're on (in case it's a dynamic landscape)
            objective.setGeneration(i);
            
            // Take measurements before operators
            if (preOperatorMetrics.isDefined())
                for (PopulationMetric<T> metric : preOperatorMetrics.get())
                    metric.measurePopulation(run, i, population);
            
            population = initializer.generatePopulation();
            
            bestIndividual = getBestIndividual(bestIndividual, population);
            
            // Take measurements after operators
            if (postOperatorMetrics.isDefined())
                for (PopulationMetric<T> metric : postOperatorMetrics.get())
                    metric.measurePopulation(run, i, population);
            
            flushMetrics();
        }
        return new EvolutionResult<T>(population, bestIndividual, objective.fitness(decoder.decode(bestIndividual)));
    }
    
    /** Flush I/O buffers. */
    private void flushMetrics() {
        if (preOperatorMetrics.isDefined())
            for (PopulationMetric<T> metric : preOperatorMetrics.get())
                metric.flush();
        if (postOperatorMetrics.isDefined())
            for (PopulationMetric<T> metric: postOperatorMetrics.get())
                metric.flush();
    }
    
    private T getBestIndividual(T bestIndividual, final List<T> population) {
        assert(population != null);
        double bestFitness = (bestIndividual == null) ? objective.fitness(decoder.decode(population.get(0))) : objective.fitness(decoder.decode(bestIndividual));
        for (final T ind : population) {
            final double fitness = objective.fitness(decoder.decode(ind));
            if (fitnessComparator.compare(fitness, bestFitness) > 0) {
                bestIndividual = ind;
                bestFitness = fitness;
            }
        }
        return bestIndividual;               
    }
    
    @Override
    public void reset() {
        for (final PopulationMetric<T> metric : preOperatorMetrics.get())
                metric.reset();
        for (final PopulationMetric<T> metric : postOperatorMetrics.get())
                metric.reset();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numGenerations > 0
                && evalsPerGeneration > 0
                && preOperatorMetrics != null
                && initializer != null
                && fitnessComparator != null
                && decoder != null
                && objective != null
                && random != null
                && !(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get()))
                && !(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get()));
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RandomCircleOfLife))
            return false;
        final RandomCircleOfLife ref = (RandomCircleOfLife) o;
        return numGenerations == ref.numGenerations
                && evalsPerGeneration == ref.evalsPerGeneration
                && initializer.equals(ref.initializer)
                && fitnessComparator.equals(ref.fitnessComparator)
                && decoder.equals(ref.decoder)
                && objective.equals(ref.objective)
                && random.equals(ref.random)
                && preOperatorMetrics.equals(ref.preOperatorMetrics)
                && postOperatorMetrics.equals(ref.postOperatorMetrics);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.numGenerations;
        hash = 47 * hash + this.evalsPerGeneration;
        hash = 47 * hash + (this.initializer != null ? this.initializer.hashCode() : 0);
        hash = 47 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 47 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 47 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 47 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 47 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 47 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numGenerations=%d, evalsPerGeneration=%d, initializer=%s, fitnessComparator=%s, preOperatorMetrics=%s, postOperatorMetrics=%s, decoder=%s, objective=%s, random=%s]", this.getClass().getSimpleName(), numGenerations, evalsPerGeneration, initializer, fitnessComparator, preOperatorMetrics, postOperatorMetrics, decoder, objective, random);
    }
    // </editor-fold>
    
    
}
