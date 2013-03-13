package SigmaEC.represent;

/**
 * Defines an individual who's phenotype is a vector in R^n.  Hard-coding it to
 * use double instead of doing, say, "VectorIndividual<T extends Number>" saves
 * us overhead from auto-boxing.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class DoubleVectorIndividual implements Individual
{
    /** The number of dimensions of the vector. */
    public abstract int size();
    
    /** Returns the element at position i in the vector. */
    public abstract double getElement(int i);
    
    /** Returns a defensive copy of the vector as an array. */
    public abstract double[] getVector();
}
