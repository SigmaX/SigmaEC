package SigmaEC.util;

import java.util.Collection;
import java.util.Random;

/**
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
    
    /** Mean. */
    public static double mean(double[] values)
    {
        assert(values != null);
        double sum = 0;
        for(int i = 0; i < values.length; i++)
            sum += values[i];
        return sum/values.length;
    }
    
    /** Population standard deviation. */
    public static double std(double[] values, double mean)
    {
        assert(values != null);
        double sum = 0;
        for (int i = 0; i < values.length; i++)
            sum += Math.pow(values[i] - mean, 2);
        return Math.sqrt(sum/(values.length));
    }
    
    /** Maximum value in an array. */
    public static double max(double[] values)
    {
        assert(values != null);
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < values.length; i++)
            if (values[i] > max)
                max = values[i];
        return max;
    }
    
    /** Minimum value in an array. */
    public static double min(double[] values)
    {
        assert(values != null);
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < values.length; i++)
            if (values[i] < min)
                min = values[i];
        return min;
    }
    
    /** n-dimensional Euclidean distance. */
    public static double euclideanDistance(double[] v1, double[] v2)
    {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
        /*double sum = 0;
        for (int i = 0; i < v1.length; i++)
            sum += Math.pow(v1[i] - v2[i], 2);
        return Math.sqrt(sum);*/
        return euclideanNorm(vectorMinus(v1, v2));
    }
    
    /** Euclidean length of a vector. */
    public static double euclideanNorm(double[] v)
    {
        assert(v != null);
        double sum = 0;
        for (int i = 0; i < v.length; i++)
            sum += Math.pow(v[i], 2);
        return Math.sqrt(sum);
    }
    
    /** Dot product. */
    public static double dotProduct(double[] v1, double[] v2)
    {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
        double sum = 0;
        for (int i = 0; i < v1.length; i++)
            sum += v1[i] * v2[i];
        return sum;
    }
    
    /** Difference between to vectors.
     * 
     * @return v1 - v2
     */
    public static double[] vectorMinus(double[] v1, double[] v2)
    {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
        double[] difference = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            difference[i] = v1[i] - v2[i];
        return difference;
    }
    
    /** Multiply a scalar times a vector. */
    public static double[] scalarTimesVector(double s, double[] v)
    {
        assert(v != null);
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++)
            result[i] = s*v[i];
        return result;
    }
    
    /** Euclidean distance from a point to a line. 
     * 
     * @param point The point.
     * @param slopeVector Slope unit vector of the line.
     * @param interceptPoint A point the line passes through.
     * @return Distance between the point and the nearest point which lies on the line.
     */
    public static double pointToLineEuclideanDistance(double[] point, double[] slopeVector, double[] interceptPoint)
    {
        assert(point != null);
        assert(slopeVector != null);
        assert(interceptPoint != null);
        assert(point.length == slopeVector.length);
        assert(point.length == interceptPoint.length);
        assert((euclideanNorm(slopeVector) - 1.0) < 0.001);
        
        double[] pointToIntercept = vectorMinus(interceptPoint, point);
        double[] pointToLine = vectorMinus(pointToIntercept, scalarTimesVector(dotProduct(pointToIntercept, slopeVector), slopeVector));
        double distance = euclideanNorm(pointToLine);
        return distance;
    }
}
