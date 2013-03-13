package SigmaEC.selection;

import SigmaEC.representation.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a function that selects an individual from a single population.
 * Used as part of a Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Selector<T extends Individual>
{     
    protected Selector() { }
    
    public abstract T selectIndividual(List<T> population);
    
    public List<T> selectMultipleIndividuals(List<T> population, int count)
    {
        List<T> output = new ArrayList();
        for (int i = 0; i < count; i++)
            output.add(selectIndividual(population));
        return output;
    }
}
