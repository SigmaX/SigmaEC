package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.linear.BitGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Bit flip mutation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitGeneMutator extends Mutator<LinearGenomeIndividual<BitGene>, BitGene> {
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    
    private final double mutationRate;
    private final Random random;
    
    public BitGeneMutator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        if (Double.isInfinite(mutationRate) || Double.isNaN(mutationRate))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (mutationRate < 0 || mutationRate > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be in the range [0, 1.0].", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        assert(repOK());
    }
    
    private BitGene mutate(final BitGene gene) {
        return new BitGene(!gene.value);
    }

    @Override
    public LinearGenomeIndividual<BitGene> mutate(final LinearGenomeIndividual<BitGene> ind) {
        assert(ind != null);
        final List<BitGene> genome = ind.getGenome();
        final List<BitGene> newGenome = new ArrayList<BitGene>();
        for (final BitGene g : genome) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate) ? mutate(g) : g);
        }
        assert(repOK());
        return ind.create(newGenome);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return random != null
                && !Double.isInfinite(mutationRate)
                && !Double.isNaN(mutationRate)
                && mutationRate >= 0
                && mutationRate <= 1.0;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_MUTATION_RATE, mutationRate,
                P_RANDOM, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (!(o instanceof BitGeneMutator))
            return false;
        final BitGeneMutator ref = (BitGeneMutator)o;
        return random.equals(ref.random)
                && Misc.doubleEquals(mutationRate, ref.mutationRate);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 17 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
