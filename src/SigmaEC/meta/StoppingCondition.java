package SigmaEC.meta;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;

/**
 * A StoppingCondition is used by a CircleOfLife to decide when to stop an evolutionary run.
 * 
 * @author Eric O. Scott
 */
public abstract class StoppingCondition<T extends Individual<F>, F extends Fitness> extends ContractObject {
    /** Determine whether it's time to stop evolving this population. */
    public abstract boolean stop(final Population<T, F> population, final int step);
    /** Determine whether it's time to stop evolving this individual.  This is a convenience function for trajectory methods. */
    public abstract boolean stopInd(final T individual, final int step);
    /** Reset any mutable state between runs. */
    public abstract void reset();
}
