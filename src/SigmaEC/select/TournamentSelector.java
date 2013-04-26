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
    final private int tournamentSize;
    final private RandomSelector<T> contestantSelector;
    final private ObjectiveFunction<? super T> objective;
    
    public TournamentSelector(ObjectiveFunction<? super T> obj, Random random, int tournamentSize) throws NullPointerException, IllegalArgumentException
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
        T selectedIndividual = finalSelector.selectIndividual(contestants);
        assert(selectedIndividual != null);
        assert(repOK());
        return selectedIndividual;
    }
    
    @Override
    final public boolean repOK()
    {
        return tournamentSize > 0
                && contestantSelector != null
                && objective != null;
    }
}
