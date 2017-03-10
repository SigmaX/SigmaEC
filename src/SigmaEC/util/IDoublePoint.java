package SigmaEC.util;

import SigmaEC.ContractObject;

/**
 * An immutable 2-dimensional point.
 * @author Eric 'Siggy' Scott
 */
public class IDoublePoint extends ContractObject {
    public final double x,y;
    public IDoublePoint(double x, double y) { this.x = x; this.y = y; }
    
    public double[] toDoubleArray() { return new double[] { x, y }; }

    @Override
    public boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IDoublePoint))
            return false;
        final IDoublePoint ref = (IDoublePoint)o;
        return Misc.doubleEquals(x, ref.x)
                && Misc.doubleEquals(y, ref.y);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
    
    @Override
    public String toString() { return String.format("[%s: x=%s, y=%s]", this.getClass().getSimpleName(), x, y); }
}
