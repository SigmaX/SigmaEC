package SigmaEC;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;

/**
 * A convenient one-stop container for defining a fitness function and a
 * genotype-to-phenotype mapping.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Problem<T extends Individual, P extends Phenotype> {
    public Decoder<T, P> getDecoder();
    public ObjectiveFunction<P> getObjective();
    
    /** Notify this Problem that the generation has changed.
     * This may be used, for instance, to update a dynamically changing
     * landscape.
     */
    public void setGeneration(int i);
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about this object's internal state. */
    public abstract boolean repOK();
}
