package SigmaEC.util;

import java.util.Collection;
import java.util.Random;

/**
 * Miscellaneous utility methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Misc
{
    /** Private construct throws an error if called. */
    private Misc() throws AssertionError
    {
        throw new AssertionError("Misc: Cannot create instance of static class.");
    }
    
    /**
     * Returns false if c contains an object of any other type, true otherwise.
     * This uses getClass() to compare, not instanceof, so subtypes of type will
     * cause a false result.
     */
    public static boolean containsOnlyClass(final Collection c, final Class type)
    {
        assert(c != null);
        assert(type != null);
        for (Object o : c)
            if (o == null || o.getClass() != type)
                return false;
        return true;
    }
    
    /** returns true if c contains any null values. */
    public static boolean containsNulls(final Collection c)
    {
        assert(c != null);
        for (Object o : c)
            if (o == null)
                return true;
        return false;
    }
    
    /** Returns true if c contains any null values. */
    public static <T> boolean containsNulls(final T[] c)
    {
        assert(c != null);
        for (int i = 0; i < c.length; i++)
            if (c[i] == null)
                return true;
        return false;
    }
    
    /** Returns false if there are non-finite values in a vector. */
    public static boolean finiteValued(final double[] vector)
    {
        for (int i = 0; i < vector.length; i++)
        {
            double e = vector[i];
            if (e == Double.NEGATIVE_INFINITY || e == Double.POSITIVE_INFINITY || e == Double.NaN)
                return false;
        }
        return true;
    }
    
    /** Check that x &lt; y for all IDoublePoints in an array. */
    public static boolean boundsOK(final IDoublePoint[] bounds)
    {
        for (IDoublePoint p : bounds)
            if (p.x >= p.y)
                return false;
        return true;
    }
    
    /** Irwin-Hall approximation of a standard Gaussian distribution. */
    public static double gaussianSample(final Random random)
    {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += random.nextDouble();
        return sum - 6;
    }
    
    public static boolean doubleEquals(final double a, final double b)
    {
        return doubleEquals(a, b, 0.000001);
    }
    
    public static boolean doubleEquals(final double a, final double b, final double epsilon)
    {
        return Math.abs(a - b) < epsilon;
    }
    
    public static boolean doubleArrayEquals(final double[] a, final double[] b)
    {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
        {
            if (!doubleEquals(a[i], b[i]));
                return false;
            
        }
        return true;
    }
}
