package SigmaEC.evaluate;

import SigmaEC.represent.Phenotype;

/**
 * Objective function interface for the Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface ObjectiveFunction<P extends Phenotype>
{
    /** Evaluate the fitness of an individual. */
    public double fitness(P ind);
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the function's internal state. */
    public boolean repOK();
}
