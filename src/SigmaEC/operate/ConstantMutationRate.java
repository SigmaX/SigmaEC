package SigmaEC.operate;

import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class ConstantMutationRate extends MutationRate {
    public final static String P_RATE = "rate";
    
    private final double rate;
    
    public ConstantMutationRate(final Parameters params, final String base) {
        assert(params != null);
        assert(base != null);
        rate = params.getDoubleParameter(Parameters.push(base, P_RATE));
        assert(repOK());
    }
    
    @Override
    public double getRateForGene(final int gene, final int step, final LinearGenomeIndividual ind) {
        return rate;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_RATE != null
                && !P_RATE.isEmpty()
                && Double.isFinite(rate)
                && rate >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConstantMutationRate))
            return false;
        final ConstantMutationRate ref = (ConstantMutationRate)o;
        return Misc.doubleEquals(rate, ref.rate);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.rate) ^ (Double.doubleToLongBits(this.rate) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f]", this.getClass().getSimpleName(),
                P_RATE, rate);
    }
    // </editor-fold>
}
