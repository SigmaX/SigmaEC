package SigmaEC.evaluate.objective;

import SigmaEC.ContractObject;
import SigmaEC.meta.Fitness;

/**
 * Objective function interface for the Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class ObjectiveFunction<P, F extends Fitness> extends ContractObject
{
    /** Evaluate the fitness of an individual. */
    public abstract F fitness(P ind);
    
    /** Notify this that the generation has changed.
     * This may be used, for instance, to update a dynamically changing
     * landscape.
     */
    public abstract void setStep(int i);
    
    public abstract int getNumDimensions();
}
