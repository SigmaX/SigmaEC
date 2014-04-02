package SigmaEC.operate;

import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import java.util.List;

/**
 * Takes a population of individuals and creates a new generation.
 *
 * @author Eric 'Siggy' Scott
 */
public interface Generator<T extends Individual>
{   
    public List<T> produceGeneration(List<T> parentPopulation);
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
    
}
