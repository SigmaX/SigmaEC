package SigmaEC.evaluate.problemclass;

import SigmaEC.evaluate.objective.ObjectiveFunction;

/**
 * Generates arbitrary instances of ObjectiveFunctions that belong to a class of
 * problems.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class ProblemClass<T extends ObjectiveFunction<P>, P> {
    public abstract T getNewInstance();
    
    // Force implementation of standard methods
    public abstract boolean repOK();
    @Override public abstract boolean equals(Object o);
    @Override public abstract int hashCode();
    @Override public abstract String toString();
}
