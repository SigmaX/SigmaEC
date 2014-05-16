package SigmaEC;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A main evolution loop
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class CircleOfLife<T extends Individual> extends ContractObject
{
    /** Takes a population of individuals and evolves them */
    public abstract List<T> evolve(int run, List<T> population);
}
