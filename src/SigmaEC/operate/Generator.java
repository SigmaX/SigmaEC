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
    public Selector<T> getParentSelector();
    
    public List<T> produceGeneration(List<T> parentPopulation);
    
    public boolean repOK();
    
}
