package SigmaEC.util;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.meta.Fitness;
import SigmaEC.represent.Individual;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Miscellaneous utility methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Misc
{
    /** Private constructor throws an error if called. */
    private Misc() throws AssertionError {
        throw new AssertionError(String.format("%s: Cannot create instance of static class.", Misc.class.getSimpleName()));
    }
    
    /**
     * Returns false if c contains an object of any other type, true otherwise.
     * This uses getClass() to compare, not instanceof, so subtypes of type will
     * cause a false result.
     */
    public static boolean containsOnlyClass(final Collection c, final Class type) {
        assert(c != null);
        assert(type != null);
        for (Object o : c)
            if (o == null || o.getClass() != type)
                return false;
        return true;
    }
    
    /** returns true if c contains any null values. */
    public static boolean containsNulls(final Collection c) {
        assert(c != null);
        for (Object o : c)
            if (o == null)
                return true;
        return false;
    }
    
    /** Returns true if c contains any null values. */
    public static <T> boolean containsNulls(final T[] c) {
        assert(c != null);
        for (int i = 0; i < c.length; i++)
            if (c[i] == null)
                return true;
        return false;
    }
    
    /** returns true if c contains any NaN values. */
    public static boolean containsNaNs(final double[] c) {
        assert(c != null);
        for (int i = 0; i < c.length; i++)
            if (Double.isNaN(c[i]))
                return true;
        return false;
    }
    
    /** Returns false if there are non-finite values in a vector. */
    public static boolean finiteValued(final double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            double e = vector[i];
            if (e == Double.NEGATIVE_INFINITY || e == Double.POSITIVE_INFINITY || Double.isNaN(e))
                return false;
        }
        return true;
    }
    
    public static double[][] deepCopy2DArray(final double[][] original) {
        final double[][] newArray = new double[original.length][];
        for (int i = 0; i < original.length; i++)
            newArray[i] = Arrays.copyOf(original[i], original[i].length);
        return newArray;
    }
    
    /** Check that x &lt; y for all IDoublePoints in an array. */
    public static boolean boundsOK(final IDoublePoint[] bounds) {
        for (IDoublePoint p : bounds)
            if (p == null || p.x >= p.y)
                return false;
        return true;
    }
    
    public static boolean doubleEquals(final double a, final double b) {
        return doubleEquals(a, b, 0.000001);
    }
    
    public static boolean doubleEquals(final double a, final double b, final double epsilon) {
        final double diff = Math.abs(a - b);
        return diff < epsilon
                || (Double.isNaN(diff) && a == b); // Handle the case where a = b = Double.POSITIVE_INFINITY or a = b = Double.NEGATIVE_INFINITY.
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
    
    public static <T> boolean collectionOfArraysEquals(final Collection<T[]> a, final Collection<T[]> b) {
        assert(a != null);
        assert(b != null);
        if (a == b)
            return true;
        if (a.size() != b.size())
            return false;
        for (final T[] aE : a) {
            if (!containsArray(b, aE))
                return false;
        }
        return true;
    }
    
    /**
     * A 'contains' method that can be used on collections of arrays.
     * 
     * Because the equals() method for arrays is broken, collections of arrays
     * are broken. This is a work-around.
     * 
     * @param c
     * @param array
     * @return True iff c contains an array a such that Arrays.equals(a, array).
     */
    public static <T> boolean containsArray(final Collection<T[]> c, final T[] array) {
        assert(c != null);
        assert(array != null);
        for (final T[] e : c)
            if (Arrays.equals(array, e))
                return true;
        return false;
    }
    
    public static boolean allFinite(final double[] a) {
        assert(a != null);
        for (final double d : a)
            if (Double.isNaN(d) || Double.isInfinite(d))
                return false;
        return true;
    }
    
    public static String inputStreamToString(final InputStream is) throws IOException {
        assert(is != null);
        final char[] buffer = new char[200];
        final StringBuilder out = new StringBuilder();
        final Reader in = new InputStreamReader(is, "UTF-8");
        try {
          for (;;) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
              break;
            out.append(buffer, 0, rsz);
          }
        }
        finally {
          in.close();
        }
        return out.toString();
    }
    
    public static double[] prepend(final double value, final double[] array) {
        assert(array != null);
        final double[] newArray = new double[array.length + 1];
        newArray[0] = value;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }
    
    public static boolean[] prepend(final boolean value, final boolean[] array) {
        assert(array != null);
        final boolean[] newArray = new boolean[array.length + 1];
        newArray[0] = value;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }
    
    public static char[] prepend(final char[] pre, final char[] post) {
        assert(post != null);
        final char[] newArray = new char[post.length + pre.length];
        System.arraycopy(pre, 0, newArray, 0, pre.length);
        System.arraycopy(post, 0, newArray, pre.length, post.length);
        return newArray;
    }
    
    public static boolean[] prepend(final boolean[] pre, final boolean[] post) {
        assert(post != null);
        final boolean[] newArray = new boolean[post.length + pre.length];
        System.arraycopy(pre, 0, newArray, 0, pre.length);
        System.arraycopy(post, 0, newArray, pre.length, post.length);
        return newArray;
    }
    
    public static <T extends Individual, F extends Fitness> boolean allElementsHaveDimension(final List<ObjectiveFunction<T, F>> objectives, final int dimensions) {
        assert(objectives != null);
        assert(objectives.size() > 0);
        for (ObjectiveFunction<T, ?> objective : objectives) {
            if (objective.getNumDimensions() != dimensions)
                return false;
        }
        return true;
    }
    
    public static boolean isNumber(final String s) {
        assert(s != null);
        try {
            Double.valueOf(s);
        }
        catch(final NumberFormatException ee) {
            return false;
        }
        return true;
    }
    
    public static boolean in(final char[] array, final char element) {
        assert(array != null);
        for (int i = 0; i < array.length; i++)
            if (array[i] == element)
                return true;
        return false;
    }
        
    public static double[] repeatValue(final double val, final int times) {
        assert(times >= 0);
        final double[] array = new double[times];
        for (int i = 0; i < array.length; i++)
            array[i] = val;
        return array;
    }
        
    public static int[] repeatValue(final int val, final int times) {
        assert(times >= 0);
        final int[] array = new int[times];
        for (int i = 0; i < array.length; i++)
            array[i] = val;
        return array;
    }
        
    public static double[] repeatValues(final double[] vals, final int times) {
        assert(vals != null);
        assert(times >= 0);
        final double[] array = new double[times*vals.length];
        for (int i = 0; i < times; i++)
            for (int j = 0; j < vals.length; j++)
                array[i*vals.length + j] = vals[j];
        return array;
    }
    
    /** @return true iff the two collections contain the exact same
     * elements in the exact same order.
     */
    public static boolean shallowEquals(final Iterable a, final Iterable b) {
        assert(a != null);
        assert(b != null);
        
        final Iterator itA = a.iterator();
        final Iterator itB = b.iterator();
        while (itA.hasNext()) {
            if (!itB.hasNext())
                return false;
            if (itA.next() != itB.next())
                return false;
        }
        assert(!itA.hasNext());
        if (itB.hasNext())
            return false;
        return true;
    }
    
    public static <T> boolean allRowsEqualLength(final T[][] matrix) {
        assert(matrix != null);
        if (matrix.length == 0)
            return true;
        final int columns = matrix[0].length;
        for (int i = 1; i < matrix.length; i++)
            if (matrix[i].length != columns)
                return false;
        return true;
    }
    
    public static boolean arrayLessThanOrEqualTo(final int[] mins, final int[] maxes) {
        assert(mins != null);
        assert(maxes != null);
        assert(mins.length == maxes.length);
        for (int i = 0; i < mins.length; i++)
            if (mins[i] > maxes[i])
                return false;
        return true;
    }
    
    public static <T> T getIthSetElement(final Set<T> set, final int i) {
        assert(set != null);
        assert(i >= 0);
        assert(i < set.size());
        int j = 0;
        for (final T e : set) {
            if (j == i)
                return e;
            j++;
        }
        throw new IllegalArgumentException(String.format("%s: attempted to retrieve the %dth element from a set whose size was %d.", Misc.class.getSimpleName(), i, set.size()));
    }
    
    /** Convert an array of bits into its corresponding integer representation, assuming a big-endian encoding. */
    public static int bitStringToInt(final boolean[] bitString) {
        assert(bitString != null);
        int result = 0;
        for (int i = 0; i < bitString.length; i++) {
            if (bitString[i])
                result += Math.pow(2, bitString.length - 1 - i);
        }
        return result;
    }
    
    public static String arrayToCSV(final double[] a) {
        assert(a != null);
        assert(a.length > 0);
        final StringBuilder sb = new StringBuilder();
        sb.append(a[0]);
        for (int i = 1; i < a.length; i++)
            sb.append(",").append(a[i]);
        return sb.toString();
    }
}
