package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.linear.DoubleGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneMutator extends Mutator<LinearGenomeIndividual<DoubleGene>, DoubleGene> {
    private final static String P_GAUSSIAN_STD = "gaussianStd";
    private final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    
    private final double mutationRate;
    final private Random random;
    final private double gaussianStd;
    
    public DoubleGeneMutator(final Parameters parameters, final String base) {
        this.gaussianStd = parameters.getDoubleParameter(Parameters.push(base, P_GAUSSIAN_STD));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        if (!Double.isFinite(mutationRate))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (mutationRate < 0 || mutationRate > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be in the range [0, 1.0].", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (Double.isInfinite(gaussianStd) || Double.isNaN(gaussianStd))
            throw new IllegalStateException(String.format("%s: %s is infinite, must be finite.", this.getClass().getSimpleName(), P_GAUSSIAN_STD));
        if (gaussianStd <= 0)
            throw new IllegalStateException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_GAUSSIAN_STD));
        if (random == null)
            throw new IllegalStateException(String.format("%s: %s is null.", this.getClass().getSimpleName(), P_RANDOM));
        assert(repOK());
    }
    
    @Override
    public DoubleGene mutate(final DoubleGene gene) {
        return new DoubleGene(gene.value + Misc.gaussianSample(random)*gaussianStd);
    }

    @Override
    public LinearGenomeIndividual<DoubleGene> mutate(final LinearGenomeIndividual<DoubleGene> ind) {
        assert(ind != null);
        final List<DoubleGene> genome = ind.getGenome();
        final List<DoubleGene> newGenome = new ArrayList<DoubleGene>();
        for (final DoubleGene g : genome) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate) ? mutate(g) : g);
        }
        assert(repOK());
        return ind.create(newGenome);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_MUTATION_RATE != null
                && !P_MUTATION_RATE.isEmpty()
                && P_GAUSSIAN_STD != null
                && !P_GAUSSIAN_STD.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && random != null
                && Double.isFinite(mutationRate)
                && mutationRate >= 0.0
                && mutationRate <= 1.0
                && !Double.isNaN(gaussianStd)
                && !Double.isInfinite(gaussianStd)
                && gaussianStd > 0;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%s]", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate, P_GAUSSIAN_STD, gaussianStd, P_RANDOM, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneMutator))
            return false;
        
        DoubleGeneMutator ref = (DoubleGeneMutator) o;
        return Misc.doubleEquals(gaussianStd, ref.gaussianStd)
                && Misc.doubleEquals(mutationRate, ref.mutationRate)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 23 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.gaussianStd) ^ (Double.doubleToLongBits(this.gaussianStd) >>> 32));
        return hash;
    }
    //</editor-fold>
}
