package SigmaEC;

import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
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
    final private Selector<T> selector;
    final private List<PopulationMetric<T>> preOperatorMetrics;
    final private List<PopulationMetric<T>> postOperatorMetrics;
    final private Problem<T, ?> problem;

    public SimpleCircleOfLife(final List<Generator<T>> generators,
                                final Selector<T> selector,
                                final Problem<T, ?> problem)
    {
        this.generators = generators;
        this.selector = selector;
        this.preOperatorMetrics = null;
        this.postOperatorMetrics = null;
        this.problem = problem;
        assert(repOK());
    }
    
    public SimpleCircleOfLife(final List<Generator<T>> generators,
                                final Selector<T> selector,
                                final PopulationMetric<T> postMetric,
                                final Problem<T, ?> problem)
    {
        this(generators, selector, null, new ArrayList<PopulationMetric<T>>() {{ add(postMetric); }}, problem);
    }
    
    public SimpleCircleOfLife(final List<Generator<T>> generators,
                                final Selector<T> selector,
                                final List<PopulationMetric<T>> preOperatorMetrics,
                                final List<PopulationMetric<T>> postOperatorMetrics,
                                final Problem<T, ?> problem)
    {
        this.generators = generators;
        this.selector = selector;
        this.preOperatorMetrics = preOperatorMetrics;
        this.postOperatorMetrics = postOperatorMetrics;
        this.problem = problem;
        assert(repOK());
    }
    
    @Override
    public List<T> evolve(final int run, List<T> population, final int generations) throws IOException
    {
        for (int i = 0; i < generations; i++)
        {
            // Tell the problem what generation we're on (in case it's a dynamic landscape)
            problem.setGeneration(i);
            
            // Take measurements before operators
            if (preOperatorMetrics != null)
                for (PopulationMetric<T> metric : preOperatorMetrics)
                    metric.measurePopulation(run, i, population);
            
            // Apply operators
            for (Generator<T> gen : generators)
                population = gen.produceGeneration(population);
            
            // Take measurements after operators
            if (postOperatorMetrics != null)
                for (PopulationMetric<T> metric : postOperatorMetrics)
                    metric.measurePopulation(run, i, population);
            
            // Survival selection
            if (selector != null)
                population = selector.selectMultipleIndividuals(population, population.size());
        }
        flushMetrics();
        return population;
    }
    
    /** Flush I/O buffers. */
    private void flushMetrics() throws IOException
    {
        if (preOperatorMetrics != null)
            for (PopulationMetric<T> metric : preOperatorMetrics)
                metric.flush();
        if (postOperatorMetrics != null)
            for (PopulationMetric<T> metric: postOperatorMetrics)
                metric.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return generators != null
                && problem != null
                && !generators.isEmpty();
    }
    
    @Override
    public String toString()
    {
        return String.format("[SimpleCircleOfLife: Generators=%s, Selector=%s, Metrics=%s, Problem=%s]", generators, selector, postOperatorMetrics, problem);
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
                && ((selector == null) ? cRef.selector == null : selector.equals(cRef.selector))
                && ((preOperatorMetrics == null) ? cRef.preOperatorMetrics == null : preOperatorMetrics.equals(cRef.preOperatorMetrics))
                && ((postOperatorMetrics == null) ? cRef.postOperatorMetrics == null : postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.generators != null ? this.generators.hashCode() : 0);
        hash = 89 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 89 * hash + (this.preOperatorMetrics != null ? this.preOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        hash = 89 * hash + (this.problem != null ? this.problem.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
