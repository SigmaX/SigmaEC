package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 * Several (at lease two) narrow multi-variate Gaussian distributions,
 * where the major axes are aligned orthogonally to one another.
 * All the Gaussians have their optima at the same point.
 * 
 * @author Jeff Bassett
 * @author Eric 'Siggy' Scott
 */
public class PhillipsHeadObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_NUM_DIMENSIONS = "numDimensions";
    private final static String P_SHORT_AXIS = "shortAxis";
    private final static String P_LONG_AXIS = "longAxis";
    
    private final int numDimensions;
    private final double shortAxisFactor;
    private final double longAxisFactor;

    public PhillipsHeadObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        this.shortAxisFactor = parameters.getIntParameter(Parameters.push(base, P_SHORT_AXIS));
        this.longAxisFactor = parameters.getIntParameter(Parameters.push(base, P_LONG_AXIS));
        
        final String funcName = this.getClass().getSimpleName();
        if (numDimensions < 1)
            throw new IllegalArgumentException(funcName +
                            ": numDimensions is < 1.");
        if (Double.isInfinite(longAxisFactor) || Double.isNaN(longAxisFactor))
            throw new IllegalArgumentException(funcName +
                            ": longAxisFactor is infinite, must be finite.");
        if (Double.isInfinite(shortAxisFactor) || Double.isNaN(shortAxisFactor))
            throw new IllegalArgumentException(funcName +
                            ": shortAxisFactor is infinite, must be finite.");
        if (longAxisFactor <= 0)
            throw new IllegalArgumentException(funcName +
                            ": longAxisFactor is <= 0.");
        if (shortAxisFactor <= 0)
            throw new IllegalArgumentException(funcName +
                            ": shortAxisFactor is <= 0.");
        if (longAxisFactor <= shortAxisFactor)
            throw new IllegalArgumentException(funcName +
                            ": longAxisFactor is <= shortAxisFactor.");
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
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
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);

        double result = 0;
        final double s = this.shortAxisFactor;
        final double l = this.longAxisFactor;
        int d, longAxis;

        for (longAxis = 0; longAxis < this.getNumDimensions(); longAxis++) {
            double interResult = 1.0;
            for (d = 0; d < this.getNumDimensions(); d++) {
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
    public void setGeneration(final int i) {
        // Do nothing
    }


    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numDimensions > 0
                && !Double.isInfinite(shortAxisFactor)
                && !Double.isNaN(shortAxisFactor)
                && shortAxisFactor > 0
                && !Double.isInfinite(longAxisFactor)
                && !Double.isNaN(longAxisFactor)
                && longAxisFactor > 0
                && longAxisFactor > shortAxisFactor;
    }


    @Override
    public String toString() {
        return String.format(
        "[%s: numDimensions=%d, shortAxisFactor=%f, longAxisFactor=%f]",
         this.getClass().getSimpleName(), numDimensions, shortAxisFactor, longAxisFactor);
    }
    

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PhillipsHeadObjective))
            return false;
        
        final PhillipsHeadObjective cRef = (PhillipsHeadObjective) o;
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
