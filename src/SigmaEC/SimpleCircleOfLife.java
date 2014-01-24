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
    final private Generator<T> matingGenerator;
    final private Generator<T> mutationGenerator;
    final private Selector<T> selector;
    final private List<PopulationMetric<T>> preOperatorMetrics;
    final private List<PopulationMetric<T>> postOperatorMetrics;

    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, Selector<T> selector)
    {
        this.matingGenerator = matingGenerator;
        this.mutationGenerator = mutationGenerator;
        this.selector = selector;
        preOperatorMetrics = null;
        postOperatorMetrics = null;
        assert(repOK());
    }
    
    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, Selector<T> selector, List<PopulationMetric<T>> preOperatorMetrics, List<PopulationMetric<T>> postOperatorMetrics)
    {
        this.matingGenerator = matingGenerator;
        this.mutationGenerator = mutationGenerator;
        this.selector = selector;
        this.preOperatorMetrics = preOperatorMetrics;
        this.postOperatorMetrics = postOperatorMetrics;
        assert(repOK());
    }
    
    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, Selector<T> selector, final PopulationMetric<T> postMetric)
    {
        this(matingGenerator, mutationGenerator, selector, null, new ArrayList<PopulationMetric<T>>() {{ add(postMetric); }});
    }
    
    @Override
    public List<T> evolve(int run, List<T> population, int generations) throws IOException
    {
        for (int i = 0; i < generations; i++)
        {
            if (preOperatorMetrics != null)
                for (PopulationMetric<T> metric : preOperatorMetrics)
                    metric.measurePopulation(run, i, population);
            
            // Apply operators
            population = matingGenerator.produceGeneration(population);
            population = mutationGenerator.produceGeneration(population);
            
            if (postOperatorMetrics != null)
                for (PopulationMetric<T> metric : postOperatorMetrics)
                    metric.measurePopulation(run, i, population);
            
            // Apply selection
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
        return matingGenerator != null
                && mutationGenerator != null
                && selector != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[SimpleCircleOfLife: MatingGenerator=%s, MutationGenerator=%s, Selector=%s, Metrics=%s]", matingGenerator, mutationGenerator, selector, postOperatorMetrics);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return matingGenerator.equals(cRef.matingGenerator)
                && mutationGenerator.equals(cRef.mutationGenerator)
                && selector.equals(cRef.selector)
                && ( (postOperatorMetrics == null && cRef.postOperatorMetrics == null)
                    || postOperatorMetrics.equals(cRef.postOperatorMetrics));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.matingGenerator != null ? this.matingGenerator.hashCode() : 0);
        hash = 97 * hash + (this.mutationGenerator != null ? this.mutationGenerator.hashCode() : 0);
        hash = 97 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 97 * hash + (this.postOperatorMetrics != null ? this.postOperatorMetrics.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
