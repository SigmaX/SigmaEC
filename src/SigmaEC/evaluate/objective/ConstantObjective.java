package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Misc;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class ConstantObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T> {
    
    private final int numDimensions;
    private final double value;
    
    public ConstantObjective(final int numDimensions, final double fitnessValue)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is infinite, must be finite.");
        this.numDimensions = numDimensions;
        this.value = fitnessValue;
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
        assert(repOK());
        return value;
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numDimensions > 0
                && (!Double.isInfinite(value));
    }

    @Override
    public String toString() {
        return String.format("[%s: NumDimensions=%d, Value=%f]", this.getClass().getSimpleName(), numDimensions, value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantObjective))
            return false;
        
        final ConstantObjective cRef = (ConstantObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleEquals(value, cRef.value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.numDimensions;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }
    //</editor-fold>
}
