package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.represent.linear.Gene;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Mutator<G extends Gene> extends ContractObject
{
    /** Non-destructively produce a mutated copy of a Gene. */
    public abstract G mutate(G gene);
}
