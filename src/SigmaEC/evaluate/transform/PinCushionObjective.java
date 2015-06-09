package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.Arrays;


/**
 * A decorator that turns a smooth landscape into a set of evenly spaces
 * peaks such that the tops of the peaks will all "touch" the original landscape
 * that was transformed.  The distance between the peaks can vary along each
 * axis (cf. the 'intervals' parameter), and the depth of the valleys is
 * controlled by the 'min' parameter.
 * 
 * @author Jeffrey K Basssett
 * @author Eric 'Siggy' Scott
 */
public class PinCushionObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_OBJECTIVE = "objective";
    public final static String P_INTERVALS = "intervals";
    public final static String P_MIN = "min";
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    private final double[] intervals; // The distances between the peaks on each axis
    private final double sigma;     // The standard deviation of the peaks
    private final double k;         // A constant
    private final double min;       // The bottom of the valleys
    
    public PinCushionObjective(final Parameters parameters, final String base) throws IllegalStateException {
        assert(parameters != null);
        assert(base != null);
        
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");

        this.intervals = parameters.getDoubleArrayParameter(Parameters.push(base, P_INTERVALS));
        this.sigma = Statistics.min(intervals) / 6;
        this.k = 1/(2 * sigma * sigma);
        final Option<Double> minOpt = parameters.getOptionalDoubleParameter(Parameters.push(base, P_MIN));
        if (minOpt.isDefined())
            this.min = minOpt.get();
        else
            this.min = 0.0;
        
        if (intervals.length != objective.getNumDimensions())
            throw new IllegalStateException(String.format("%s: intervals vector must have same length as the dimensionality of the objective function.", this.getClass().getSimpleName()));
        if (Double.isNaN(min))
            throw new IllegalStateException(String.format("%s: min is NaN.", this.getClass().getSimpleName()));
        
        assert(repOK());
    }
    
    public PinCushionObjective(final double[] intervals, final double min, final ObjectiveFunction<DoubleVectorIndividual> objective) {
        if (intervals == null)
            throw new NullPointerException(String.format("%s: intervals array cannot be null.", this.getClass().getSimpleName()));
        if (Misc.containsNaNs(intervals) || !Misc.allFinite(intervals))
            throw new IllegalArgumentException(String.format("%s: intervals array contains a NaN or infinite value.  All elements must be finite.", this.getClass().getSimpleName()));
        if (objective == null)
            throw new NullPointerException(String.format("%s: objectve cannot be null.", this.getClass().getSimpleName()));
        if (intervals.length != objective.getNumDimensions())
            throw new IllegalArgumentException(String.format("%s: intervals vector must have same length as the dimensionality of the objective function.", this.getClass().getSimpleName()));
        if (Double.isNaN(min))
            throw new IllegalArgumentException(String.format("%s: min is NaN.", this.getClass().getSimpleName()));
        
        this.objective = objective;
        this.intervals = intervals;
        this.min = min;
        this.sigma = Statistics.min(intervals)/6;
        this.k = 1/(2*sigma*sigma);
        
        assert(repOK());
    }

    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        int n = ind.size();
        double[] center = new double[n];
        double[] relative = new double[n];

        // Find the closest peak, and our position relative to it
        for (int i = 0; i < n; i++) {
            double gene = ind.getElement(i);
            center[i] = Math.round(gene / intervals[i]) * intervals[i];
            relative[i] = gene - center[i];
        }

        // Find height of the peak relative to the min
        double value = objective.fitness(new DoubleVectorIndividual.Builder(center).build()) - min;
        
        // Calculate the value one axis at a time
        for (int i = 0; i < n; i++)
            value = value * Math.exp(-this.k * relative[i] * relative[i]);
        value += min;
        
        return value;
    }
    
    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }

    @Override
    public void setGeneration(final int i) {
        objective.setGeneration(i);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && this.intervals != null
                && this.intervals.length == objective.getNumDimensions()
                && !Misc.containsNaNs(this.intervals)
                && Misc.finiteValued(this.intervals)
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_INTERVALS != null
                && !P_INTERVALS.isEmpty()
                && !Double.isNaN(this.sigma)
                && !Double.isInfinite(this.sigma)
                && this.sigma > 0.0
                && !Double.isNaN(this.k)
                && !Double.isInfinite(this.k)
                && this.k > 0.0
                && !Double.isNaN(min)
                && Misc.doubleEquals(sigma, Statistics.min(intervals) / 6)
                && Misc.doubleEquals(k, 1/(2 * sigma * sigma));
    }
    
    @Override
    public String toString() {
        return String.format("[%s: objective=%s, intervals=%s, min=%f]", this.getClass().getSimpleName(), objective.toString(), Arrays.toString(this.intervals), min);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PinCushionObjective))
            return false;
        
        final PinCushionObjective cRef = (PinCushionObjective) o;
        return objective.equals(cRef.objective)
               && Misc.doubleArrayEquals(cRef.intervals, this.intervals)
               && Misc.doubleEquals(min, cRef.min);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 71 * hash + Arrays.hashCode(this.intervals);
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        return hash;
    }
    //</editor-fold>
}
