package SigmaEC.operate.constraint;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;

/**
 * Checks a constraint on a genome.
 * 
 * @author Eric O. Scott
 */
public abstract class Constraint<T extends Individual> extends ContractObject {
    public abstract boolean isViolated(final T individual);
}
