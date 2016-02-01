package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ConstantObjective;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.transform.AdditiveObjective;
import SigmaEC.evaluate.transform.PinCushionObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class PinCushionGaussianObjective  extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_INTERVALS = "intervals";
    public final static String P_HEIGHT = "height";
    public final static String P_STD = "std";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    private final double[] intervals;
    private final double height;
    private final double std;
    
    public PinCushionGaussianObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        final int numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        intervals = parameters.getDoubleArrayParameter(Parameters.push(base, P_INTERVALS));
        
        if (numDimensions != intervals.length)
            throw new IllegalStateException(String.format("%s: numDimensions is %d, but the %d intervals are specified.  They must be equal.", this.getClass().getSimpleName(), numDimensions, intervals.length));
        height = parameters.getDoubleParameter(Parameters.push(base, P_HEIGHT));
        std = parameters.getDoubleParameter(Parameters.push(base, P_STD));
        
        
        final ObjectiveFunction<DoubleVectorIndividual> gaussian, plane, reducedGaussian;
        gaussian = new GaussianObjective(numDimensions, height, std);
        plane = new ConstantObjective(numDimensions, -height);
        reducedGaussian = new AdditiveObjective<>(new ArrayList<ObjectiveFunction<DoubleVectorIndividual>>() {{
            add(gaussian);
            add(plane);
        }});
        
        this.objective = new PinCushionObjective(intervals, -height, reducedGaussian);
        
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        return objective.fitness(ind);
    }

    @Override
    public void setStep(int i) {
        objective.setStep(i);
    }

    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_INTERVALS != null
                && !P_INTERVALS.isEmpty()
                && P_HEIGHT != null
                && !P_HEIGHT.isEmpty()
                && P_STD != null
                && !P_STD.isEmpty()
                && objective != null
                && intervals.length == getNumDimensions();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PinCushionGaussianObjective))
            return false;
        final PinCushionGaussianObjective ref = (PinCushionGaussianObjective)o;
        return Misc.doubleEquals(height, ref.height)
                && Misc.doubleEquals(std, ref.std)
                && Misc.doubleArrayEquals(intervals, ref.intervals)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 59 * hash + Arrays.hashCode(this.intervals);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, height=%f, std=%f, intervals=%s]", this.getClass().getSimpleName(), getNumDimensions(), height, std, Arrays.toString(intervals));
    }
    // </editor-fold>
}
