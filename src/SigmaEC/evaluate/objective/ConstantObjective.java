package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class ConstantObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_VALUE = "fitnessValue";

    private final int numDimensions;
    private final double value;
    
    public ConstantObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        this.value = parameters.getDoubleParameter(Parameters.push(base, P_VALUE));
        
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
        return numDimensions > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, value=%f]", this.getClass().getSimpleName(), numDimensions, value);
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
