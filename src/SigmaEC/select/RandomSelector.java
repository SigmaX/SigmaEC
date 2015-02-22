package SigmaEC.select;

import SigmaEC.SRandom;
import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A Selector that just returns a random individual from the population.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RandomSelector<T extends Individual> extends Selector<T> {
    public final static String P_RANDOM = "random";
    private final Random random;
    
    public RandomSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }
    
    public RandomSelector(final Random random) throws NullPointerException {
        if (random == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": random is null.");
        this.random = random;
        assert(repOK());
    }
    
    @Override
    public T selectIndividual(final List<T> population) {
        assert(population != null);
        assert(!population.isEmpty());
        return population.get(random.nextInt(population.size()));
    }

    @Override
    public int selectIndividualIndex(final List<T> population) {
        assert(population != null);
        assert(!population.isEmpty());
        return random.nextInt(population.size());
    }

    @Override
    public List<T> selectMultipleIndividuals(final List<T> population, final int count)  {
        assert(population != null);
        assert(!population.isEmpty());
        assert(count >= 0);
        return new ArrayList() {{
            for (int i = 0; i < count; i++)
                add(selectIndividual(population));
        }};
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
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RandomSelector))
            return false;
        return random.equals(((RandomSelector)o).random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    
    @Override
    public final boolean repOK() {
        return (random != null);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: random=%s]", this.getClass().getSimpleName(), random);
    }
    // </editor-fold>
}
