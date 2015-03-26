package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.transform.InvertedObjective;
import SigmaEC.evaluate.transform.MaxObjective;
import SigmaEC.evaluate.transform.PinCushionObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A grid of Gaussian peaks whose envelope forms the shape of an inverted sphere
 * function (paraboloid).  The top of the highest optimum is located at the
 * origin, with fitness of 0.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PinCushionSphereObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_INTERVALS = "intervals";
    public final static String P_WIDTH = "width";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    private final double[] intervals;
    private final double width;
    
    public PinCushionSphereObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        final int numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        intervals = parameters.getDoubleArrayParameter(Parameters.push(base, P_INTERVALS));
        
        if (numDimensions != intervals.length)
            throw new IllegalStateException(String.format("%s: numDimensions is %d, but the %d intervals are specified.  They must be equal.", this.getClass().getSimpleName(), numDimensions, intervals.length));
        width = parameters.getDoubleParameter(Parameters.push(base, P_WIDTH));
        /* Setting min to -(w/2)^2 ensures that the peaks of all the optima are
         * contained inside the hypercube of width w. */
        final double min = - Math.pow(width/2, 2);
        
        final ObjectiveFunction<DoubleVectorIndividual> sphere, invertedSphere, plane, invertedSphereOnPlane;
        sphere = new SphereObjective(numDimensions);
        invertedSphere = new InvertedObjective<DoubleVectorIndividual>(sphere);
        plane = new ConstantObjective<DoubleVectorIndividual>(numDimensions, min);
        invertedSphereOnPlane = new MaxObjective<DoubleVectorIndividual>(new ArrayList<ObjectiveFunction<DoubleVectorIndividual>>() {{
            add(invertedSphere);
            add(plane);
        }});
        this.objective = new PinCushionObjective(intervals, min, invertedSphereOnPlane);
        
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        return objective.fitness(ind);
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }

    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return P_INTERVALS != null
                && !P_INTERVALS.isEmpty()
                && P_WIDTH != null
                && !P_WIDTH.isEmpty()
                && objective != null
                && intervals.length == getNumDimensions();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PinCushionSphereObjective))
            return false;
        final PinCushionSphereObjective ref = (PinCushionSphereObjective)o;
        return Misc.doubleEquals(width, ref.width)
                && Misc.doubleArrayEquals(intervals, ref.intervals)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 41 * hash + Arrays.hashCode(this.intervals);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, width=%f, intervals=%s]", this.getClass().getSimpleName(), getNumDimensions(), width, Arrays.toString(intervals));
    }
    // </editor-fold>
}
