package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 * Represents a plane in point-normal form.  The last element of each vector is
 * the fitness coordinate.
 * 
 * @author Eric O. Scott
 */
public class PlaneObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_POINT = "point";
    public final static String P_NORMAL = "normalVector";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final double[] point;
    private final double[] normalVector;
    
    public PlaneObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        point = parameters.getDoubleArrayParameter(Parameters.push(base, P_POINT));
        if (!Misc.finiteValued(point))
                throw new IllegalStateException(String.format("%s: %s contains infinite or NaN values, must be finite-valued.", this.getClass().getSimpleName(), P_POINT));
        final int numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (point.length - 1 != numDimensions)
            throw new IllegalStateException(String.format("%s: %s has %d elements, but must have %d elements for a %d-dimensional objective.", this.getClass().getSimpleName(), P_POINT, point.length, numDimensions + 1, numDimensions));
        normalVector = parameters.getDoubleArrayParameter(Parameters.push(base, P_NORMAL));
        if (!Misc.finiteValued(normalVector))
                throw new IllegalStateException(String.format("%s: %s contains infinite or NaN values, must be finite-valued.", this.getClass().getSimpleName(), P_NORMAL));
        if (point.length != normalVector.length)
                throw new IllegalStateException(String.format("%s: lengths of %s and %s differ.", this.getClass().getSimpleName(), P_POINT, P_NORMAL));
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind != null);
        final double[] genome = ind.getGenomeArray();
        final double z0 = point[point.length - 1];
        final double c = normalVector[normalVector.length - 1];
        double sum = 0.0;
        for (int i = 0; i < point.length - 1; i++)
            sum += normalVector[i] * (genome[i] - point[i]);
        final double result = -1/c*sum + z0;
        assert(repOK());
        return result;
    }

    @Override
    public void setStep(int i) { /* Do nothing. */ }

    @Override
    public int getNumDimensions() {
        return point.length - 1;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_POINT != null
                && !P_POINT.isEmpty()
                && P_NORMAL != null
                && !P_NORMAL.isEmpty()
                && point != null
                && Misc.finiteValued(point)
                && normalVector != null
                && Misc.finiteValued(normalVector)
                && point.length == normalVector.length;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PlaneObjective))
            return false;
        final PlaneObjective ref = (PlaneObjective) o;
        return Misc.doubleArrayEquals(point, ref.point)
                && Misc.doubleArrayEquals(normalVector, ref.normalVector);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.hashCode(this.point);
        hash = 29 * hash + Arrays.hashCode(this.normalVector);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_POINT, Arrays.toString(point),
                P_NORMAL, Arrays.toString(normalVector));
    }
    // </editor-fold>
}
