package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Mutator<T extends Individual> extends ContractObject {   
    public abstract T mutate(final T ind, final int step);
}
