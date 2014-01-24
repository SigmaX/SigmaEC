package SigmaEC.select;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Truncation selection strategy.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TruncationSelector<T extends Individual, P extends Phenotype> extends Selector<T>
{
    private final ObjectiveFunction<P> objective;
    private final Decoder<T, P> decoder;

    public ObjectiveFunction<P> getObjective()
    {
        return objective;
    }
    
    public TruncationSelector(final ObjectiveFunction<P> objective, final Decoder<T, P> decoder)
    {
        super();
        if (objective == null)
            throw new IllegalArgumentException("TruncationSelector(): objective is null.");
        if (decoder == null)
            throw new IllegalArgumentException("TruncationSelector(): decoder is null.");
        this.objective = objective;
        this.decoder = decoder;
    }
    
    /**
     *  Loops through the population to find the highest fitness individual.
     *  In the case that this individual is not unique, the *latest* individual
     *  with the highest fitness value is chosen.
     */
    @Override
    public T selectIndividual(final List<T> population) throws NullPointerException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException("TruncationSelector.selectMultipleIndividuals(): population is empty.");
        
        double bestFitness = Double.NEGATIVE_INFINITY;
        T best = null;
        for (T ind : population)
        {
            double fitness = objective.fitness(decoder.decode(ind));
            if (fitness >= bestFitness)
            {
                bestFitness = fitness;
                best = ind;
            }
        }
        assert(best != null);
        return best;
    }
    
    /**
     * Sort the population (nondestructively) by fitness and select the top
     * [count] individuals.
     */
    @Override
    public List<T> selectMultipleIndividuals(final List<T> population, final int numToSelect) throws IllegalArgumentException, NullPointerException
    {
        if (numToSelect < 1)
            throw new IllegalArgumentException("TruncationSelector.selectMultipleIndividuals(): numToSelect is zero.");
        else if (population.isEmpty())
            throw new IllegalArgumentException("TruncationSelector.selectMultipleIndividuals(): population is empty.");
        else if (numToSelect > population.size())
            throw new IllegalArgumentException("TruncationSelector.selectMultipleIndividuals(): numToSelect is greater than population size.");
        
        final List<T> sortedPop = new ArrayList(population);
        Collections.sort(sortedPop, new fitnessComparator());
        final List<T> topIndividuals = new ArrayList(numToSelect);
        for (int i = 0; i < numToSelect; i++)
            topIndividuals.add(sortedPop.get(sortedPop.size() - 1 - i));
        return topIndividuals;
    }
    
    @Override
    final public boolean repOK()
    {
        return (objective != null);
    }
    
    private class fitnessComparator implements Comparator<T>
    {
        @Override
        public int compare(T ind1, T ind2)
        {
            final double x = objective.fitness(decoder.decode(ind1));
            final double y = objective.fitness(decoder.decode(ind2));
            
            if (x > y)
                return 1;
            if (x < y)
                return -1;
            return 0;
        }
        
    }
}
