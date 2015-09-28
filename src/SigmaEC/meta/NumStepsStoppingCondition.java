package SigmaEC.meta;

import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class NumStepsStoppingCondition<T extends Individual> extends StoppingCondition<T> {
    public final static String P_NUM_STEPS = "numSteps";
    
    private final int numSteps;
    
    public NumStepsStoppingCondition(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numSteps = parameters.getIntParameter(Parameters.push(base, P_NUM_STEPS));
        if (numSteps < 0)
            throw new IllegalStateException(String.format("%s: %s is negative, must be >= 0.", this.getClass().getSimpleName(), P_NUM_STEPS));
        assert(repOK());
    }
    @Override
    public boolean stop(final Population<T> population, int step) {
        assert(step >= 0);
        assert(repOK());
        return step >= numSteps;
    }

    @Override
    public void reset() { /* Do nothing. */ }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_STEPS != null
                && !P_NUM_STEPS.isEmpty()
                && numSteps >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof NumStepsStoppingCondition))
            return false;
        final NumStepsStoppingCondition ref = (NumStepsStoppingCondition)o;
        return numSteps == ref.numSteps;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.numSteps;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_NUM_STEPS, numSteps);
    }
    // </editor-fold>
}
