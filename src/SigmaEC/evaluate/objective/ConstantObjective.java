package SigmaEC.evaluate.objective;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class ConstantObjective<T extends Individual> extends ObjectiveFunction<T, ScalarFitness> {
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
        final Option<Double> valueOpt = parameters.getOptionalDoubleParameter(Parameters.push(base, P_VALUE));
        value = valueOpt.isDefined() ? valueOpt.get() : 1.0;
        assert(repOK());
    }
    
    public ConstantObjective(final int numDimensions, final double fitnessValue) {
        if (numDimensions <= 0)
            throw new IllegalArgumentException(String.format("%s: numDimensions is negative, must be positive", this.getClass().getSimpleName()));
        this.numDimensions = numDimensions;
        this.value = fitnessValue;
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    @Override
    public ScalarFitness fitness(final T ind) {
        assert(repOK());
        return new ScalarFitness(value);
    }

    @Override
    public void setStep(int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_VALUE != null
                && !P_VALUE.isEmpty()
                && numDimensions > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%f]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions,
                P_VALUE, value);
    }
    
    @Override
    public boolean equals(final Object o) {
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
