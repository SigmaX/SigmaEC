package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;

/**
 * The so-called "sphere function," f(x) = \sum_i x_i^2.  This is actually a
 * paraboloid, but De Jong (1975) introduced it originally as a function over
 * three variables, which has "spherical constant-cost contours." 
 * Traditionally, each variable is bounded between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class SphereObjective implements ObjectiveFunction<DoubleVectorIndividual>, Dimensioned
{
    private final int numDimensions;
    
    public SphereObjective(int numDimensions)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("SphereObjective: numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("SphereObjective: numDimensions is infinite, must be finite.");
        this.numDimensions = numDimensions;
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
        double sum = 0;
        for (double d : ind.getVector())
            sum+= Math.pow(d,2);
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
        return String.format("[SphereObjective: NumDimensions=%d]", numDimensions);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SphereObjective))
            return false;
        
        SphereObjective cRef = (SphereObjective) o;
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
