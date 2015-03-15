package SigmaEC;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A StoppingCondition is used by a CircleOfLife to decide when to stop an evolutionary run.
 * 
 * @author Eric O. Scott
 */
public abstract class StoppingCondition<T extends Individual> extends ContractObject {
    public abstract boolean stop(final List<T> population, final int step);
    /** Reset any mutable state between runs. */
    public abstract void reset();
}
