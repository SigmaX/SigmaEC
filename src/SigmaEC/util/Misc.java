package SigmaEC.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Miscellaneous utility methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Misc
{
    /** Private constructor throws an error if called. */
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
    
    /** returns true if c contains any NaN values. */
    public static boolean containsNaNs(final double[] c)
    {
        assert(c != null);
        for (int i = 0; i < c.length; i++)
            if (Double.isNaN(c[i]))
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
    
    public static double[][] deepCopy2DArray(final double[][] original) {
        final double[][] newArray = new double[original.length][];
        for (int i = 0; i < original.length; i++)
            newArray[i] = Arrays.copyOf(original[i], original[i].length);
        return newArray;
    }
    
    /** Returns false if there are non-finite values in a vector. */
    public static boolean finiteValued(final double[] vector)
    {
        for (int i = 0; i < vector.length; i++)
        {
            double e = vector[i];
            if (e == Double.NEGATIVE_INFINITY || e == Double.POSITIVE_INFINITY || Double.isNaN(e))
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
    public static double gaussianSample(final Random random) {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += random.nextDouble();
        return sum - 6;
    }
    
    public static boolean doubleEquals(final double a, final double b) {
        return doubleEquals(a, b, 0.000001);
    }
    
    public static boolean doubleEquals(final double a, final double b, final double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
    
    public static boolean doubleArrayEquals(final double[] a, final double[] b) {
        assert(a != null);
        assert(b != null);
        if (a == b)
            return true;
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++) {
            if (!doubleEquals(a[i], b[i]))
                return false;
        }
        return true;
    }
    
    public static boolean listOfDoubleArraysEquals(final List<double[]> a, final List<double[]> b) {
        assert(a != null);
        assert(b != null);
        if (a == b)
            return true;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (!doubleArrayEquals(a.get(i), b.get(i)))
                return false;
        }
        return true;
    }
    
    public static boolean allFinite(final double[] a) {
        assert(a != null);
        for (final double d : a)
            if (Double.isNaN(d) || Double.isInfinite(d))
                return false;
        return true;
    }
    
    public static Writer openFile(String path)
    {
        try
        {
            File file = new File(path);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                    file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            return bw;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static double[] prepend(final double value, final double[] array) {
        assert(array != null);
        final double[] newArray = new double[array.length + 1];
        newArray[0] = value;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }
}
