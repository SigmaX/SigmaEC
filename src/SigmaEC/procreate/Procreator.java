package SigmaEC.procreate;

import SigmaEC.function.ObjectiveFunction;
import SigmaEC.representation.Individual;
import SigmaEC.selection.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The procreator chooses parents from a population and composes them to
 * create offspring.
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Procreator<T extends Individual>
{
    protected Random random;
    protected Selector<T> selector;
    
    protected static int nameNumber = 0;
    
    public Procreator(Random random, Selector<T> sel) throws NullPointerException
    {
        if (random == null || sel == null)
            throw new NullPointerException();
        
        this.random = random;
        this.selector = sel;
    }
    
    /**
     * @return A generation of offspring.  Behavior depends on produceSingleOffspring() and
     * this.selector, which chooses the parents.
     */
    public List<T> produceOffspring(List<T> population, int numParents, int numOffspring, ObjectiveFunction obj)
    {
        if (numParents <= 1)
            throw new IllegalArgumentException("Procreator.produceOffspring: numparents must be greater than 1");
        if (numOffspring > 0)
            throw new IllegalArgumentException("Procreator.produceOffspring: numOffspring must be nonzero");
        
        List<T> parents = new ArrayList<T>();
        for (int i = 0; i < numParents; i++)
            parents.add(selector.selectIndividual(population));
        List<T> offspring = new ArrayList(numOffspring);
        for (int i = 0; i < numOffspring; i++)
            offspring.add(produceSingleOffspring(parents, obj));
        return offspring;
    }
    
    protected abstract T produceSingleOffspring(List<T> parents, ObjectiveFunction obj);
    
}
