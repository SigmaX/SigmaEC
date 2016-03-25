package SigmaEC.operate;

import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public abstract class MutatingOperator<T extends Individual> extends Operator<T> {
    /** Apply mutation to a single individual. */
    public abstract T operateInd(T ind);
}
