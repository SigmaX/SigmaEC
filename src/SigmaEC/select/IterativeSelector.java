package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * A selector that simply iterates through the population, in order.
 * 
 * @author Eric 'Siggy' Scott
 */
public class IterativeSelector<T extends Individual> extends Selector<T> {
    private int i = 0;
    
    public IterativeSelector(final Parameters parameters, final String base) { assert(repOK()); }
    
    public IterativeSelector() {}
    
    @Override
    public T selectIndividual(final List<T> population) {
        return population.get(i++%population.size());
    }

    @Override
    public int selectIndividualIndex(final List<T> population) {
        return i++%population.size();
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
    public int[] selectMultipleIndividualIndices(List<T> population, int count) {
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
    public boolean repOK() {
        return i >= 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override 
    public boolean equals(final Object o) {
        if (!(o instanceof IterativeSelector))
            return false;
        return i == ((IterativeSelector)o).i;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.i;
        return hash;
    }
    
    // </editor-fold>
}
