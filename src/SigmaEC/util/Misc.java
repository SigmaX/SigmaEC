package SigmaEC.util;

import java.util.Collection;
import java.util.Random;

/**
 * Miscellaneous utility methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public class Misc
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
    public static boolean containsOnlyClass(Collection c, Class type)
    {
        assert(c != null);
        assert(type != null);
        for (Object o : c)
            if (o == null || o.getClass() != type)
                return false;
        return true;
    }
    
    /** returns true if c contains any null values. */
    public static boolean containsNulls(Collection c)
    {
        assert(c != null);
        for (Object o : c)
            if (o == null)
                return true;
        return false;
    }
    
    /** Returns true if c contains any null values. */
    public static <T> boolean containsNulls(T[] c)
    {
        assert(c != null);
        for (int i = 0; i < c.length; i++)
            if (c[i] == null)
                return true;
        return false;
    }
    
    /** Returns false if there are non-finite values in a vector. */
    public static boolean finiteValued(double[] vector)
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
    public static boolean boundsOK(IDoublePoint[] bounds)
    {
        for (IDoublePoint p : bounds)
            if (p.x >= p.y)
                return false;
        return true;
    }
    
    /** Irwin-Hall approximation of a standard Gaussian distribution. */
    public static double gaussianSample(Random random)
    {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += random.nextDouble();
        return sum - 6;
    }
}
