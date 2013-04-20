package SigmaEC.select;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A selector that simply iterates through the population, in order.
 * 
 * @author Eric 'Siggy' Scott
 */
public class IterativeSelector<T extends Individual> extends Selector<T>
{
    private int i = 0;
    
    public IterativeSelector()
    {
        
    }
    
    @Override
    public T selectIndividual(List<T> population) {
        return population.get(i++%population.size());
    }

    @Override
    public boolean repOK() {
        return i >= 0;
    }
    
}
