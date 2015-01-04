package SigmaEC.evaluate.objective;

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
public class ValleyObjective extends ObjectiveFunction<DoubleVectorIndividual>
{
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_INTERCEPT_VECTOR = "interceptVector";
    public final static String P_SLOPE_VECTOR = "slopeVector";

    private final int numDimensions;
    private final double[] interceptVector;
    private final double[] slopeVector;
    /**
     * @param slopeVector The direction along which the valley lies.
     * @param interceptVector The location of the global optima.
     */
    public ValleyObjective(final Parameters parameters, final String base)
    {
        assert(parameters != null);
        assert(base != null);
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");

        interceptVector = parameters.getDoubleArrayParameter(Parameters.push(base, P_INTERCEPT_VECTOR));
        if (interceptVector.length != numDimensions)
            throw new IllegalStateException(String.format("%s: interceptVector has %d elements, must have %d.", this.getClass().getSimpleName(), interceptVector.length, numDimensions));

        slopeVector = parameters.getDoubleArrayParameter(Parameters.push(base, P_SLOPE_VECTOR));
        if (slopeVector.length != numDimensions)
            throw new IllegalStateException(String.format("%s: slopeVector has %d elements, must have %d.", this.getClass().getSimpleName(), slopeVector.length, numDimensions));
        if (!Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector is not a unit vector.");
        
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    @Override
    public void setGeneration(final int i) {
        // Do nothing
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        final double[] genome = ind.getGenomeArray();
        double result = Vector.euclideanDistance(genome, interceptVector) + 10*Vector.pointToLineEuclideanDistance(genome, slopeVector, interceptVector);
        assert(repOK());
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numDimensions > 0
                && interceptVector != null
                && interceptVector.length == numDimensions
                && slopeVector != null
                && slopeVector.length == numDimensions
                && Misc.finiteValued(slopeVector)
                && Misc.finiteValued(interceptVector)
                && Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0);
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, slopeVector=%s, interceptVector=%s]", this.getClass().getSimpleName(), numDimensions, Arrays.toString(slopeVector), Arrays.toString(interceptVector));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ValleyObjective))
            return false;
        
        final ValleyObjective cRef = (ValleyObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleArrayEquals(slopeVector, cRef.slopeVector)
                && Misc.doubleArrayEquals(interceptVector, cRef.interceptVector);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        hash = 97 * hash + Arrays.hashCode(this.slopeVector);
        hash = 97 * hash + Arrays.hashCode(this.interceptVector);
        return hash;
    }
    //</editor-fold>
}
