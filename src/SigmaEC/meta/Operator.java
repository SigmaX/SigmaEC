package SigmaEC.meta;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * An operator is a generic operation that takes a collection of individuals
 * and applies some transformation to produce a new collection of individuals.
 * 
 * In generational EAs, we can string together a number of generators that apply
 * mutation, take measurements, and perform fitness evaluation in an experiment-specific sequence.
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Operator<T extends Individual> extends ContractObject {  
    public abstract List<T> operate(final int run, final int step, final List<T> parentPopulation);
}
