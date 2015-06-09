package SigmaEC.represent.linear;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * An individual based on a List of Genes.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class LinearGenomeIndividual<G extends Gene> extends Individual
{
    /**
     * Constructs a new LinearGenomeIndividual by using the subtype of this as
     * a prototype.  No data is copied from this -- it is only used to determine
     * the subtype.
     * 
     * @param genome The genome to use in the new individual.
     */
    public abstract LinearGenomeIndividual<G> create(final List<G> genome, final List<? extends Individual> parents);
    
    /** Return a defensive copy of the genome. */
    public abstract List<G> getGenome();
    
    public abstract int size();
}
