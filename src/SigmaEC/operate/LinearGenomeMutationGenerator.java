package SigmaEC.operate;

import SigmaEC.represent.Gene;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Takes a population of LinearGenomeIndividuals and mutates them according to
 * some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearGenomeMutationGenerator<T extends LinearGenomeIndividual> implements Generator<T>
{
    private final double mutationRate;
    private final Random random;
    private final Selector<T> parentSelector = new IterativeSelector<T>();

    @Override
    public Selector getParentSelector()
    {
        return parentSelector;
    }
    
    public LinearGenomeMutationGenerator(double mutationRate, Random random)
    {
        this.mutationRate = mutationRate;
        this.random = random;
        assert(repOK());
    }

    @Override
    public List<T> produceGeneration(List<T> parentPopulation)
    {
        List<T> newPopulation = new ArrayList<T>(parentPopulation.size());
        for(int i = 0; i < parentPopulation.size(); i++)
        {
            T individual = parentSelector.selectIndividual(parentPopulation);
            List<Gene> genome = individual.getGenome();
            List<Gene> newGenome = new ArrayList<Gene>(genome.size());
            for (Gene g : genome)
            {
                double roll = random.nextDouble();
                newGenome.add((roll < mutationRate) ? g.mutate() : g);
            }
            newPopulation.add((T) (parentPopulation.get(0).create(newGenome)));
        }
        return newPopulation;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return mutationRate >= 0.0
                && mutationRate <= 1.0
                && random != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[LinearGenomeMutatingGenerator: MutationRate=%.2f, Random=%s", mutationRate, random);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof LinearGenomeMutationGenerator))
            return false;
        
        LinearGenomeMutationGenerator cRef = (LinearGenomeMutationGenerator) o;
        return mutationRate == cRef.mutationRate
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 97 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
