package SigmaEC;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A generator is a generic operation that takes a population of individuals
 * and applies some transformation to produce a new population of individuals.
 * 
 * In generational EAs, we can string together a number of generators that apply
 * mutation, take measurements, and perform fitness evaluation in an experiment-specific sequence.
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Generator<T extends Individual> extends ContractObject {  
    public abstract List<T> produceGeneration(final List<T> parentPopulation);
}
