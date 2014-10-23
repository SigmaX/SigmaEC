package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fitness proportionate selection via the "dartboard" method.
 * 
 * XXX WARNING: This is currently hard-coded for fitness maximization.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessProportionateSelector<T extends Individual, P> extends Selector<T>
{
    private final Random random;
    private int numContestants;
    private final Decoder<T, P> decoder;
    private Selector<T> contestantSelector;
    private final ObjectiveFunction<P> objective;
    
    /** Set up a selector that will perform fitness-proportionate selection on
     * the entire population.
     */
    public FitnessProportionateSelector(final ObjectiveFunction<P> objective, final Decoder<T, P> decoder, final Random random) throws NullPointerException
    {
        if (objective == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": obj is null.");
        if (decoder == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": decoder is null.");
        if (random == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": random is null.");
        this.objective = objective;
        this.decoder = decoder;
        this.random = random;
        this.numContestants = 0;
        assert(repOK());
    }
    
    /** Set up a selector that will perform fitness-proportionate selection on a
     * random subset of the population.
     */
    public FitnessProportionateSelector(final ObjectiveFunction<P> objective, final Decoder<T, P> decoder, final Random random, final int poolSize) throws NullPointerException, IllegalArgumentException
    {
        this(objective, decoder, random);
        if (poolSize <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": poolSize is less than 1.");
        this.numContestants = poolSize;
        this.contestantSelector = new RandomSelector<T>(random);
        assert(repOK());
    }
    
    @Override
    public T selectIndividual(final List<T> population) throws NullPointerException, IllegalArgumentException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".selectIndividual: population is empy.");
        List<T> contestants = new ArrayList(numContestants);
        if (numContestants == 0)
            return chooseFromContestants(population);
        
        for (int i = 0; i < numContestants; i++)
            contestants.add(contestantSelector.selectIndividual(population));
        assert(repOK());
        return chooseFromContestants(contestants);
    }
    
    private T chooseFromContestants(final List<T> contestants)
    {
        // TODO The cumulative distribution only needs calculated once for each unique population.
        double totalFitness = 0.0;
        for (int i = 0; i < contestants.size(); i++)
            totalFitness += objective.fitness(decoder.decode(contestants.get(i)));
        double dart = random.nextDouble();
        double binUpperBound = 0.0;
        for (int i = 0; i < contestants.size(); i++)
        {
            binUpperBound = binUpperBound + objective.fitness(decoder.decode(contestants.get(i)))/totalFitness;
            if (dart <= binUpperBound)
                return contestants.get(i);
        }
        return contestants.get(contestants.size()-1); // Happens occassionally because of conversion error in the last bin
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return random != null
                && (numContestants > 0 && contestantSelector != null)
                    || (numContestants == 0 && contestantSelector == null)
                && objective != null;
    }
    
    @Override
    public String toString()
    {
        return "[FitnessProportionateSelector: Random=" + random.toString() + "]";
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof FitnessProportionateSelector))
            return false;
        
        FitnessProportionateSelector cRef = (FitnessProportionateSelector) o;
        return random == cRef.random
                && numContestants == cRef.numContestants
                && ((contestantSelector == null && cRef.contestantSelector == null)
                    || (contestantSelector.equals(cRef.contestantSelector)))
                && objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 13 * hash + this.numContestants;
        hash = 13 * hash + (this.contestantSelector != null ? this.contestantSelector.hashCode() : 0);
        hash = 13 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
}
