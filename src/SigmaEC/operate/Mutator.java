package SigmaEC.operate;

import SigmaEC.represent.Gene;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface Mutator<T extends Gene>
{
    /** Non-destructively produce a mutated copy of a Gene. */
    public abstract T mutate(T gene);
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
