package SigmaEC.util.math;

/**
 * Elementary statistics methods.
 * 
 * @author Eric 'Siggy' Scott
 */
public final class Statistics
{
    public Statistics() throws AssertionError {
        throw new AssertionError(Statistics.class.getSimpleName() + ": Attempted to instantiate static utility class.");
    }
    
    /** Mean. */
    public static double mean(double[] values) {
        assert(values != null);
        double sum = 0;
        for(int i = 0; i < values.length; i++)
            sum += values[i];
        return sum/values.length;
    }

    /** Sample standard deviation (without Bessel's correction). */
    public static double std(double[] values, double mean) {
        assert(values != null);
        double sum = 0;
        for (int i = 0; i < values.length; i++)
            sum += Math.pow(values[i] - mean, 2);
        return Math.sqrt(sum/(values.length));
    }

    /** Index of maximum value in an array. */
    public static int maxIndex(final double[] values) {
        assert(values != null);
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /** Maximum value in an array. */
    public static double max(final double[] values) {
        assert(values != null);
        return values[maxIndex(values)];
    }
    
    /** Index of minimum value in an array. */
    public static int minIndex(double[] values) {
        assert(values != null);
        double min = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < min) {
                min = values[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
    
    /** Minimum value in an array. */
    public static double min(double[] values) {
        assert(values != null);
        return values[minIndex(values)];
    }
    
}
