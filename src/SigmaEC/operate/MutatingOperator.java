package SigmaEC.operate;

import SigmaEC.ContractObject;
import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Takes a population of LinearGenomeIndividuals and mutates individual genes
 * according to some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MutatingOperator<T extends Individual> extends Operator<T> {
    public final static String P_MUTATOR = "mutator";
    public final static String P_OVERLAP_STYLE = "overlapStyle";
    public final static String P_CHILDREN_PER_PARENT = "childrenPerParent";
    
    public static enum OverlapStyle { COMMA, PLUS };
    private final OverlapStyle overlapStyle;
    private final int childrenPerParent;
    
    private final Mutator<T> mutator;
    private final Selector<T> parentSelector = new IterativeSelector<>();
    
    public MutatingOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.mutator = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATOR), Mutator.class);
        overlapStyle = OverlapStyle.valueOf(parameters.getOptionalStringParameter(Parameters.push(base, P_OVERLAP_STYLE), OverlapStyle.COMMA.toString()));
        childrenPerParent = parameters.getOptionalIntParameter(Parameters.push(base, P_CHILDREN_PER_PARENT), 1);
        if (childrenPerParent <= 0)
            throw new IllegalStateException(String.format("%s: parameter '%s' is set to %d, but must be positive.", this.getClass().getSimpleName(), Parameters.push(base, P_CHILDREN_PER_PARENT), childrenPerParent));
        assert(repOK());
    }

    @Override
    public List<T> operate(final int run, final int step, final List<T> parentPopulation) {
        final List<T> newPopulation = new ArrayList<>(parentPopulation.size());
        for(int i = 0; i < parentPopulation.size(); i++) {
            final T parent = parentSelector.selectIndividual(parentPopulation);
            for (int j = 0; j < childrenPerParent; j++)
                newPopulation.add(operateInd(parent, step));
        }
        switch (overlapStyle) {
            case COMMA:
                return newPopulation;
            case PLUS:
                final List<T> combinedPopulation = new ArrayList<>(parentPopulation);
                combinedPopulation.addAll(newPopulation);
                return combinedPopulation;
            default:
                throw new IllegalStateException("Unrecognized overlap style encountered.");
        }
    }
    
    public T operateInd(final T ind, final int step) {
        return mutator.mutate(ind, step);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_CHILDREN_PER_PARENT != null
                && !P_CHILDREN_PER_PARENT.isEmpty()
                && P_MUTATOR != null
                && !P_MUTATOR.isEmpty()
                && P_OVERLAP_STYLE != null
                && !P_OVERLAP_STYLE.isEmpty()
                && childrenPerParent > 0
                && mutator != null
                && parentSelector != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%d, %s=%s", this.getClass().getSimpleName(),
                P_OVERLAP_STYLE, overlapStyle,
                P_CHILDREN_PER_PARENT, childrenPerParent,
                P_MUTATOR, mutator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MutatingOperator))
            return false;
        
        final MutatingOperator ref = (MutatingOperator) o;
        return childrenPerParent == ref.childrenPerParent
                && overlapStyle.equals(ref.overlapStyle)
                && mutator.equals(ref.mutator)
                && parentSelector.equals(ref.parentSelector);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.overlapStyle);
        hash = 53 * hash + this.childrenPerParent;
        hash = 53 * hash + Objects.hashCode(this.mutator);
        hash = 53 * hash + Objects.hashCode(this.parentSelector);
        return hash;
    }
    //</editor-fold>
}
