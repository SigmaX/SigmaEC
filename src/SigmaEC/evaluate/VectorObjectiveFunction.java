package SigmaEC.evaluate;

import SigmaEC.represent.Individual;

/**
 * An ObjectiveFunction with a certain number of dimensions.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class VectorObjectiveFunction<T extends Individual> implements ObjectiveFunction<T>
{
    /** The number of dimensions the function is defined over. */
    public abstract int getNumDimensions();
}
