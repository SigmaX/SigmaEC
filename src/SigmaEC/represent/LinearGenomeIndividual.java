package SigmaEC.represent;

import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class LinearGenomeIndividual<G extends Gene> implements Individual, LinearGenome<G>
{
    /**
     * Constructs a new LinearGenomeIndividual by using the subtype of this as
     * a prototype.  No data is copied from this -- it is only used to determine
     * the subtype.
     * 
     * @param genome The genome to use in the new individual.
     */
    public abstract LinearGenomeIndividual<G> create(List<G> genome);
}
