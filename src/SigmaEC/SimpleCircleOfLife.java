package SigmaEC;

import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.Generator;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A basic evolutionary loop that mates parents and mutates offspring.  An
 * optional callback function (PopulationMetric) may be provided to be run
 * every generation (ex. for data collection purposes).
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleCircleOfLife<T extends Individual> implements CircleOfLife<T>
{
    private Generator<T> matingGenerator;
    private Generator<T> mutationGenerator;
    private PopulationMetric<T> metric;

    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator)
    {
        this.matingGenerator = matingGenerator;
        this.mutationGenerator = mutationGenerator;
        assert(repOK());
    }
    
    public SimpleCircleOfLife(Generator<T> matingGenerator, Generator<T> mutationGenerator, PopulationMetric<T> metric)
    {
        this(matingGenerator, mutationGenerator);
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
                && mutationGenerator != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[SimpleCircleOfLife: MatingGenerator=%s, MutationGenerator=%s]", matingGenerator, mutationGenerator);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SimpleCircleOfLife))
            return false;
        
        SimpleCircleOfLife cRef = (SimpleCircleOfLife) o;
        return matingGenerator.equals(cRef.matingGenerator)
                && mutationGenerator.equals(cRef.mutationGenerator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.matingGenerator != null ? this.matingGenerator.hashCode() : 0);
        hash = 29 * hash + (this.mutationGenerator != null ? this.mutationGenerator.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
