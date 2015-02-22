package SigmaEC.select;

import SigmaEC.SRandom;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stochastic Universal Sampling.  This must be parameterized with a
 * SelectionProbability defining the probability distribution to sample form.
 * 
 * @author Eric O. Scott
 */
public class SUSSelector<T extends Individual, P> extends Selector<T> {
    public final static String P_PROBABILITY = "selectionProbability";
    public final static String P_RANDOM = "random";
    
    private final SelectionProbability<T> selectionProbability;
    private final Random random;

    public SUSSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        selectionProbability = parameters.getInstanceFromParameter(Parameters.push(base, P_PROBABILITY), SelectionProbability.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }
    
    @Override
    public T selectIndividual(final List<T> population) {
        return population.get(selectMultipleIndividualIndices(population, 1)[0]);
    }

    @Override
    public int selectIndividualIndex(final List<T> population) {
        return selectMultipleIndividualIndices(population, 1)[0];
    }

    @Override
    public List<T> selectMultipleIndividuals(final List<T> population, final int count) {
        assert(population != null);
        assert(population.size() > 0);
        assert(count >= 0);
        return new ArrayList<T>() {{
                final int[] indices = selectMultipleIndividualIndices(population, count);
                for (final int i : indices)
                    add(population.get(i));
        }};
    }

    @Override
    public int[] selectMultipleIndividualIndices(final List<T> population, int count) {
        assert(population != null);
        assert(population.size() > 0);
        assert(count >= 0);
        
        final double[] cdf = populationCDF(population);
        assert(cdf.length == population.size());
        final double max = cdf[cdf.length-1];
        
        final double firstSample = random.nextDouble()*max/count;
        final int[] indices = new int[count];
        int index = 0;
        for (int i = 0; i < count; i++) {
            final double sample = firstSample + max/count*i;
            while (sample > cdf[index]) {
                index++;
                assert(index < cdf.length);
            }
            indices[i] = index;
        }
        assert(repOK());
        return indices;
    }
    
    private double[] populationCDF(final List<T> population) {
        assert(population != null);
        assert(population.size() > 0);
        
        final double[] dist = selectionProbability.probability(population);
        for (int i = 1; i < dist.length; i++)
            dist[i] = dist[i] + dist[i-1];
        assert(Misc.doubleEquals(1.0, dist[dist.length - 1]));
        dist[dist.length - 1] = 1.0; // Make the last element exactly 1, correcting any floating point error.
        return dist;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_PROBABILITY != null
                && !P_PROBABILITY.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && selectionProbability != null
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SUSSelector))
            return false;
        final SUSSelector ref = (SUSSelector) o;
        return random.equals(ref.random)
                && selectionProbability.equals(ref.selectionProbability);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.selectionProbability != null ? this.selectionProbability.hashCode() : 0);
        hash = 37 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: random=%s, selectionProbability=%s]", this.getClass().getSimpleName(), random, selectionProbability);
    }
    // </editor-fold>
}
