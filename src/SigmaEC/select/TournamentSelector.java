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
    final private Random random;
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
        this.random = random;
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
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return tournamentSize > 0
                && contestantSelector != null
                && objective != null;
    }
    @Override
    public String toString()
    {
        return String.format("[TournamentSelector: TournamentSize=%d, Random=%s, Objective=%s", tournamentSize, random, objective);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof TournamentSelector))
            return false;
        
        TournamentSelector cRef = (TournamentSelector) o;
        return tournamentSize == cRef.tournamentSize
                && random.equals(cRef.random)
                && objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.tournamentSize;
        hash = 11 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 11 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
