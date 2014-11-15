package SigmaEC.represent.linear;

import SigmaEC.util.Misc;

/**
 * A Gene that wraps a single double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGene implements Gene {
    final public double value;
    public DoubleGene(double value) { this.value = value; assert(repOK()); }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    
    public final boolean repOK() {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleGene))
            return false;
        final DoubleGene ref = (DoubleGene) o;
        return Misc.doubleEquals(value, ref.value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: value=%f]", this.getClass().getSimpleName(), value);
    }
    // </editor-fold>
}
