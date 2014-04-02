package SigmaEC;

import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual> implements CircleOfLife<T>
{
    final private List<Generator<T>> generators;
    final private Option<Selector<T>> parentSelector;
    final private Option<Selector<T>> survivalSelector;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Problem<T, ?> problem;
    
    private SimpleCircleOfLife(final Builder<T> builder)
    {
        this.generators = builder.generators;
        this.parentSelector = builder.parentSelector;
        this.survivalSelector = builder.survivalSelector;
        this.preOperatorMetrics = builder.preOperatorMetrics;
        this.postOperatorMetrics = builder.postOperatorMetrics;
        this.problem = builder.problem;
        assert(repOK());
    }
    
    public static class Builder<T extends Individual> {
        final private List<Generator<T>> generators;
        final private Problem<T, ?> problem;
        
        private Option<Selector<T>> parentSelector = Option.NONE;
        private Option<Selector<T>> survivalSelector = Option.NONE;
        private Option<List<PopulationMetric<T>>> preOperatorMetrics = Option.NONE;
        private Option<List<PopulationMetric<T>>> postOperatorMetrics = Option.NONE;
        
        public Builder(final List<Generator<T>> generators, final Problem<T, ?> problem) {
            assert(generators != null);
            assert(problem != null);
            this.generators = generators;
            this.problem = problem;
        }
        
        public SimpleCircleOfLife<T> build() {
            return new SimpleCircleOfLife(this);
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
    public List<T> evolve(final int run, List<T> population, final int generations) throws IOException
    {
        for (int i = 0; i < generations; i++)
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
    private void flushMetrics() throws IOException
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
    final public boolean repOK()
    {
        return generators != null
                && problem != null
                && !generators.isEmpty()
                && preOperatorMetrics != null
                && postOperatorMetrics != null
                && !(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get()))
                && !(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get()));
    }
    
    @Override
    public String toString()
    {
        return String.format("[SimpleCircleOfLife: Generators=%s, Selector=%s, Metrics=%s, Problem=%s]", generators, survivalSelector, postOperatorMetrics, problem);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return generators.equals(cRef.generators)
                && problem.equals(cRef.problem)
                && ((survivalSelector == null) ? cRef.survivalSelector == null : survivalSelector.equals(cRef.survivalSelector))
                && ((preOperatorMetrics == null) ? cRef.preOperatorMetrics == null : preOperatorMetrics.equals(cRef.preOperatorMetrics))
                && ((postOperatorMetrics == null) ? cRef.postOperatorMetrics == null : postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 89 * hash + (this.survivalSelector != null ? this.survivalSelector.hashCode() : 0);
        hash = 89 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.problem != null ? this.problem.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
