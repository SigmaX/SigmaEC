package SigmaEC.represent;

/**
 * An evolvable individual.  An individual wraps a genome, but also may include
 * supplementary data and/or information about a genotype-to-phenotype mapping.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Individual
{
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
