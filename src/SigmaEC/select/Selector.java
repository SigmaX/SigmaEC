package SigmaEC.select;

import SigmaEC.BuilderT;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    
    public interface SelectorBuilder<T extends Individual> extends BuilderT<Selector<T>> {
        SelectorBuilder<T> decoder(final Decoder decoder);
        SelectorBuilder<T> objective(final ObjectiveFunction objective);
        SelectorBuilder<T> random(final Random random);
    }
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public abstract boolean repOK();
}
