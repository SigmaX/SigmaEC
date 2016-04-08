package SigmaEC.select;

import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * An Operator that uses a selection operator to create a new population.
 * 
 * @author Eric O. Scott
 */
public class SelectionOperator<T extends Individual> extends Operator<T> {
    public final static String P_SELECTOR = "selector";
    public final static String P_SURVIVOR_POP_SIZE = "survivorPopSize";
    
    private final Selector<T> selector;
    private final Option<Integer> survivorPopSize;
    
    public SelectionOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        selector = parameters.getInstanceFromParameter(Parameters.push(base, P_SELECTOR), Selector.class);
        survivorPopSize = parameters.getOptionalIntParameter(Parameters.push(base, P_SURVIVOR_POP_SIZE));
        assert(repOK());
    }
    
    @Override
    public List<T> operate(final int run, final int generation, final List<T> population) {
        assert(population != null);
        assert(repOK());
        return selector.selectMultipleIndividuals(population, survivorPopSize.isDefined() ? survivorPopSize.get() : population.size());
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
        if (!(o instanceof SelectionOperator))
            return false;
        final SelectionOperator ref = (SelectionOperator)o;
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
