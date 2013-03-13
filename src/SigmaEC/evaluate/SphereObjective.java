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
public class SphereObjective extends VectorObjectiveFunction<DoubleVectorIndividual>
{
    private final int numDimensions;
    
    public SphereObjective(int numDimensions)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("SphereObjective: numDimensions is < 1.");
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

    @Override
    final public boolean repOK()
    {
        return numDimensions > 0;
    }

}
