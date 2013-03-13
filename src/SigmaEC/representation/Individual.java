package SigmaEC.representation;

/**
 * An evolvable individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Individual
{
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
