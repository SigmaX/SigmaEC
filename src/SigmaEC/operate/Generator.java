package SigmaEC.operate;

import SigmaEC.BuilderT;
import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;
import java.util.Random;

/**
 * Takes a population of individuals and creates a new generation.
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Generator<T extends Individual> extends ContractObject
{   
    public abstract List<T> produceGeneration(List<T> parentPopulation);
    
    public interface GeneratorBuilder<T extends Individual> extends BuilderT<Generator<T>> {
        GeneratorBuilder<T> random(final Random random);
    }
}
