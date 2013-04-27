package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;

/**
 * A piecewise continuous step function from De Jong's original suite.  Sums the
 * floor of a vector's elements.  Traditionally, each variable is bounded
 * between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class StepObjective implements ObjectiveFunction<DoubleVectorIndividual>
{
    private final int numDimensions;
    
    public StepObjective(int numDimensions)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("StepObjective: numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("StepObjective: numDimensions is infinite, must be finite.");
        this.numDimensions = numDimensions;
        assert(repOK());
    }

    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    
    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        int sum = 0;
        for(double d : ind.getVector())
            sum += (int) d;
        assert(repOK());
        return sum;
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
        return String.format("[StepObjective: NumDimensions=%d]", numDimensions);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof StepObjective))
            return false;
        
        StepObjective cRef = (StepObjective) o;
        return numDimensions == cRef.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>

}
