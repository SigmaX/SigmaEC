package SigmaEC.represent;

/**
 * An evolvable individual.  An individual wraps a genome, but also may include
 * supplementary data and/or information about a genotype-to-phenotype mapping.
 * 
 * This is an abstract class instead of an interface in order to force users to
 * override equals(), hashCode() and toString().
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Individual
{
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public abstract boolean repOK();
    
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
    
}
