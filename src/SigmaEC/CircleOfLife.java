package SigmaEC;

import SigmaEC.represent.Individual;
import java.util.List;
import java.util.Random;

/**
 * A main evolution loop
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class CircleOfLife<T extends Individual> extends ContractObject
{
    /** Takes a population of individuals and evolves them */
    public abstract List<T> evolve(int run, List<T> population);
    
    public interface CircleOfLifeBuilder<T extends Individual> extends BuilderT<CircleOfLife<T>> {
        CircleOfLifeBuilder<T> random(final Random random);
    }
}
