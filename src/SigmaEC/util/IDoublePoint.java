package SigmaEC.util;

/**
 * An immutable 2-dimensional point.
 * @author Eric 'Siggy' Scott
 */
public class IDoublePoint
{
    public final double x,y;
    public IDoublePoint(double x, double y) { this.x = x; this.y = y; }
    @Override
    public String toString() { return String.format("[%s: x=%s, y=%s]", this.getClass().getSimpleName(), x, y); }
    
    public double[] toDoubleArray() { return new double[] { x, y }; }
}
