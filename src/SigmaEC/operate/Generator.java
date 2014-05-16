package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * Takes a population of individuals and creates a new generation.
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Generator<T extends Individual> extends ContractObject
{   
    public abstract List<T> produceGeneration(List<T> parentPopulation);
}
