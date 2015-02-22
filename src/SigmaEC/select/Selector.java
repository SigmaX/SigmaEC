package SigmaEC.select;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * Interface for a function that selects an individual from a single population.
 * Used as part of a Strategy pattern.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Selector<T extends Individual> extends ContractObject {     
    public abstract T selectIndividual(final List<T> population);
    
    public abstract int selectIndividualIndex(final List<T> population);
    
    public abstract List<T> selectMultipleIndividuals(final List<T> population, final int count);
    
    public abstract int[] selectMultipleIndividualIndices(final List<T> population, final int count);
}
