package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Tournament selection.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TournamentSelector<T extends Individual, P> extends Selector<T> {
    public final static String P_TOURNAMENT_SIZE = "tournamentSize";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_RANDOM = "random";
    
    private final int tournamentSize;
    private final Comparator<T> fitnessComparator;
    private final Random random;
    private final RandomSelector<T> contestantSelector;
    
    public TournamentSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        tournamentSize = parameters.getIntParameter(Parameters.push(base, P_TOURNAMENT_SIZE));
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), Comparator.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        contestantSelector = new RandomSelector<T>(random);
        assert(repOK());
    }
    
    @Override
    public T selectIndividual(final List<T> population) {
        assert(population != null);
        assert(!population.isEmpty());
        
        final List<T> contestants = contestantSelector.selectMultipleIndividuals(population, tournamentSize);
        final TruncationSelector<T, P> finalSelector = new TruncationSelector(fitnessComparator);
        final T selectedIndividual = finalSelector.selectIndividual(contestants);
        
        assert(selectedIndividual != null);
        assert(repOK());
        return selectedIndividual;
    }

    @Override
    public List<T> selectMultipleIndividuals(final List<T> population, final int count) {
        assert(population != null);
        assert(!population.isEmpty());
        assert(count >= 0);
        return new ArrayList() {{
            for (int i = 0; i < count; i++)
                add(selectIndividual(population));
        }};
    }

    @Override
    public int selectIndividualIndex(final List<T> population) {
        assert(population != null);
        assert(!population.isEmpty());
        
        final int[] contestants = contestantSelector.selectMultipleIndividualIndices(population, tournamentSize);
        assert(contestants.length == tournamentSize);
        int bestIndex = -1;
        T best = null;
        for (int i = 0; i < contestants.length; i++) {
            final T contestant = population.get(contestants[i]);
            if (fitnessComparator.compare(contestant, best) > 0) {
                best = contestant;
                bestIndex = contestants[i];
            }
        }
        assert(best != null);
        assert(bestIndex >= 0);
        assert(repOK());
        return bestIndex;
    }

    @Override
    public int[] selectMultipleIndividualIndices(final List<T> population, final int count) {
        assert(population != null);
        assert(!population.isEmpty());
        assert(count >= 0);
        final int[] result = new int[count];
        for (int i = 0; i < count; i++)
            result[i] = selectIndividualIndex(population);
        return result;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return tournamentSize > 0
                && contestantSelector != null
                && fitnessComparator != null
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_TOURNAMENT_SIZE != null
                && !P_TOURNAMENT_SIZE.isEmpty();
    }
    @Override
    public String toString() {
        return String.format("[%s: tournamentSize=%d, random=%s, fitnessComparator=%s]", this.getClass().getSimpleName(), tournamentSize, random, fitnessComparator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TournamentSelector))
            return false;
        
        TournamentSelector cRef = (TournamentSelector) o;
        return tournamentSize == cRef.tournamentSize
                && random.equals(cRef.random)
                && fitnessComparator.equals(cRef.fitnessComparator)
                && contestantSelector.equals(cRef.contestantSelector);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.tournamentSize;
        hash = 61 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 61 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 61 * hash + (this.contestantSelector != null ? this.contestantSelector.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
