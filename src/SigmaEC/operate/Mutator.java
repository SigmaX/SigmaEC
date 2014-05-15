package SigmaEC.operate;

import SigmaEC.BuilderT;
import SigmaEC.ContractObject;
import SigmaEC.represent.Gene;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Mutator<G extends Gene> extends ContractObject
{
    /** Non-destructively produce a mutated copy of a Gene. */
    public abstract G mutate(G gene);
    
    public interface MutatorBuilder<G extends Gene> extends BuilderT<Mutator<G>> {
        MutatorBuilder<G> random(final Random random);
    }
}
