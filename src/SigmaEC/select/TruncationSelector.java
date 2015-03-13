package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Truncation selection strategy.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TruncationSelector<T extends Individual, P> extends Selector<T> {
    public final static String P_COMPARATOR = "fitnessComparator";
    private final FitnessComparator<T> fitnessComparator;
    
    public TruncationSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }
    
    public TruncationSelector(final FitnessComparator<T> fitnessComparator) {
        assert(fitnessComparator != null);
        this.fitnessComparator = fitnessComparator;
        assert(repOK());
    }
    
    /**
     *  Loops through the population to find the highest fitness individual.
     *  In the case that this individual is not unique, the *latest* individual
     *  with the highest fitness value is chosen.
     */
    @Override
    public T selectIndividual(final List<T> population) throws NullPointerException {
        assert(!population.isEmpty());
        T best = null;
        for (final T ind : population)
            if (fitnessComparator.betterThan(ind, best))
                best = ind;
        assert(best != null);
        assert(repOK());
        return best;
    }
    
    /**
     * Sort the population (nondestructively) by fitness and select the top
     * [count] individuals.
     */
    @Override
    public List<T> selectMultipleIndividuals(final List<T> population, final int numToSelect) throws IllegalArgumentException, NullPointerException {
        assert(population != null);
        assert(!population.isEmpty());
        assert(numToSelect >= 1);
        
        final List<T> sortedPop = new ArrayList(population);
        Collections.sort(sortedPop, fitnessComparator);
        final List<T> topIndividuals = new ArrayList(numToSelect);
        for (int i = 0; i < numToSelect; i++)
            topIndividuals.add(sortedPop.get(sortedPop.size() - 1 - i)); // Best individuals are at the end ofthe array
        assert(repOK());
        return topIndividuals;
    }

    @Override
    public int selectIndividualIndex(final List<T> population) {
        assert(population != null);
        assert(!population.isEmpty());
        assert(!population.isEmpty());
        int bestIndex = -1;
        T best = null;
        for (int i = 0; i < population.size(); i++)
            if (fitnessComparator.betterThan(population.get(i), best)) {
                best = population.get(i);
                bestIndex = i;
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
        assert(count >= 1);
        
        // XXX This approach to getting sorted indices induces much auto-boxing slowness!
        // See here for tips on writing a faster version: http://stackoverflow.com/questions/951848/java-array-sort-quick-way-to-get-a-sorted-list-of-indices-of-an-array
        final Integer[] sortedIndices = new Integer[population.size()];
        for (int i = 0; i < sortedIndices.length; i++)
            sortedIndices[i] = i;
        // Sort from worst to best fitness
        Arrays.sort(sortedIndices, new Comparator<Integer>() { 
            @Override public int compare(final Integer o1, final Integer o2) {
                return fitnessComparator.compare(population.get(o1), population.get(o2));
            }
        });
        final int[] result = new int[count];
        for (int i = 0; i < count; i++)
            result[i] = sortedIndices[sortedIndices.length - i - 1];
        assert(repOK());
        return result;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return fitnessComparator != null
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("[%s: fitnessComparator=%s]", this.getClass().getSimpleName(), fitnessComparator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TruncationSelector))
            return false;
        final TruncationSelector ref = (TruncationSelector)o;
        return fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
}
