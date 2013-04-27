package SigmaEC.util;

import java.util.Collection;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Misc
{
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
            if (o.getClass() != type)
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
        double sum = 0;
        for(int i = 0; i < values.length; i++)
            sum += values[i];
        return sum/values.length;
    }
    
    /** Sample standard deviation. */
    public static double std(double[] values, double mean)
    {
        double sum = 0;
        for (int i = 0; i < values.length; i++)
            sum += Math.pow(values[i] - mean, 2);
        return Math.sqrt(sum/(values.length - 1));
    }
    
    /** Maximum value in an array. */
    public static double max(double[] values)
    {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < values.length; i++)
            if (values[i] > max)
                max = values[i];
        return max;
    }
    
    /** Minimum value in an array. */
    public static double min(double[] values)
    {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < values.length; i++)
            if (values[i] < min)
                min = values[i];
        return min;
    }
    
}
