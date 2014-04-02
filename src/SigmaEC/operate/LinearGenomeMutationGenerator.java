package SigmaEC.operate;

import SigmaEC.represent.Gene;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Takes a population of LinearGenomeIndividuals and mutates them according to
 * some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearGenomeMutationGenerator<T extends LinearGenomeIndividual<G>, G extends Gene> implements Generator<T>
{
    private final double mutationRate;
    private final Mutator<G> mutator;
    private final Random random;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    public LinearGenomeMutationGenerator(double mutationRate, Mutator<G> mutator, Random random)
    {
        this.mutationRate = mutationRate;
        this.mutator = mutator;
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
            List<G> genome = individual.getGenome();
            List<G> newGenome = new ArrayList<G>(genome.size());
            for (G g : genome)
            {
                double roll = random.nextDouble();
                newGenome.add((roll < mutationRate) ? mutator.mutate(g) : g);
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
        return Misc.doubleEquals(mutationRate, cRef.mutationRate)
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
