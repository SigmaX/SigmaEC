package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.linear.Gene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Takes a population of LinearGenomeIndividuals and mutates individual genes
 * according to some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MutatingGenerator<T extends LinearGenomeIndividual<G>, G extends Gene> extends Generator<T> {
    private final static String P_MUTATION_RATE = "mutationRate";
    private final static String P_MUTATOR = "mutator";
    private final static String P_RANDOM = "random";
    
    private final double mutationRate;
    private final Mutator<G> mutator;
    private final Random random;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    public MutatingGenerator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        this.mutator = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATOR), Mutator.class);
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }

    @Override
    public List<T> produceGeneration(final List<T> parentPopulation) {
        final List<T> newPopulation = new ArrayList<T>(parentPopulation.size());
        for(int i = 0; i < parentPopulation.size(); i++) {
            final T individual = parentSelector.selectIndividual(parentPopulation);
            final List<G> genome = individual.getGenome();
            final List<G> newGenome = new ArrayList<G>(genome.size());
            for (final G g : genome) {
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
                && random != null
                && mutator != null
                && parentSelector != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: mutationRate=%.2f, random=%s, mutator=%s", this.getClass().getSimpleName(), mutationRate, random, mutator);
    }
    
    @Override
    public boolean equals(final Object o)
    {
        if (!(o instanceof MutatingGenerator))
            return false;
        
        MutatingGenerator cRef = (MutatingGenerator) o;
        return Misc.doubleEquals(mutationRate, cRef.mutationRate)
                && random.equals(cRef.random)
                && mutator.equals(cRef.mutator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 83 * hash + (this.mutator != null ? this.mutator.hashCode() : 0);
        hash = 83 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
