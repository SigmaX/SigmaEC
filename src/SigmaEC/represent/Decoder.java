package SigmaEC.represent;

import SigmaEC.ContractObject;

/**
 * Defines a mapping that converts and individual (genotype) to a phenotype.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Decoder<T extends Individual, P> extends ContractObject {
    /** Compute a phenotype from the given genotype. */
    public abstract P decode(T individual);
}
