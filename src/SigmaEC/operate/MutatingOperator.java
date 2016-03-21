package SigmaEC.operate;

import SigmaEC.meta.Operator;
import SigmaEC.represent.linear.Gene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a population of LinearGenomeIndividuals and mutates individual genes
 * according to some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MutatingOperator<T extends LinearGenomeIndividual<G>, G extends Gene> extends Operator<T> {
    public final static String P_MUTATOR = "mutator";
    public final static String P_OVERLAP_STYLE = "overlapStyle";
    
    public static enum OverlapStyle { COMMA, PLUS };
    private final OverlapStyle overlapStyle;
    
    private final Mutator<T, G> mutator;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    public MutatingOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.mutator = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATOR), Mutator.class);
        overlapStyle = OverlapStyle.valueOf(parameters.getOptionalStringParameter(Parameters.push(base, P_OVERLAP_STYLE), OverlapStyle.COMMA.toString()));
        assert(repOK());
    }

    @Override
    public List<T> operate(final int run, final int generation, final List<T> parentPopulation) {
        final List<T> newPopulation = new ArrayList<T>(parentPopulation.size());
        for(int i = 0; i < parentPopulation.size(); i++) {
            final T individual = parentSelector.selectIndividual(parentPopulation);
            newPopulation.add(mutator.mutate(individual));
        }
        switch (overlapStyle) {
            case COMMA:
                return newPopulation;
            case PLUS:
                parentPopulation.addAll(newPopulation);
                return parentPopulation;
            default:
                throw new IllegalStateException("Unrecognized overlap style encountered.");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return mutator != null
                && parentSelector != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: parentSelector=%s, mutator=%s", this.getClass().getSimpleName(), parentSelector, mutator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MutatingOperator))
            return false;
        
        final MutatingOperator cRef = (MutatingOperator) o;
        return mutator.equals(cRef.mutator)
                && parentSelector.equals(cRef.parentSelector);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.mutator != null ? this.mutator.hashCode() : 0);
        hash = 59 * hash + (this.parentSelector != null ? this.parentSelector.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
