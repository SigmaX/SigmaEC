package SigmaEC;

import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import java.util.List;

/**
 * A basic evolutionary loop that mates parents, mutates offspring, and applies
 * selection.  An optional callback function (PopulationMetric) may be provided
 * to be run every generation (ex. for data collection purposes).
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual> implements CircleOfLife<T>
{
    final private Generator<T> matingGenerator;
    final private Generator<T> mutationGenerator;
    final private Selector<T> selector;
    final private PopulationMetric<T> metric;

    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, Selector<T> selector)
    {
        this.matingGenerator = matingGenerator;
        this.mutationGenerator = mutationGenerator;
        this.selector = selector;
        metric = null;
        assert(repOK());
    }
    
    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, Selector<T> selector, PopulationMetric<T> metric)
    {
        this.matingGenerator = matingGenerator;
        this.mutationGenerator = mutationGenerator;
        this.selector = selector;
        this.metric = metric;
        assert(repOK());
    }
    
    @Override
    public List<T> evolve(List<T> population, int generations)
    {
        for (int i = 0; i < generations; i++)
        {
            population = matingGenerator.produceGeneration(population);
            population = mutationGenerator.produceGeneration(population);
            population = selector.selectMultipleIndividuals(population, population.size());
            if (metric != null)
                metric.measurePopulation(population);
        }
        return population;
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
        return String.format("[SimpleCircleOfLife: MatingGenerator=%s, MutationGenerator=%s, Selector=%s]", matingGenerator, mutationGenerator, selector);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return matingGenerator.equals(cRef.matingGenerator)
                && mutationGenerator.equals(cRef.mutationGenerator)
                && selector.equals(cRef.selector)
                && ( (metric == null && cRef.metric == null)
                    || metric.equals(cRef.metric));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.matingGenerator != null ? this.matingGenerator.hashCode() : 0);
        hash = 97 * hash + (this.mutationGenerator != null ? this.mutationGenerator.hashCode() : 0);
        hash = 97 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 97 * hash + (this.metric != null ? this.metric.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
