package SigmaEC;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Random;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual, P extends Phenotype> extends CircleOfLife<T>
{
    final public static String P_NUM_GENERATIONS = "numGenerations";
    final public static String P_GENERATORS = "generators";
    final public static String P_DECODER = "decoder";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_PARENT_SELECTOR = "parentSelector";
    final public static String P_SURVIVAL_SELECTOR = "survivalSelector";
    final public static String P_PRE_METRICS = "preMetrics";
    final public static String P_POST_METRICS = "postMetrics";
    final public static String P_RANDOM = "random";
        
    final private int numGenerations;
    final private List<Generator<T>> generators;
    final private Option<Selector<T>> parentSelector;
    final private Option<Selector<T>> survivalSelector;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Decoder<T, P> decoder;
    final private ObjectiveFunction<P> objective;
    final private Random random;
    
    public SimpleCircleOfLife(final Parameters parameters, final String base)
    {
        this.numGenerations = parameters.getIntParameter(Parameters.push(base, P_NUM_GENERATIONS));
        this.generators = parameters.getInstancesFromParameter(Parameters.push(base, P_GENERATORS), Generator.class);
        this.decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        this.parentSelector = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_PARENT_SELECTOR), Selector.class);
        this.survivalSelector = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_SURVIVAL_SELECTOR), Selector.class);
        this.preOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_PRE_METRICS), PopulationMetric.class);
        this.postOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_POST_METRICS), PopulationMetric.class);
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T> evolve(final int run, List<T> population)
    {
        T bestIndividual = null;
        for (int i = 0; i < numGenerations; i++)
        {
            // Tell the problem what generation we're on (in case it's a dynamic landscape)
            objective.setGeneration(i);
            
            // Parent selection
            if (parentSelector.isDefined())
                population = parentSelector.get().selectMultipleIndividuals(population, population.size());
            
            // Take measurements before operators
            if (preOperatorMetrics.isDefined())
                for (PopulationMetric<T> metric : preOperatorMetrics.get())
                    metric.measurePopulation(run, i, population);
            
            // Apply reproductive operators
            for (Generator<T> gen : generators)
                population = gen.produceGeneration(population);
            
            // Take measurements after operators
            if (postOperatorMetrics.isDefined())
                for (PopulationMetric<T> metric : postOperatorMetrics.get())
                    metric.measurePopulation(run, i, population);
            
            bestIndividual = getBestIndividual(bestIndividual, population);
            
            // Survival selection
            if (survivalSelector.isDefined())
                population = survivalSelector.get().selectMultipleIndividuals(population, population.size());
        }
        flushMetrics();
        return new EvolutionResult<T>(population, bestIndividual, objective.fitness(decoder.decode(bestIndividual)));
    }
    
    /** Flush I/O buffers. */
    private void flushMetrics()
    {
        if (preOperatorMetrics.isDefined())
            for (PopulationMetric<T> metric : preOperatorMetrics.get())
                metric.flush();
        if (postOperatorMetrics.isDefined())
            for (PopulationMetric<T> metric: postOperatorMetrics.get())
                metric.flush();
    }
    
    private T getBestIndividual(T bestIndividual, final List<T> population) {
        assert(population != null);
        double bestFitness = (bestIndividual == null) ? Double.NEGATIVE_INFINITY : objective.fitness(decoder.decode(bestIndividual));
        for (final T ind : population) {
            final double fitness = objective.fitness(decoder.decode(ind));
            if (fitness > bestFitness) { // XXX Hardcoding maximization
                bestIndividual = ind;
                bestFitness = fitness;
            }
        }
        return bestIndividual;               
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return generators != null
                && decoder != null
                && objective != null
                && preOperatorMetrics != null
                && postOperatorMetrics != null
                && !generators.isEmpty()
                && !Misc.containsNulls(generators)
                && !(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get()))
                && !(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get()));
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numGenerations=%d, decoder=%s, objective=%s, parentSelector = %s, preMetrics=%s, generators=%s, postMetrics=%s, survivalSelector=%s]", this.getClass().getSimpleName(), numGenerations, decoder, objective, parentSelector, preOperatorMetrics, generators, postOperatorMetrics, survivalSelector);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        final SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return numGenerations == cRef.numGenerations
                && generators.equals(cRef.generators)
                && decoder.equals(cRef.decoder)
                && objective.equals(cRef.objective)
                && ((parentSelector == null) ? cRef.parentSelector == null : parentSelector.equals(cRef.parentSelector))
                && ((survivalSelector == null) ? cRef.survivalSelector == null : survivalSelector.equals(cRef.survivalSelector))
                && ((preOperatorMetrics == null) ? cRef.preOperatorMetrics == null : preOperatorMetrics.equals(cRef.preOperatorMetrics))
                && ((postOperatorMetrics == null) ? cRef.postOperatorMetrics == null : postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.numGenerations;
        hash = 37 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 37 * hash + (this.parentSelector != null ? this.parentSelector.hashCode() : 0);
        hash = 37 * hash + (this.survivalSelector != null ? this.survivalSelector.hashCode() : 0);
        hash = 37 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 37 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 37 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 37 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
