package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;

/**
 * Several (at lease two) narrow multi-variate Gaussian distributions,
 * where the major axes are aligned orthogonally to one another.
 * All the Gaussians have their optima at the same point.
 * 
 * @author Jeff Bassett
 */
public class PhillipsHeadObjective
        implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final int numDimensions;
    private final double shortAxisFactor;
    private final double longAxisFactor;

    public PhillipsHeadObjective(int numDimensions,
                                 double shortAxisFactor,
                                 double longAxisFactor)
    {
        String funcName = "PhillipsHeadObjective";
        if (numDimensions < 1)
            throw new IllegalArgumentException(funcName +
                            ": numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException(funcName +
                            ": numDimensions is infinite, must be finite.");
        if (longAxisFactor <= 0)
            throw new IllegalArgumentException(funcName +
                            ": longAxisFactor is <= 0.");
        if (shortAxisFactor <= 0)
            throw new IllegalArgumentException(funcName +
                            ": shortAxisFactor is <= 0.");
        if (longAxisFactor <= shortAxisFactor)
            throw new IllegalArgumentException(funcName +
                            ": longAxisFactor is <= shortAxisFactor.");
        
        this.numDimensions = numDimensions;
        this.shortAxisFactor = shortAxisFactor;
        this.longAxisFactor = longAxisFactor;
        assert(repOK());
    }


    public PhillipsHeadObjective() throws IllegalArgumentException
    {
        this(2, 0.1, 1);
    }

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }


    /**
     * Calculate fitness for multiple crossing thin Gaussians. The form is:
     *   f(x,y) = max(exp(-x*x/s) * exp(-y*y/l),
     *                exp(-x*x/l) * exp(-y*y/s))
     *
     * where s = shortAxisFactor and l = longAxisFactor.
     */
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        assert(ind.size() == numDimensions);

        double result = 0;
        double s = this.shortAxisFactor;
        double l = this.longAxisFactor;
        int d, longAxis;

        for (longAxis = 0; longAxis < this.getNumDimensions(); longAxis++)
        {
            double interResult = 1.0;
            for (d = 0; d < this.getNumDimensions(); d++)
            {
                double x = ind.getElement(d);
                if (d == longAxis)
                    interResult = interResult * Math.exp(-x*x/l);
                else
                    interResult = interResult * Math.exp(-x*x/s);
            }
            result = Math.max(result, interResult);
        }

        assert(repOK());
        return result;
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }


    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return numDimensions > 0
                && shortAxisFactor > 0
                && longAxisFactor > 0
                && longAxisFactor > shortAxisFactor;
    }


    @Override
    public String toString()
    {
        return String.format(
        "[PhillipsHead: NumDimensions=%d, shortAxisFactor=%f, longAxisFactor=%f]",
         numDimensions, shortAxisFactor, longAxisFactor);
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof PhillipsHeadObjective))
            return false;
        
        PhillipsHeadObjective cRef = (PhillipsHeadObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleEquals(shortAxisFactor, cRef.shortAxisFactor)
                && Misc.doubleEquals(longAxisFactor, cRef.longAxisFactor);
    }


    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        // I think this is the right way to do it.  JKB
        hash = 97 * hash + (int)(Double.doubleToLongBits(this.shortAxisFactor) ^
            (Double.doubleToLongBits(this.shortAxisFactor) >>> 32));
        hash = 97 * hash + (int)(Double.doubleToLongBits(this.longAxisFactor) ^
            (Double.doubleToLongBits(this.longAxisFactor) >>> 32));
        return hash;
    }
    //</editor-fold>
}
