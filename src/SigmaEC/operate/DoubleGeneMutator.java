package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.DoubleGene;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Random;

/**
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneMutator extends Mutator<DoubleGene> {
    private final static String P_GAUSSIAN_STD = "gaussianStd";
    private final static String P_RANDOM = "random";
    final private Random random;
    final private double gaussianStd;
    
    public DoubleGeneMutator(final Parameters parameters, final String base) {
        this.gaussianStd = parameters.getDoubleParameter(Parameters.push(base, P_GAUSSIAN_STD));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        
        if (Double.isInfinite(gaussianStd) || Double.isNaN(gaussianStd))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": gaussianStd is infinite, must be finite.");
        if (gaussianStd <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": gaussianStd is <= 0, must be positive.");
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
        assert(repOK());
    }
    
    @Override
    public DoubleGene mutate(final DoubleGene gene) {
        return new DoubleGene(gene.value + Misc.gaussianSample(random)*gaussianStd);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return random != null
                && !Double.isNaN(gaussianStd)
                && !Double.isInfinite(gaussianStd)
                && gaussianStd > 0;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: GaussianStd=%f, Random=%s]", this.getClass().getSimpleName(), gaussianStd, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneMutator))
            return false;
        
        DoubleGeneMutator cRef = (DoubleGeneMutator) o;
        return Misc.doubleEquals(gaussianStd, cRef.gaussianStd)
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.gaussianStd) ^ (Double.doubleToLongBits(this.gaussianStd) >>> 32));
        return hash;
    }
    //</editor-fold>
}
