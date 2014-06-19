package SigmaEC.evaluate.objective;

import SigmaEC.ContractObject;

/**
 * Objective function interface for the Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class ObjectiveFunction<P> extends ContractObject
{
    /** Evaluate the fitness of an individual. */
    public abstract double fitness(P ind);
    
    /** Notify this that the generation has changed.
     * This may be used, for instance, to update a dynamically changing
     * landscape.
     */
    public abstract void setGeneration(int i);
    
    public abstract int getNumDimensions();
}
