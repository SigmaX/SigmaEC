package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Vector;
import java.util.Arrays;

/**
 * A steep valley.  Taken from Jeff Bassett's dissertation.
 * 
 * This is a minimization problem.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ValleyObjective extends ObjectiveFunction<DoubleVectorIndividual, ScalarFitness>
{
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_OPTIMUM = "optimum";
    public final static String P_SLOPE_VECTOR = "slopeVector";

    private final int numDimensions;
    private final double[] optimum;
    private final double[] slopeVector;
    
    public ValleyObjective(final Parameters parameters, final String base)
    {
        assert(parameters != null);
        assert(base != null);
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(String.format("%s: %s is < 1.", this.getClass().getSimpleName(), P_NUM_DIMENSIONS));

        optimum = parameters.getDoubleArrayParameter(Parameters.push(base, P_OPTIMUM));
        if (optimum.length != numDimensions)
            throw new IllegalStateException(String.format("%s: %s has %d elements, must have %d.", this.getClass().getSimpleName(), P_OPTIMUM, optimum.length, numDimensions));
        if (!Misc.allFinite(optimum))
            throw new IllegalStateException(String.format("%s: %s contains a NaN or infinite element, must be finite.", this.getClass().getSimpleName(), P_OPTIMUM));

        slopeVector = parameters.getDoubleArrayParameter(Parameters.push(base, P_SLOPE_VECTOR));
        if (slopeVector.length != numDimensions)
            throw new IllegalStateException(String.format("%s: %s has %d elements, must have %d.", this.getClass().getSimpleName(), P_SLOPE_VECTOR, slopeVector.length, numDimensions));
        final double norm = Vector.euclideanNorm(slopeVector);
        if (Misc.doubleEquals(norm, 0.0))
            throw new IllegalStateException(String.format("%s: %s is the zero vector -- must be non-zero.", this.getClass().getSimpleName(), P_SLOPE_VECTOR));
        if (!Misc.doubleEquals(norm, 1.0))
            for (int i = 0; i < slopeVector.length; i++)
                slopeVector[i] = slopeVector[i]/norm;
        
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    @Override
    public void setStep(final int i) {
        // Do nothing
    }
    
    @Override
    public ScalarFitness fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        final double[] genome = ind.getGenomeArray();
        double result = Vector.euclideanDistance(genome, optimum) + 10*Vector.pointToLineEuclideanDistance(genome, slopeVector, optimum);
        assert(repOK());
        return new ScalarFitness(result);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_OPTIMUM != null
                && !P_OPTIMUM.isEmpty()
                && P_SLOPE_VECTOR != null
                && !P_SLOPE_VECTOR.isEmpty()
                && numDimensions > 0
                && optimum != null
                && optimum.length == numDimensions
                && slopeVector != null
                && slopeVector.length == numDimensions
                && Misc.finiteValued(slopeVector)
                && Misc.finiteValued(optimum)
                && Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0);
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions,
                P_SLOPE_VECTOR, Arrays.toString(slopeVector),
                P_OPTIMUM, Arrays.toString(optimum));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ValleyObjective))
            return false;
        
        final ValleyObjective cRef = (ValleyObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleArrayEquals(slopeVector, cRef.slopeVector)
                && Misc.doubleArrayEquals(optimum, cRef.optimum);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        hash = 97 * hash + Arrays.hashCode(this.slopeVector);
        hash = 97 * hash + Arrays.hashCode(this.optimum);
        return hash;
    }
    //</editor-fold>
}
