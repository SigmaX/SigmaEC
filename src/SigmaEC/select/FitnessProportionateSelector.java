package SigmaEC.select;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fitness proportionate selection via the "dartboard" method.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessProportionateSelector<T extends Individual> extends Selector<T>
{
    private final Random random;
    private int numContestants;
    private Selector<T> contestantSelector;
    private final ObjectiveFunction<T> objective;
    
    /** Set up a selector that will perform fitness-proportionate selection on
     * the entire population.
     */
    public FitnessProportionateSelector(ObjectiveFunction<T> objective, Random random) throws NullPointerException
    {
        if (objective == null)
            throw new NullPointerException("FitnessProportionalSelector: obj is null.");
        this.objective = objective;
        this.random = random;
        this.numContestants = 0;
        assert(repOK());
    }
    
    /** Set up a selector that will perform fitness-proportionate selection on a
     * random subset of the population.
     */
    public FitnessProportionateSelector(ObjectiveFunction<T> objective, Random random, int poolSize) throws NullPointerException, IllegalArgumentException
    {
        this(objective, random);
        if (poolSize <= 0)
            throw new IllegalArgumentException("FitnessProportionalSelector: poolSize is less than 1.");
        this.numContestants = poolSize;
        this.contestantSelector = new RandomSelector<T>(random);
        assert(repOK());
    }
    
    @Override
    public T selectIndividual(List<T> population) throws NullPointerException, IllegalArgumentException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException("FitnessProportionalSelector.selectIndividual: population is empy.");
        List<T> contestants = new ArrayList(numContestants);
        if (numContestants == 0)
            return chooseFromContestants(population);
        
        for (int i = 0; i < numContestants; i++)
            contestants.add(contestantSelector.selectIndividual(population));
        assert(repOK());
        return chooseFromContestants(contestants);
    }
    
    private T chooseFromContestants(List<T> contestants)
    {
        // TODO The cumulative distribution only needs calculated once for each unique population.
        double totalFitness = 0.0;
        for (int i = 0; i < contestants.size(); i++)
            totalFitness += objective.fitness(contestants.get(i));
        double dart = random.nextDouble();
        double binUpperBound = 0.0;
        for (int i = 0; i < contestants.size(); i++)
        {
            binUpperBound = binUpperBound + objective.fitness(contestants.get(i))/totalFitness;
            if (dart <= binUpperBound)
                return contestants.get(i);
        }
        return contestants.get(contestants.size()-1); // Happens occassionally because of conversion error in the last bin
    }
    
    @Override
    final public boolean repOK()
    {
        return random != null
                && (numContestants > 0 && contestantSelector != null)
                    || (numContestants == 0 && contestantSelector == null)
                && objective != null;
    }
}
