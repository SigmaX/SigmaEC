package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import SigmaEC.util.math.Vector;
import java.util.Arrays;

/**
 * A steep valley.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ValleyObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final int numDimensions;
    private final double[] slopeVector;
    private final double[] interceptVector;
    
    public ValleyObjective(int numDimensions, double[] interceptVector, double[] slopeVector)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("ValleyObjective: numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("ValleyObjective: numDimensions is infinite, must be finite.");
        if (interceptVector == null)
            throw new IllegalArgumentException("ValleyObjective: interceptVector is null.");
        if (interceptVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("LinearRidgeObjective: interceptVector has %d elements, must have %d.", slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(interceptVector)))
            throw new IllegalArgumentException("ValleyObjective: interceptVector contains non-finite values.");
        if (slopeVector == null)
            throw new IllegalArgumentException("ValleyObjective: slopeVector is null.");
        if (slopeVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("ValleyObjective: slopeVector has %d elements, must have %d.", slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(slopeVector)))
            throw new IllegalArgumentException("ValleyObjective: slopeVector contains non-finite values.");
        if ((Vector.euclideanNorm(slopeVector) - 1.0) > 0.0001)
            throw new IllegalArgumentException("ValleyObjective: slopeVector is not a unit vector.");
        
        this.numDimensions = numDimensions;
        this.slopeVector = Arrays.copyOf(slopeVector, slopeVector.length);
        this.interceptVector = Arrays.copyOf(interceptVector, interceptVector.length);
        assert(repOK());
    }

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        assert(ind.size() == numDimensions);
        double result = Math.abs(ind.getVector()[0]) + 10*Vector.pointToLineEuclideanDistance(ind.getVector(), slopeVector, interceptVector);
        assert(repOK());
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return numDimensions > 0
                && interceptVector != null
                && interceptVector.length == numDimensions
                && slopeVector != null
                && slopeVector.length == numDimensions
                && Misc.finiteValued(slopeVector)
                && Misc.finiteValued(interceptVector)
                && (Vector.euclideanNorm(slopeVector) - 1.0) <= 0.0001;
    }

    @Override
    public String toString()
    {
        return String.format("[Valley: NumDimensions=%d, SlopeVector=%s, InterceptVector=%s]", numDimensions, Arrays.toString(slopeVector), Arrays.toString(interceptVector));
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ValleyObjective))
            return false;
        
        ValleyObjective cRef = (ValleyObjective) o;
        return numDimensions == cRef.numDimensions
                && Arrays.equals(slopeVector, cRef.slopeVector)
                && Arrays.equals(interceptVector, cRef.interceptVector);
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
