package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.represent.linear.Gene;
import SigmaEC.represent.linear.LinearGenomeIndividual;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Mutator<T extends LinearGenomeIndividual<G>, G extends Gene> extends ContractObject {   
    public abstract T mutate(final T ind);
}
