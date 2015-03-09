package SigmaEC;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.FitnessComparator;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.Comparator;
import java.util.List;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual, P> extends CircleOfLife<T> {
    private final static String P_INITIALIZER = "initializer";
    final public static String P_GENERATORS = "generators";
    final public static String P_DECODER = "decoder";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_COMPARATOR = "fitnessComparator";
    final public static String P_PARENT_SELECTOR = "parentSelector";
    final public static String P_PRE_METRICS = "preMetrics";
    final public static String P_POST_METRICS = "postMetrics";
    final public static String P_RANDOM = "random";
    final public static String P_IS_DYNAMIC = "isDynamic";
    final public static String P_NUM_GENERATIONS = "numGenerations";
    final public static String P_NUM_GENS_WITHOUT_IMPROVEMENT = "numGensWithoutImprovement";
        
    final private Option<Integer> numGenerations;
    final private Option<Integer> numGensWithoutImprovement;
    
    private final Initializer<T> initializer;
    final private List<Generator<T>> generators;
    final private FitnessComparator<T, P> fitnessComparator;
    final private Option<Selector<T>> parentSelector;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Decoder<T, P> decoder;
    final private ObjectiveFunction<P> objective;
    final private boolean isDynamic;
    
    public SimpleCircleOfLife(final Parameters parameters, final String base) {
        this.numGenerations = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_GENERATIONS));
        this.numGensWithoutImprovement = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_GENS_WITHOUT_IMPROVEMENT));
        if (numGenerations.isDefined() && numGensWithoutImprovement.isDefined())
            throw new IllegalStateException(String.format("%s: both of the parameters '%s' and '%s' are defined.  Must choose one or the other.", this.getClass().getSimpleName(), Parameters.push(base, P_NUM_GENERATIONS), Parameters.push(base, P_NUM_GENS_WITHOUT_IMPROVEMENT)));
        if (!numGenerations.isDefined() && !numGensWithoutImprovement.isDefined())
            throw new IllegalStateException(String.format("%s: neither of the parameters '%s' and '%s' are defined.  Need one.", this.getClass().getSimpleName(), Parameters.push(base, P_NUM_GENERATIONS), Parameters.push(base, P_NUM_GENS_WITHOUT_IMPROVEMENT)));
        this.initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        this.generators = parameters.getInstancesFromParameter(Parameters.push(base, P_GENERATORS), Generator.class);
        this.decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        this.fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        this.parentSelector = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_PARENT_SELECTOR), Selector.class);
        this.preOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_PRE_METRICS), PopulationMetric.class);
        this.postOperatorMetrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_POST_METRICS), PopulationMetric.class);
        if (parameters.isDefined(Parameters.push(base, P_IS_DYNAMIC)))
            this.isDynamic = parameters.getBooleanParameter(Parameters.push(base, P_IS_DYNAMIC));
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
        while (!stop(i, bestSoFarInd)) {
            
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
            
            // Tell the problem what generation we're on (if it's a dynamic landscape)
            if (isDynamic)
                objective.setGeneration(i);
            
            // Clear any cache in the genotype-phenotype mapping, so we'll re-compute clones (in case the G-P map is stochastic).
            decoder.reset();
            
            final T bestOfGen = Statistics.max(population, fitnessComparator);
            if (fitnessComparator.betterThan(bestOfGen, bestSoFarInd)) 
                bestSoFarInd = bestOfGen;
            
            // Take measurements after operators
            if (postOperatorMetrics.isDefined())
                for (PopulationMetric<T> metric : postOperatorMetrics.get())
                    metric.measurePopulation(run, i, population);
            
            flushMetrics();
            i++;
        }
        return new EvolutionResult<T>(population, bestSoFarInd, objective.fitness(decoder.decode(bestSoFarInd)));
    }
    
    private T previousBestSoFar = null;
    private int gensPassedWithNoImprovement = 0;
    private boolean stop(final int generation, final T bestSoFar) {
        assert(generation >= 0);
        if (generation == 0)
            return false;
        assert(bestSoFar != null);
        if (numGenerations.isDefined())
            return generation >= numGenerations.get();
        else if (numGensWithoutImprovement.isDefined()) {
            assert(-1 != fitnessComparator.compare(bestSoFar, previousBestSoFar));
            if (fitnessComparator.betterThan(bestSoFar, previousBestSoFar) ) {
                previousBestSoFar = bestSoFar;
                gensPassedWithNoImprovement = 0;
            }
            else
                gensPassedWithNoImprovement++;
            return (gensPassedWithNoImprovement >= numGensWithoutImprovement.get());
        }
        else
            throw new IllegalStateException();
    }
    
    /** Flush I/O buffers. */
    private void flushMetrics() {
        if (preOperatorMetrics.isDefined())
            for (final PopulationMetric<T> metric : preOperatorMetrics.get())
                metric.flush();
        if (postOperatorMetrics.isDefined())
            for (final PopulationMetric<T> metric: postOperatorMetrics.get())
                metric.flush();
    }
    
    private void reset() {
        previousBestSoFar = null;
        gensPassedWithNoImprovement = 0;
    
        if (preOperatorMetrics.isDefined())
            for (final PopulationMetric<T> metric : preOperatorMetrics.get())
                    metric.reset();
        if (postOperatorMetrics.isDefined())
            for (final PopulationMetric<T> metric : postOperatorMetrics.get())
                    metric.reset();
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
        return String.format("[%s: isDynamic=%s, numGenerations=%s, numGensWithoutImprovement=%s, decoder=%s, objective=%s, parentSelector = %s, fitnessComparator=%s, preMetrics=%s, generators=%s, postMetrics=%s]", this.getClass().getSimpleName(), isDynamic, numGenerations, numGensWithoutImprovement, decoder, objective, parentSelector, fitnessComparator, preOperatorMetrics, generators, postOperatorMetrics);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        final SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return isDynamic == cRef.isDynamic
                && numGenerations.equals(cRef.numGenerations)
                && numGensWithoutImprovement.equals(cRef.numGensWithoutImprovement)
                && generators.equals(cRef.generators)
                && decoder.equals(cRef.decoder)
                && objective.equals(cRef.objective)
                && fitnessComparator.equals(cRef.fitnessComparator)
                && ((parentSelector == null) ? cRef.parentSelector == null : parentSelector.equals(cRef.parentSelector))
                && ((preOperatorMetrics == null) ? cRef.preOperatorMetrics == null : preOperatorMetrics.equals(cRef.preOperatorMetrics))
                && ((postOperatorMetrics == null) ? cRef.postOperatorMetrics == null : postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.numGenerations != null ? this.numGenerations.hashCode() : 0);
        hash = 89 * hash + (this.numGensWithoutImprovement != null ? this.numGensWithoutImprovement.hashCode() : 0);
        hash = 89 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 89 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 89 * hash + (this.parentSelector != null ? this.parentSelector.hashCode() : 0);
        hash = 89 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 89 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 89 * hash + (this.isDynamic ? 1 : 0);
        return hash;
    }
    //</editor-fold>
}
