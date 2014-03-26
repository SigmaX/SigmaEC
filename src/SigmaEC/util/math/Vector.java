package SigmaEC.util.math;

/**
 * Elementary vector algebra methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Vector
{
    public Vector() throws AssertionError
    {
        throw new AssertionError("Vector: Attempted to instantiate static utility class.");
    }
    
    /** n-dimensional Euclidean distance. */
    public static double euclideanDistance(double[] v1, double[] v2)
    {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
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
    
    public static double[] vectorNegate(double[] v1) {
        assert(v1 != null);
        final double[] neg = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            neg[i] = -v1[i];
        return neg;
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
