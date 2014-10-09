package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import SigmaEC.util.Misc;
import java.util.Arrays;


/**
 * A decorator that turns a smooth landscape into a set of evenly spaces
 * peaks sich that the tops of the peaks will all "touch" the original landscape
 * that was transformed.  The distance between the peaks can vary along each
 * axis.
 * 
 * @author Jeffrey K Basssett
 */
public class PinCushionObjective extends ObjectiveFunction<DoubleVectorIndividual>
{
    public final static String P_OBJECTIVE = "objective";
    public final static String P_INTERVALS = "intervals";
    final private ObjectiveFunction<DoubleVectorIndividual> objective;
    private double[] intervals; // The distances between the peaks on each axis
    private double sigma;     // The standard deviation of the peaks
    private double k;         // A constant
    
    public PinCushionObjective(final Parameters parameters, final String base) {
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");

        this.intervals = parameters.getDoubleArrayParameter(Parameters.push(base, P_INTERVALS));
        this.sigma = Statistics.min(intervals) / 6;  // relative sigma = 3?
        this.k = 1/(2 * sigma * sigma);
        assert(repOK());
    }

    @Override
    public double fitness(DoubleVectorIndividual ind) {
        int n = ind.size();
        double[] center = new double[n];
        double[] relative = new double[n];

        // Find the closest peak, and our position relative to it
        for (int i = 0; i < n; i++)
        {
            double gene = ind.getElement(i);
            center[i] = Math.round(gene / intervals[i]) * intervals[i];
            relative[i] = gene - center[i];
        }

        // Find value at the peak
        double value = objective.fitness(new DoubleVectorIndividual(center));
        
        // Calculate the value one axis at a time
        for (int i = 0; i < n; i++)
            value = value * Math.exp(-relative[i] * relative[i] * this.k);

        return value;
    }
    
    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && this.intervals != null
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
                && this.k > 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: objective=%s, intervals=%s]", this.getClass().getSimpleName(), objective.toString(), Arrays.toString(this.intervals));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PinCushionObjective))
            return false;
        
        final PinCushionObjective cRef = (PinCushionObjective) o;
        return objective.equals(cRef.objective)
               && Misc.doubleArrayEquals(cRef.intervals, this.intervals);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>

    
}
