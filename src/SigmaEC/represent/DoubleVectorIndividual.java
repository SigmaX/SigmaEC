package SigmaEC.represent;

/**
 * Methods to associate a vector of doubles with the state of a class.  This is
 * used, for instance, to provide a phenotype to an individual that can be
 * represented in R^n.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface DoubleVectorIndividual extends Individual
{
    /** The number of dimensions of the vector. */
    public abstract int size();
    
    /** Returns the element at position i in the vector. */
    public abstract double getElement(int i);
    
    /** Returns a defensive copy of the vector as an array. */
    public abstract double[] getVector();
}
