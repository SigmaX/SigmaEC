package SigmaEC.function;

import SigmaEC.representation.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;

/**
 * Shekel's "foxholes" function, which contains n (traditionally 25) local
 * optima in R^2.  This is the original Shekel function as used by De Jong --
 * its inverse is also commonly used in the literature.  Traditionally, each
 * variable is bounded between -65.536 and 65.536 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class ShekelObjective extends VectorObjectiveFunction<DoubleVectorIndividual>
{
    IDoublePoint[] optima;
    
    /**
     * @param optima The (x,y) coordinates of the local optima.
     */
    public ShekelObjective(IDoublePoint[] optima)
    {
        if (optima == null)
            throw new IllegalArgumentException("ShekelObjective: optima array is null.");
        if (!optimaOK())
            throw new IllegalArgumentException("ShekelObjective: optima array is invalid (perhaps contains nulls).");
        this.optima = optima;
        assert(repOK());
    }

    @Override
    public int getNumDimensions() { return 2; }

    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == 2);
        double sum = 0.002;
        for (int i = 0; i < optima.length; i++)
            sum += 1/(i + (ind.getElement(0) - optima[i].x) + (ind.getElement(1) - optima[i].y));
        assert(repOK());
        return 1/sum;
    }

    @Override
    final public boolean repOK()
    {
        return optima != null
            && optimaOK();
    }
    
    private boolean optimaOK()
    {
        for (IDoublePoint dp : optima)
            if (dp == null)
                return false;
        return true;
    }
}
