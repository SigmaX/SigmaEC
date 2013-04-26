package SigmaEC.represent;

import java.util.List;

/**
 * An individual based on a List of Genes.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface LinearGenomeIndividual<G extends Gene> extends Individual
{
    /**
     * Constructs a new LinearGenomeIndividual by using the subtype of this as
     * a prototype.  No data is copied from this -- it is only used to determine
     * the subtype.
     * 
     * @param genome The genome to use in the new individual.
     */
    public abstract LinearGenomeIndividual<G> create(List<G> genome);
    
    public abstract List<G> getGenome();
}
