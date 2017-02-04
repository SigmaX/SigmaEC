package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.represent.linear.LinearGenomeIndividual;

/**
 * A MutationRate object determines the mutation rate that should be applied to
 * a particular gene in an individual.
 * 
 * @author Eric O. Scott
 */
public abstract class MutationRate extends ContractObject {
    public abstract double getRateForGene(final int gene, final int step, final LinearGenomeIndividual ind);
}
