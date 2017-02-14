package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.BitGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Bit flip mutation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitGeneMutator extends Mutator<LinearGenomeIndividual<BitGene, ?>> {
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    
    private final MutationRate mutationRate;
    private final Random random;
    
    public BitGeneMutator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        mutationRate = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATION_RATE), MutationRate.class);
        assert(repOK());
    }
    
    private BitGene mutate(final BitGene gene) {
        return new BitGene(!gene.value);
    }
 
    /** Takes an individual and produces a version of that individual with zero or more mutated genes.
     * 
     * The new individual has bit-flip mutation applied to each gene with
     * probability P_MUTATION_RATE.
     * 
     * If the old individual has parents, then
     * the new individual's parents are the same the original's parents---that
     * is, ind is not considered the parent of the mutated individual.
     * 
     * If ind's parents attribute is empty, however, then the new individual
     * considered ind to be its parent.
     * 
     * This way, an offspring individual has its parents assigned by the first
     * reproductive operator that is applied to its parents.
     * 
     * @param ind The original individual.
     * @return The newly mutated individual.
     */
    @Override
    public LinearGenomeIndividual<BitGene, ?> mutate(final LinearGenomeIndividual<BitGene, ?> ind, final int step) {
        assert(ind != null);
        final List<BitGene> genome = ind.getGenome();
        final List<BitGene> newGenome = new ArrayList<BitGene>();
        for (int i = 0; i < genome.size(); i++) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate.getRateForGene(i, step, ind)) ? mutate(genome.get(i)) : genome.get(i));
        }
        final List<? extends Individual> parents = ind.hasParents() ?
                ind.getParents().get() :
                new ArrayList<Individual>() {{ add(ind); }};
        assert(repOK());
        return ind.create(newGenome, parents);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return random != null
                && mutationRate != null;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MUTATION_RATE, mutationRate,
                P_RANDOM, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (!(o instanceof BitGeneMutator))
            return false;
        final BitGeneMutator ref = (BitGeneMutator)o;
        return random.equals(ref.random)
                && mutationRate.equals(ref.mutationRate);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.mutationRate);
        hash = 73 * hash + Objects.hashCode(this.random);
        return hash;
    }
    //</editor-fold>
}
