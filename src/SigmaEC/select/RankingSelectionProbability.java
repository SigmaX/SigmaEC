package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class RankingSelectionProbability<T extends Individual, P> extends SelectionProbability<T> {
    public final static String P_POWER = "power";
    public final static String P_COMPARATOR = "comparator";
    final public static String P_MINIMIZE = "minimize";
    
    final private boolean minimize;
    private final int power;
    private final Comparator<T> fitnessComparator;
    
    public RankingSelectionProbability(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        power = parameters.getIntParameter(Parameters.push(base, P_POWER));
        if (power < 0)
            throw new IllegalStateException(String.format("%s: power is %d, must be >= 0.", this.getClass().getSimpleName(), power));
        final Option<Boolean> minimizeOpt = parameters.getOptionalBooleanParameter(Parameters.push(base, P_MINIMIZE));
        minimize = minimizeOpt.isDefined() ? minimizeOpt.get() : false;
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), Comparator.class);
        assert(repOK());
    }
    
    @Override
    public double[] probability(final List<T> population) {
        assert(population != null);
        
        final Integer[] sortedIndices = sortByFitness(population);
        final int[] rank = sortedIndicesToRanks(sortedIndices);
        assert(rank.length == population.size());
        final double[] p = new double[rank.length];
        final int n = population.size();
        double sum = 0;
        for (int i = 0; i < p.length; i++) {
            p[i] = (double) (power + 1)/Math.pow(n, power + 1) * Math.pow(rank[i] + 1, power);
            sum += p[i];
        }
        for (int i = 0; i < p.length; i++) {
            p[i] /= sum;
            if (minimize)
                p[i] = 1.0 - p[i];
        }
        assert(repOK());
        return p;
    }
    
    /** Use fitnessComparator to sort a population by fitness.
     * @return An array A, such that A[i] is the index in the population of
     *     the individual with rank i.  */
    private Integer[] sortByFitness(final List<T> population) {
        assert(population != null);
        // XXX Much auto-boxing slowness!
        final Integer[] sortedIndices = new Integer[population.size()];
        for (int i = 0; i < sortedIndices.length; i++)
            sortedIndices[i] = i;
        // Sort from worst to best fitness
        Arrays.sort(sortedIndices, new Comparator<Integer>() { 
            @Override public int compare(final Integer o1, final Integer o2) {
                return fitnessComparator.compare(population.get(o1), population.get(o2));
            }
        });
        return sortedIndices;
    }
    
    /** 
     * @param sortedIndices An array A, such that A[i] is the index in the population
     * of the individual with rank i. 
     * @return An array B, such that B[j] is the rank of the jth individual in the population. */
    private int[] sortedIndicesToRanks(final Integer[] sortedIndices) {
        assert(sortedIndices != null);
        final int[] ranks = new int[sortedIndices.length];
        for (int rank = 0; rank < sortedIndices.length; rank++)
            ranks[sortedIndices[rank]] = rank;
        return ranks;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_POWER != null
                && !P_POWER.isEmpty()
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && power >= 0
                && fitnessComparator != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(this instanceof RankingSelectionProbability))
            return false;
        final RankingSelectionProbability ref = (RankingSelectionProbability)o;
        return power == ref.power
                && fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.power;
        hash = 19 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s]", this.getClass().getSimpleName(),
                P_POWER, power,
                P_COMPARATOR, fitnessComparator);
    }
    // </editor-fold>
}
