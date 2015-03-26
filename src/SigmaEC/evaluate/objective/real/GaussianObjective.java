package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class GaussianObjective extends ObjectiveFunction<DoubleVectorIndividual>
{
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_HEIGHT = "height";
    public final static String P_STD = "std";
    
    private final int numDimensions;
    private final double height;
    private final double std;
    
    public GaussianObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        height = parameters.getDoubleParameter(Parameters.push(base, P_HEIGHT));
        std = parameters.getDoubleParameter(Parameters.push(base, P_STD));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        if (height <= 0.0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": height is <= 0, must be positive.");
        if (std <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": std is <= 0, must be positive.");
        if (Double.isInfinite(std) || Double.isNaN(std))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": std is infinite, must be finite.");
        assert(repOK());
    }
    
    public GaussianObjective(final int numDimensions, final double height, final double std) {
        this.numDimensions = numDimensions;
        this.height = height;
        this.std = std;
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        if (height <= 0.0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": height is <= 0, must be positive.");
        if (std <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": std is <= 0, must be positive.");
        if (Double.isInfinite(std) || Double.isNaN(std))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": std is infinite, must be finite.");
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        double exponent = 0;
        for (double d : ind.getGenomeArray())
            exponent+= Math.pow(d, 2)/(2*Math.pow(std, 2));
        assert(repOK());
        return height*Math.exp(-exponent);
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numDimensions > 0
                && std != Double.POSITIVE_INFINITY
                && !Double.isNaN(std)
                && !Double.isInfinite(std)
                && std > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s: NumDimensions=%d, std=%f, height=%f]", this.getClass().getSimpleName(), numDimensions, std, height);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GaussianObjective))
            return false;
        
        final GaussianObjective cRef = (GaussianObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleEquals(height, cRef.height)
                && Misc.doubleEquals(std, cRef.std);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.numDimensions;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
        return hash;
    }
    //</editor-fold>
}
