package SigmaEC.represent;

/**
 * An immutable gene.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Gene
{
    /** A producer that non-destructively creates a mutated copy of this gene. */
    public Gene mutate();
}
