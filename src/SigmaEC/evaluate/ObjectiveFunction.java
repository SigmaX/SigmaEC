package SigmaEC.evaluate;

import SigmaEC.represent.Phenotype;

/**
 * Objective function interface for the Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface ObjectiveFunction<T extends Phenotype>
{
    /** Evaluate the fitness of an individual. */
    public double fitness(T ind);
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
