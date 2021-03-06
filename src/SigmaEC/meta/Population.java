package SigmaEC.meta;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class Population<T extends Individual<F>, F extends Fitness> extends ContractObject {
    private final List<T>[] subpopulations;
    
    public Population(final int numSubPopulations, final Initializer<T> initializer) {
        assert(numSubPopulations > 0);
        subpopulations = new List[numSubPopulations];
        for (int i = 0; i < numSubPopulations; i++)
            subpopulations[i] = initializer.generatePopulation();
        assert(repOK());
    }
    
    public Population(final List<T>[] subpopulations) {
        this.subpopulations = new List[subpopulations.length];
        for (int i = 0; i < subpopulations.length; i++)
            this.subpopulations[i] = new ArrayList<>(subpopulations[i]); // Defensive copy
        assert(repOK());
    }
    
    public Population(final List<T> population) {
        assert(population != null);
        this.subpopulations = new List[] { new ArrayList<>(population) }; // Defensive copy
        assert(repOK());
    }
    
    /** @return A defensive copy of the ith subpopulation. */
    public List<T> getSubpopulation(final int i) {
        assert(i >= 0);
        assert(i < subpopulations.length);
        return new ArrayList<>(subpopulations[i]);
    }
    
    public synchronized void setSubpopulation(final int i, final List<T> population) {
        assert(i >= 0);
        assert(i < subpopulations.length);
        assert(population != null);
        assert(!population.isEmpty());
        assert(!Misc.containsNulls(population));
        subpopulations[i] = population;
        assert(repOK());
    }
    
    public synchronized void set(final int subpop, final int i, final T ind) {
        assert(subpop >= 0);
        assert(subpop < subpopulations.length);
        assert(i >= 0);
        assert(i < subpopulations[subpop].size());
        subpopulations[subpop].set(i, ind);
    }
    
    /** 
     * @return The best individual across all subpopulations.
     * 
     * @param comparator A ScalarFitnessComparator that defines what it means for one individual to be "better" than another.
     */
    public T getBest(final FitnessComparator<T, F> comparator) {
        assert(comparator != null);
        T best = subpopulations[0].get(0);
        for (final List<T> subpopulation : subpopulations) {
            for (final T val : subpopulation) {
                if (comparator.betterThan(val, best))
                    best = val;
            }
        }
        assert(repOK());
        return best;
    }
    
    /**
     * @return The best individual from a particular subpopulation.
     * 
     * @param subpopulation The subpopulation to consider.
     * @param comparator A ScalarFitnessComparator that defines what it means for one individual to be "better" than another.
     */
    public T getBest(final int subpopulation, final FitnessComparator<T, F> comparator) {
        assert(subpopulation >= 0);
        assert(subpopulation < subpopulations.length);
        assert(comparator != null);
        T best = subpopulations[subpopulation].get(0);
        for (final T val : subpopulations[subpopulation]) {
            if (comparator.betterThan(val, best))
                best = val;
        }
        assert(repOK());
        return best;
    }
    
    /** 
     * @return The worst individual across all subpopulations.
     * 
     * @param comparator A ScalarFitnessComparator that defines what it means for one individual to be "better" than another.
     */
    public T getWorst(final FitnessComparator<T, F> comparator) {
        assert(comparator != null);
        return getBest(comparator.invert());
    }
    
    /**
     * @return The worst individual from a particular subpopulation.
     * 
     * @param subpopulation The subpopulation to consider.
     * @param comparator A ScalarFitnessComparator that defines what it means for one individual to be "better" than another.
     */
    public T getWorst(final int subpopulation, final FitnessComparator<T, F> comparator) {
        assert(subpopulation >= 0);
        assert(subpopulation < subpopulations.length);
        assert(comparator != null);
        return getBest(subpopulation, comparator.invert());
    }
    
    public int numSuppopulations() {
        return subpopulations.length;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return subpopulations != null
                && subpopulations.length > 0
                && !Misc.containsNulls(subpopulations)
                && subpopsOkay(subpopulations);
                
    }
    
    private final boolean subpopsOkay(final List<T>[] subpopulations) {
        assert(subpopulations != null);
        for (int i = 0; i < subpopulations.length; i++)
            if (Misc.containsNulls(subpopulations[i]))
                return false;
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Population))
            return false;
        final Population ref = (Population)o;
        return Arrays.equals(subpopulations, ref.subpopulations);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Arrays.deepHashCode(this.subpopulations);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numSubpopulations=%d]", this.getClass().getSimpleName(), subpopulations.length);
    }
    // </editor-fold>
}
