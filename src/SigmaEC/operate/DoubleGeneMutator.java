package SigmaEC.operate;

import SigmaEC.represent.DoubleGene;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Properties;
import java.util.Random;

/**
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneMutator extends Mutator<DoubleGene>
{
    final private Random random;
    final private double gaussianStd;
    
    private DoubleGeneMutator(final Builder builder) {
        this.gaussianStd = builder.gaussianStd;
        this.random = builder.random;
        if (Double.isInfinite(gaussianStd) || Double.isNaN(gaussianStd))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": gaussianStd is infinite, must be finite.");
        if (gaussianStd <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": gaussianStd is <= 0, must be positive.");
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
        assert(repOK());
    }
    
    public static class Builder implements MutatorBuilder<DoubleGene> {
        private final static String P_GAUSSIAN_STD = "gaussianStd";
        
        private double gaussianStd;
        private Random random;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.gaussianStd = Parameters.getDoubleParameter(properties, Parameters.push(base, P_GAUSSIAN_STD));
        }
        
        @Override
        public MutatorBuilder<DoubleGene> random(final Random random) {
            assert(random != null);
            this.random = random;
            return this;
        }

        @Override
        public Mutator<DoubleGene> build() {
            if (random == null)
                throw new IllegalStateException(this.getClass().getSimpleName() + ": trying to build before random has been initialized.");
            return new DoubleGeneMutator(this);
        }
        
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
