package SigmaEC.select;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * Defines a probability distribution over a population, i.e. for selection.
 * 
 * @author Eric O. Scott
 */
public abstract class SelectionProbability<T extends Individual> extends ContractObject {
    /** Compute the probability of each individual being selected. */ 
    public abstract double[] probability(final List<T> population);
}
