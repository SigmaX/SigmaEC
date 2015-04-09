package SigmaEC.select;

import SigmaEC.Generator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * An Generator that uses a selection operator to create a new population.
 * 
 * @author Eric O. Scott
 */
public class SelectionGenerator<T extends Individual> extends Generator<T> {
    public final static String P_SELECTOR = "selector";
    
    private final Selector<T> selector;
    
    public SelectionGenerator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        selector = parameters.getInstanceFromParameter(Parameters.push(base, P_SELECTOR), Selector.class);
        assert(repOK());
    }
    
    @Override
    public List<T> produceGeneration(final List<T> population) {
        assert(population != null);
        assert(repOK());
        return selector.selectMultipleIndividuals(population, population.size());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_SELECTOR != null
                && !P_SELECTOR.isEmpty()
                && selector != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SelectionGenerator))
            return false;
        final SelectionGenerator ref = (SelectionGenerator)o;
        return selector.equals(ref.selector);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_SELECTOR, selector);
    }
    // </editor-fold>
}