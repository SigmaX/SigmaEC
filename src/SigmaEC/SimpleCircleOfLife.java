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
import java.util.Properties;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  Optionally takes one or more PopulationMetrics to be run every
 * generation for data collection purposes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual, P extends Phenotype> extends CircleOfLife<T>
{
    final private int numGenerations;
    final private List<Generator<T>> generators;
    final private Option<Selector<T>> parentSelector;
    final private Option<Selector<T>> survivalSelector;
    final private Option<List<PopulationMetric<T>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<T>>> postOperatorMetrics;
    final private Decoder<T, P> decoder;
    final private ObjectiveFunction<P> objective;
    
    private SimpleCircleOfLife(final Builder<T,P> builder)
    {
        this.numGenerations = builder.numGenerations;
        this.generators = builder.generators;
        this.parentSelector = builder.parentSelector;
        this.survivalSelector = builder.survivalSelector;
        this.preOperatorMetrics = builder.preOperatorMetrics;
        this.postOperatorMetrics = builder.postOperatorMetrics;
        this.decoder = builder.decoder;
        this.objective = builder.objective;
        assert(repOK());
    }
    
    public static class Builder<T extends Individual, P extends Phenotype> implements BuilderT<SimpleCircleOfLife<T,P>> {
        final private static String P_NUM_GENERATIONS = "numGenerations";
        final private static String P_GENERATORS = "generators";
        final private static String P_DECODER = "decoder";
        final private static String P_OBJECTIVE = "objective";
        final private static String P_PARENT_SELECTOR = "parentSelector";
        final private static String P_SURVIVAL_SELECTOR = "survivalSelector";
        final private static String P_PRE_METRICS = "preMetrics";
        final private static String P_POST_METRICS = "postMetrics";
        
        private int numGenerations;
        private List<Generator<T>> generators;
        private Option<Selector<T>> parentSelector = Option.NONE;
        private Option<Selector<T>> survivalSelector = Option.NONE;
        private Option<List<PopulationMetric<T>>> preOperatorMetrics = Option.NONE;
        private Option<List<PopulationMetric<T>>> postOperatorMetrics = Option.NONE;
        private Decoder<T, P> decoder;
        private ObjectiveFunction<P> objective;
        
        public Builder(final int numGenerations, final List<Generator<T>> generators, final Decoder<T, P> decoder, final ObjectiveFunction<P> objective) {
            assert(generators != null);
            assert(objective != null);
            this.numGenerations = numGenerations;
            this.generators = generators;
            this.decoder = decoder;
            this.objective = objective;
        }
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.numGenerations = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_GENERATIONS));
            this.generators = Parameters.getInstancesFromParameter(properties, Parameters.push(base, P_GENERATORS), Generator.class);
            this.decoder = Parameters.getInstanceFromParameter(properties, Parameters.push(base, P_DECODER), Decoder.class);
            this.objective = Parameters.getInstanceFromParameter(properties, Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
            
            final Option<BuilderT<Selector>> parentSelectorBuilder = Parameters.getOptionalBuilderFromParameter(properties, Parameters.push(base, P_PARENT_SELECTOR), Selector.class);
            if (parentSelectorBuilder.isDefined())
                this.parentSelector = new Option<Selector<T>>(((Selector.SelectorBuilder<T>)parentSelectorBuilder)
                        .decoder(decoder)
                        .objective(objective)
                        .build());
            else
                this.parentSelector = Option.NONE;
            
            final Option<BuilderT<Selector>> survivalSelectorBuilder = Parameters.getOptionalBuilderFromParameter(properties, Parameters.push(base, P_SURVIVAL_SELECTOR), Selector.class);
            if (survivalSelectorBuilder.isDefined())
                this.survivalSelector = new Option<Selector<T>>(((Selector.SelectorBuilder<T>)survivalSelectorBuilder)
                        .decoder(decoder)
                        .objective(objective)
                        .build());
            else
                this.survivalSelector = Option.NONE;
            
            this.preOperatorMetrics = Parameters.getOptionalInstancesFromParameter(properties, Parameters.push(base, P_PRE_METRICS), Selector.class);
            this.postOperatorMetrics = Parameters.getOptionalInstancesFromParameter(properties, Parameters.push(base, P_POST_METRICS), Selector.class);
        }
        
        @Override
        public SimpleCircleOfLife<T,P> build() {
            return new SimpleCircleOfLife(this);
        }
        
        public Builder<T,P> numGenerations(final int numGenerations) {
            assert(numGenerations > 0);
            this.numGenerations = numGenerations;
            return this;
        }
        
        public Builder<T,P> generators(final List<Generator<T>> generators) {
            assert(generators != null);
            assert(!Misc.containsNulls(generators));
            this.generators = generators;
            return this;
        }
        
        public Builder<T,P> problem(final ObjectiveFunction<P> objective) {
            assert(objective != null);
            this.objective = objective;
            return this;
        }
        
        public Builder<T,P> parentSelector(final Selector<T> parentSelector) {
            if (parentSelector == null)
                this.parentSelector = Option.NONE;
            else
                this.parentSelector = new Option<Selector<T>>(parentSelector);
            return this;
        }
        
        public Builder<T,P> survivalSelector(final Selector<T> survivalSelector) {
            if (survivalSelector == null)
                this.survivalSelector = Option.NONE;
            else
                this.survivalSelector = new Option<Selector<T>>(survivalSelector);
            return this;
        }
        
        public Builder<T,P> preOperatorMetrics(final List<PopulationMetric<T>> preOperatorMetrics) {
            if (preOperatorMetrics == null)
                this.preOperatorMetrics = Option.NONE;
            else
                this.preOperatorMetrics = new Option<List<PopulationMetric<T>>>(preOperatorMetrics);
            return this;
        }
        
        public Builder<T,P> postOperatorMetrics(final List<PopulationMetric<T>> postOperatorMetrics) {
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
