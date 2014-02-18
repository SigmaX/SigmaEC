package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class ConstantObjective<T extends DoubleVectorPhenotype> implements ObjectiveFunction<T> {
    
    private final int numDimensions;
    private final double value;
    
    public ConstantObjective(final int numDimensions, final double fitnessValue)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("ConstantObjective: numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("ConstantObjective: numDimensions is infinite, must be finite.");
        this.numDimensions = numDimensions;
        this.value = fitnessValue;
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
        assert(repOK());
        return value;
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
        return String.format("[ConstantObjective: NumDimensions=%d, Value=%f]", numDimensions, value);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ConstantObjective))
            return false;
        
        final ConstantObjective cRef = (ConstantObjective) o;
        return numDimensions == cRef.numDimensions
                && Math.abs(value - cRef.value) < 0.000001;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
