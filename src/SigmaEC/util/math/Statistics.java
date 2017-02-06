package SigmaEC.util.math;

import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.represent.Individual;
import SigmaEC.represent.distance.DistanceMeasure;
import SigmaEC.util.Misc;
import java.util.List;

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

    /** Sample standard deviation (with Bessel's correction). */
    public static double std(double[] values, double mean) {
        assert(Misc.doubleEquals(mean, mean(values)));
        assert(values != null);
        if (values.length <= 1)
            return 0;
        double sum = 0;
        for (int i = 0; i < values.length; i++)
            sum += Math.pow(values[i] - mean, 2);
        return Math.sqrt(sum/((values.length) - 1));
    }

    /** Sample standard deviation (with Bessel's correction). */
    public static double std(double[] values) {
        assert(values != null);
        if (values.length <= 1)
            return 0;
        return std(values, mean(values));
    }
    
    /*public static double covariance(double[] x, double[] y) {
        
    }*/

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
    
    /** Return the best individual according to some FitnessComparator. */
    public static <T extends Individual<F>, F extends Fitness> T best(final List<T> values, final FitnessComparator<T, F> comparator) {
        assert(values != null);
        assert(comparator != null);
        if (values.isEmpty()) return null;
        T best = values.get(0);
        for (final T val : values) {
            if (comparator.betterThan(val, best))
                best = val;
        }
        return best;
    }
    
    /** Return the index of the best individual according to some ScalarFitnessComparator. */
    public static <T extends Individual<F>, F extends Fitness> int bestIndex(final List<T> values, final FitnessComparator<T, F> comparator) {
        assert(values != null);
        assert(comparator != null);
        if (values.isEmpty()) return -1;
        int bestIndex = 0;
        T best = values.get(0);
        for (int i = 1; i < values.size(); i++) {
            final T val = values.get(i);
            if (comparator.betterThan(val, best)) {
                bestIndex = i;
                best = val;
            }
        }
        return bestIndex;
    }
    
    /** Return the worst individual according to some ScalarFitnessComparator. */
    public static <T extends Individual<F>, F extends Fitness> T worst(final List<T> values, final FitnessComparator<T, F> comparator) {
        assert(values != null);
        assert(comparator != null);
        if (values.isEmpty()) return null;
        T best = values.get(0);
        for (final T val : values) {
            if (!comparator.betterThan(val, best))
                best = val;
        }
        return best;
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
    
    public static boolean sumsToOne(final double[] array) {
        if (array == null || Misc.containsNaNs(array))
            return false;
        double sum = 0.0;
        for (final double d : array)
            sum += d;
        return Misc.doubleEquals(sum, 1.0);
    }
    
    /** Compute distance matrix among a set of individuals. */
    public static <T> double[][] distanceMatrix(final List<T> points, final DistanceMeasure<T> distanceMeasure) {
        assert(points != null);
        assert(points.size() > 0);
        final double[][] distances = new double[points.size()][points.size()];
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                distances[i][j] = distanceMeasure.distance(points.get(i), points.get(j));
            }
        }
        return distances;
    }
}
