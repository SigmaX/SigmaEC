package SigmaEC.selection;

import SigmaEC.function.ObjectiveFunction;
import SigmaEC.representation.Individual;
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
    private Random random;
    private int numContestants;
    private RandomSelector<T> contestantSelector;
    private ObjectiveFunction<T> objective;
    
    public FitnessProportionateSelector(ObjectiveFunction<T> objective, Random random, int poolSize) throws NullPointerException, IllegalArgumentException
    {
        if (objective == null)
            throw new NullPointerException("FitnessProportionalSelector: obj is null.");
        else if (poolSize <= 0)
            throw new IllegalArgumentException("FitnessProportionalSelector: poolSize is less than 1.");
        this.objective = objective;
        this.random = random;
        this.numContestants = poolSize;
        this.contestantSelector = new RandomSelector<T>(random);
    }
    
    @Override
    public T selectIndividual(List<T> population) throws NullPointerException, IllegalArgumentException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException("FitnessProportionalSelector.selectIndividual: population is empy.");
        List<T> contestants = new ArrayList(numContestants);
        for (int i = 0; i < numContestants; i++)
            contestants.add(contestantSelector.selectIndividual(population));
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
    
    public boolean repOK()
    {
        return random != null
                && numContestants > 0
                && contestantSelector != null
                && objective != null;
    }
}
