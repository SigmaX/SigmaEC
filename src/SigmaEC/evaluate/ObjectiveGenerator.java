package SigmaEC.evaluate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Phenotype;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class ObjectiveGenerator<T extends ObjectiveFunction<P>, P extends Phenotype> {
    public abstract T getNewInstance();
    
    // Force implementation of standard methods
    public abstract boolean repOK();
    @Override public abstract boolean equals(Object o);
    @Override public abstract int hashCode();
    @Override public abstract String toString();
}
