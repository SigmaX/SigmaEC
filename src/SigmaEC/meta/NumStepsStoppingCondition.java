package SigmaEC.meta;

import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class NumStepsStoppingCondition<T extends Individual<F>, F extends Fitness> extends StoppingCondition<T, F> {
    public final static String P_NUM_STEPS = "numSteps";
    public final static String P_PROGRESS_BAR = "progressBar";
    private final static int NUM_PROGRESS_BAR_BLOCKS = 50;
    
    private final int numSteps;
    private final boolean progressBar;
    private final int stepsPerProgressBarBlock;
    
    public NumStepsStoppingCondition(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numSteps = parameters.getIntParameter(Parameters.push(base, P_NUM_STEPS));
        if (numSteps < 0)
            throw new IllegalStateException(String.format("%s: %s is negative, must be >= 0.", this.getClass().getSimpleName(), P_NUM_STEPS));
        progressBar = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PROGRESS_BAR), false);
        stepsPerProgressBarBlock = numSteps/NUM_PROGRESS_BAR_BLOCKS;
        assert(repOK());
    }
    
    @Override
    public boolean stop(final Population<T, F> population, int step) {
        assert(step >= 0);
        assert(repOK());
        if (progressBar && (step % stepsPerProgressBarBlock == 0)) {
            System.err.print("\r");
            System.err.print(progressBar(step/stepsPerProgressBarBlock, NUM_PROGRESS_BAR_BLOCKS));
        }
        return step >= numSteps;
    }

    @Override
    public boolean stopInd(final T individual, final int step) {
        return stop(null, step);
    }
    
    private static String progressBar(final int complete, final int total) {
        final StringBuilder sb = new StringBuilder("[");
        int i = 0;
        for (; i < complete; i++)
            sb.append("=");
        for (; i < total; i++)
            sb.append(" ");
        sb.append("]");
        return sb.toString();
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
