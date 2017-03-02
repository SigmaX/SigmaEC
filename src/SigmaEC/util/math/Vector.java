package SigmaEC.util.math;

import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;

/**
 * Elementary vector algebra methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Vector {
    public Vector() throws AssertionError {
        throw new AssertionError("Vector: Attempted to instantiate static utility class.");
    }
    
    /** n-dimensional Euclidean distance. */
    public static double euclideanDistance(final double[] v1, final double[] v2) {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
        return euclideanNorm(vectorMinus(v1, v2));
    }
    
    /** Euclidean length of a vector. */
    public static double euclideanNorm(final double[] v) {
        assert(v != null);
        double sum = 0;
        for (int i = 0; i < v.length; i++)
            sum += Math.pow(v[i], 2);
        return Math.sqrt(sum);
    }
    
    public static double[] normalize(final double[] v) {
        assert(v != null);
        assert(!Misc.containsNaNs(v));
        final double norm = euclideanNorm(v);
        final double[] normalizedV = new double[v.length];
        for (int i = 0; i < v.length; i++)
            normalizedV[i] = v[i]/norm;
        assert(Misc.doubleEquals(1.0, euclideanNorm(normalizedV)));
        return normalizedV;
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
    
    /** Sum of to vectors.
     * 
     * @return v1 + v2
     */
    public static double[] vectorSum(double[] v1, double[] v2)
    {
        assert(v1 != null);
        assert(v2 != null);
        assert(v1.length == v2.length);
        double[] difference = new double[v1.length];
        for (int i = 0; i < v1.length; i++)
            difference[i] = v1[i] + v2[i];
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
    
    /** Twp-dimensional cross product. */
    public static double crossProduct(final double[] a, final double[] b) {
        assert(a != null);
        assert(b != null);
        assert(a.length == 2);
        assert(b.length == 2);
        assert(!Misc.containsNaNs(a));
        assert(!Misc.containsNaNs(b));
        return a[0]*b[1] - b[0]*a[1];
    }
    
    /** Returns true iff the start and end points of line2 are on the same side
    * of the infinite line suggested by line1. */
    public static boolean onSameSide(final double[] line1, final double[] line2) {
        assert(line1 != null);
        assert(line2 != null);
        assert(line1.length == 4);
        assert(line2.length == 4);
        assert(!Misc.containsNaNs(line1));
        assert(!Misc.containsNaNs(line2));
        /* To determine which side of [AB] a point P is on, treat [AB] and [BP] as
         * vectors centered at the origin, and take the cross product.  The sign
         * indicates which side the point is on (thanks to the right hand rule). */
        // Center line1 on the origin
        final double[] line1Vector = new double[] { line1[2] - line1[0], line1[3] - line1[1] }; 
        // Interpret the 1st point of line2 as a vector extending from the end of line1
        final double[] p1Vector = new double[] { line2[0] - line1[2], line2[1] - line1[3] }; 
        // Interpret the 2nd point of line2 as a vector extending from the end of line1
        final double[] p2Vector = new double[] { line2[2] - line1[2], line2[3] - line1[3] };
        return !((crossProduct(line1Vector, p1Vector) > 0) ^ (crossProduct(line1Vector, p2Vector) > 0));
    }
    
    /** Returns true if the two line seqments cross.  Each segment is a pair
    * of points, (x1, y1, x2, y2). */
    public static boolean crosses(final double[] line1, final double[] line2) {
        assert(line1 != null);
        assert(line2 != null);
        assert(line1.length == 4);
        assert(line2.length == 4);
        assert(!Misc.containsNaNs(line1));
        assert(!Misc.containsNaNs(line2));
        /* If the points in line1 are on the same side of line2, or if the points in
        * line2 are on the same side of line1, then the lines do not intersect. */
        return !(onSameSide(line1, line2) || onSameSide(line2, line1));
    }
    
    public static IDoublePoint intersectionPoint(final double[] line1, final double[] line2) {
        assert(line1 != null);
        assert(line2 != null);
        assert(line1.length == 4);
        assert(line2.length == 4);
        assert(!Misc.containsNaNs(line1));
        assert(!Misc.containsNaNs(line2));
        assert(crosses(line1, line2));
        
        if (isVerticle(line1)) {
            if (!isVerticle(line2))
                return intersectionPointWithVerticleLine(line1, line2);
            else {
                // Both lines are verticle and cross, so they intersect as infinitely many points.
                // Return an arbitrary point along one of the segments.
                return new IDoublePoint(line1[0], line1[1]);
            }
        }
        else if (isVerticle(line2))
                return intersectionPointWithVerticleLine(line2, line1);
        
        // Neither line is verticle.
        // Solving algebraically for the intersection of two lines each defined
        // by two points yields the following equations.
        final double theirSlope = (line1[3] - line1[1])/(line1[2] - line1[0]);
        final double ourSlope = (line2[3] - line2[1])/(line2[2] - line2[0]);
        final double xResult = (1.0/(theirSlope - ourSlope)) * (theirSlope*line1[0] - ourSlope*line2[0] + line2[1] - line1[1]);
        final double yResult = theirSlope*(xResult - line1[0]) + line1[1];
        assert(Double.isFinite(xResult));
        assert(Double.isFinite(yResult));
        assert(Misc.doubleEquals(0, pointToLineEuclideanDistance(new double[] { xResult, yResult }, normalize(new double[] { line1[2] - line1[0], line1[3] - line1[1] }), new double[] { line1[0], line1[1] })));
        assert(Misc.doubleEquals(0, pointToLineEuclideanDistance(new double[] { xResult, yResult }, normalize(new double[] { line2[2] - line2[0], line2[3] - line2[1] }), new double[] { line2[0], line2[1] })));
        return new IDoublePoint(xResult, yResult);
    }
    
    private static IDoublePoint intersectionPointWithVerticleLine(final double[] verticalLine, final double[] otherLine) {
        assert(verticalLine != null);
        assert(otherLine != null);
        assert(verticalLine.length == 4);
        assert(otherLine.length == 4);
        assert(!Misc.containsNaNs(verticalLine));
        assert(!Misc.containsNaNs(otherLine));
        assert(isVerticle(verticalLine));
        assert(crosses(verticalLine, otherLine));
        final double xResult = verticalLine[0];
        final double slope = (otherLine[3] - otherLine[1])/(otherLine[2] - otherLine[0]);
        final double yResult = slope*(xResult - otherLine[0]) + otherLine[1];
        return new IDoublePoint(xResult, yResult);
    }
    
    private static boolean isVerticle(final double[] line) {
        assert(line != null);
        assert(line.length == 4);
        assert(!Misc.containsNaNs(line));
        return line[0] == line[2];
    }
}
