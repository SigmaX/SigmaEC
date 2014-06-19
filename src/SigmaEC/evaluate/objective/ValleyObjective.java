package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Misc;
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
    private final int numDimensions;
    private final double[] slopeVector;
    private final double[] interceptVector;
    /**
     * @param slopeVector The direction along which the valley lies.
     * @param interceptVector The location of the global optima.
     */
    public ValleyObjective(final int numDimensions, final double[] interceptVector, final double[] slopeVector)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        if (interceptVector == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": interceptVector is null.");
        if (interceptVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("%s: interceptVector has %d elements, must have %d.", this.getClass().getSimpleName(), slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(interceptVector)))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": interceptVector contains non-finite values.");
        if (slopeVector == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector is null.");
        if (slopeVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("%s: slopeVector has %d elements, must have %d.", this.getClass().getSimpleName(), slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(slopeVector)))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector contains non-finite values.");
        if (!Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector is not a unit vector.");
        
        this.numDimensions = numDimensions;
        this.slopeVector = Arrays.copyOf(slopeVector, slopeVector.length);
        this.interceptVector = Arrays.copyOf(interceptVector, interceptVector.length);
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
