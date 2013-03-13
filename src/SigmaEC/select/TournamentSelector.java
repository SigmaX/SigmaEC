package SigmaEC.select;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import java.util.List;
import java.util.Random;

/**
 * Tournament selection.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TournamentSelector<T extends Individual> extends Selector<T>
{
    private int tournamentSize;
    private RandomSelector<T> contestantSelector;
    private ObjectiveFunction<T> objective;
    
    public TournamentSelector(ObjectiveFunction<T> obj, Random random, int tournamentSize) throws NullPointerException, IllegalArgumentException
    {
        if (obj == null)
            throw new NullPointerException("TournamentSelector: obj is null.");
        else if (tournamentSize <= 0)
            throw new IllegalArgumentException("TournamentSelector: tournamentSize is less than 1.");
        
        this.objective = obj;
        this.tournamentSize = tournamentSize;
        this.contestantSelector = new RandomSelector<T>(random);
    }
    
    @Override
    public T selectIndividual(List<T> population) throws NullPointerException, IllegalArgumentException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException("TournamentSelector.selectIndividual: population is empty.");
        List<T> contestants = contestantSelector.selectMultipleIndividuals(population, tournamentSize);
        TruncationSelector<T> finalSelector = new TruncationSelector(this.objective);
        return finalSelector.selectIndividual(contestants);
    }
    
    public boolean repOK()
    {
        return tournamentSize > 0
                && contestantSelector != null
                && objective != null;
    }
}
