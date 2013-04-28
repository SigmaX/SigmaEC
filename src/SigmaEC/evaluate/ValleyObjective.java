package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Misc;

/**
 * A steep valley.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ValleyObjective implements ObjectiveFunction<DoubleVectorIndividual>, Dimensioned
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
        if ((Misc.euclideanNorm(slopeVector) - 1.0) > 0.0001)
            throw new IllegalArgumentException("ValleyObjective: slopeVector is not a unit vector.");
        
        this.numDimensions = numDimensions;
        this.slopeVector = slopeVector;
        this.interceptVector = interceptVector;
        assert(repOK());
    }

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        double result = Math.abs(ind.getVector()[0]) + 10*Misc.pointToLineEuclideanDistance(ind.getVector(), slopeVector, interceptVector);
        assert(repOK());
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return numDimensions > 0;
    }

    @Override
    public String toString()
    {
        return String.format("[Valley: NumDimensions=%d]", numDimensions);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ValleyObjective))
            return false;
        
        ValleyObjective cRef = (ValleyObjective) o;
        return numDimensions == cRef.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
