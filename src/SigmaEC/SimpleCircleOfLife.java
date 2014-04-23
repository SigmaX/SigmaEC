package SigmaEC;

import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Properties;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual> extends CircleOfLife<T>
{
    final private int numGenerations;
    final private List<Generator<T>> generators;
    final private Option<Selector<T>> parentSelector;
    final private Option<Selector<T>> survivalSelector;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Problem<T, ?> problem;
    
    private SimpleCircleOfLife(final Builder<T> builder)
    {
        this.numGenerations = builder.numGenerations;
        this.generators = builder.generators;
        this.parentSelector = builder.parentSelector;
        this.survivalSelector = builder.survivalSelector;
        this.preOperatorMetrics = builder.preOperatorMetrics;
        this.postOperatorMetrics = builder.postOperatorMetrics;
        this.problem = builder.problem;
        assert(repOK());
    }
    
    public static class Builder<T extends Individual> {
        final private static String P_NUM_GENERATIONS = "numGenerations";
        final private static String P_GENERATORS = "generators";
        final private static String P_PROBLEM = "problem";
        final private static String P_PARENT_SELECTOR = "parentSelector";
        final private static String P_SURVIVAL_SELECTOR = "survivalSelector";
        final private static String P_PRE_METRICS = "preMetrics";
        final private static String P_POST_METRICS = "postMetrics";
        
        private List<Generator<T>> generators;
        private Problem<T, ?> problem;
        
        private int numGenerations;
        private Option<Selector<T>> parentSelector = Option.NONE;
        private Option<Selector<T>> survivalSelector = Option.NONE;
        private Option<List<PopulationMetric<T>>> preOperatorMetrics = Option.NONE;
        private Option<List<PopulationMetric<T>>> postOperatorMetrics = Option.NONE;
        
        public Builder(final int numGenerations, final List<Generator<T>> generators, final Problem<T, ?> problem) {
            assert(generators != null);
            assert(problem != null);
            this.numGenerations = numGenerations;
            this.generators = generators;
            this.problem = problem;
        }
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.numGenerations = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_GENERATIONS));
            this.generators = Parameters.getInstancesFromParameter(properties, Parameters.push(base, P_GENERATORS), Generator.class);
            this.problem = Parameters.getInstanceFromParameter(properties, Parameters.push(base, P_PROBLEM), Problem.class);
            this.parentSelector = Parameters.getOptionalInstanceFromParameter(properties, Parameters.push(base, P_PARENT_SELECTOR), Selector.class);
            this.survivalSelector = Parameters.getOptionalInstanceFromParameter(properties, Parameters.push(base, P_SURVIVAL_SELECTOR), Selector.class);
            this.preOperatorMetrics = Parameters.getOptionalInstancesFromParameter(properties, Parameters.push(base, P_PRE_METRICS), Selector.class);
            this.postOperatorMetrics = Parameters.getOptionalInstancesFromParameter(properties, Parameters.push(base, P_POST_METRICS), Selector.class);
        }
        
        public SimpleCircleOfLife<T> build() {
            return new SimpleCircleOfLife(this);
        }
        
        public Builder<T> numGenerations(final int numGenerations) {
            assert(numGenerations > 0);
            this.numGenerations = numGenerations;
            return this;
        }
        
        public Builder<T> generators(final List<Generator<T>> generators) {
            assert(generators != null);
            assert(!Misc.containsNulls(generators));
            this.generators = generators;
            return this;
        }
        
        public Builder<T> problem(final Problem<T, ?> problem) {
            assert(problem != null);
            this.problem = problem;
            return this;
        }
        
        public Builder<T> parentSelector(final Selector<T> parentSelector) {
            if (parentSelector == null)
                this.parentSelector = Option.NONE;
            else
                this.parentSelector = new Option<Selector<T>>(parentSelector);
            return this;
        }
        
        public Builder<T> survivalSelector(final Selector<T> survivalSelector) {
            if (survivalSelector == null)
                this.survivalSelector = Option.NONE;
            else
                this.survivalSelector = new Option<Selector<T>>(survivalSelector);
            return this;
        }
        
        public Builder<T> preOperatorMetrics(final List<PopulationMetric<T>> preOperatorMetrics) {
            if (preOperatorMetrics == null)
                this.preOperatorMetrics = Option.NONE;
            else
                this.preOperatorMetrics = new Option<List<PopulationMetric<T>>>(preOperatorMetrics);
            return this;
        }
        
        public Builder<T> postOperatorMetrics(final List<PopulationMetric<T>> postOperatorMetrics) {
            if (postOperatorMetrics == null)
                this.postOperatorMetrics = Option.NONE;
            else
                this.postOperatorMetrics = new Option<List<PopulationMetric<T>>>(postOperatorMetrics);
            return this;
        }
    }
    
    @Override
    public List<T> evolve(final int run, List<T> population)
    {
        for (int i = 0; i < numGenerations; i++)
        {
            // Tell the problem what generation we're on (in case it's a dynamic landscape)
            problem.setGeneration(i);
            
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
            
            // Survival selection
            if (survivalSelector.isDefined())
                population = survivalSelector.get().selectMultipleIndividuals(population, population.size());
        }
        flushMetrics();
        return population;
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

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return generators != null
                && problem != null
                && preOperatorMetrics != null
                && postOperatorMetrics != null
                && !generators.isEmpty()
                && !Misc.containsNulls(generators)
                && !(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get()))
                && !(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get()));
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numGenerations=%d, problem=%s, parentSelector = %s, preMetrics=%s, generators=%s, postMetrics=%s, survivalSelector=%s]", this.getClass().getSimpleName(), numGenerations, problem, parentSelector, preOperatorMetrics, generators, postOperatorMetrics, survivalSelector);
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
                && problem.equals(cRef.problem)
                && ((parentSelector == null) ? cRef.parentSelector == null : parentSelector.equals(cRef.parentSelector))
                && ((survivalSelector == null) ? cRef.survivalSelector == null : survivalSelector.equals(cRef.survivalSelector))
                && ((preOperatorMetrics == null) ? cRef.preOperatorMetrics == null : preOperatorMetrics.equals(cRef.preOperatorMetrics))
                && ((postOperatorMetrics == null) ? cRef.postOperatorMetrics == null : postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.numGenerations;
        hash = 67 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 67 * hash + (this.parentSelector != null ? this.parentSelector.hashCode() : 0);
        hash = 67 * hash + (this.survivalSelector != null ? this.survivalSelector.hashCode() : 0);
        hash = 67 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 67 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 67 * hash + (this.problem != null ? this.problem.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
