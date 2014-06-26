package SigmaEC;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class ContractObject {
    /** Representation invariant.  If this returns false, there is something
     * invalid about this object's internal state. */
    public abstract boolean repOK();
    @Override public abstract boolean equals(Object o);
    @Override public abstract int hashCode();
    @Override public abstract String toString();
}
