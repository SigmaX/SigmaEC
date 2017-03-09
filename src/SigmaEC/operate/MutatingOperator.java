package SigmaEC.operate;

import SigmaEC.meta.Operator;
import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Individual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_MAX_ATTEMPTS = "maxAttempts";
    public final static String P_STOP_ON_UNSATISFIED_CONSTRAINT = "stopOnUnsatisfiedConstraint";
    public final static int DEFAULT_CONSTRAINT_ATTEMPTS = 10000;
    
    public static enum OverlapStyle { COMMA, PLUS };
    private final OverlapStyle overlapStyle;
    private final int childrenPerParent;
    private final Option<Constraint<T>> constraint;
    private final int maxAttempts;
    private final boolean stopOnUnsatisfiedConstraint;
    
    private final Mutator<T> mutator;
    private final Selector<T> parentSelector = new IterativeSelector<>();
    
    public MutatingOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.mutator = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATOR), Mutator.class);
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        maxAttempts = parameters.getOptionalIntParameter(Parameters.push(base, P_MAX_ATTEMPTS), DEFAULT_CONSTRAINT_ATTEMPTS);
        if (maxAttempts < 1)
            throw new IllegalStateException(String.format("[%s: parameters '%s' is set to %d, but must be positive.", this.getClass().getSimpleName(), Parameters.push(base, P_MAX_ATTEMPTS), maxAttempts));
        stopOnUnsatisfiedConstraint = parameters.getOptionalBooleanParameter(Parameters.push(base, P_STOP_ON_UNSATISFIED_CONSTRAINT), true);
        overlapStyle = OverlapStyle.valueOf(parameters.getOptionalStringParameter(Parameters.push(base, P_OVERLAP_STYLE), OverlapStyle.COMMA.toString()));
        childrenPerParent = parameters.getOptionalIntParameter(Parameters.push(base, P_CHILDREN_PER_PARENT), 1);
        if (childrenPerParent <= 0)
            throw new IllegalStateException(String.format("%s: parameter '%s' is set to %d, but must be positive.", this.getClass().getSimpleName(), Parameters.push(base, P_CHILDREN_PER_PARENT), childrenPerParent));
        assert(repOK());
    }

    @Override
    public List<T> operate(final int run, final int step, final List<T> parentPopulation) {
        assert(parentPopulation != null);
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
        assert(ind != null);
        if (!constraint.isDefined())
            return mutator.mutate(ind, step);
        int attempt = 0;
        while (attempt < maxAttempts) {
            final T child = mutator.mutate(ind, step);
            if (!constraint.get().isViolated(ind)) {
                if (attempt > 0)
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, String.format("Valid individual generated after %d mutation attempts.", attempt));
                assert(repOK());
                return child;
            }
            attempt++;
        }
        if (stopOnUnsatisfiedConstraint)
            throw new IllegalStateException(String.format("%s: Failed to find a valid gene value after attempting %d mutations: stopping.", this.getClass().getSimpleName(), maxAttempts));
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,
                String.format("Failed to find a valid gene value after attempting %d mutations: falling back to cloning.", maxAttempts));
        assert(repOK());
        return ind;
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
                && P_CONSTRAINT != null
                && !P_CONSTRAINT.isEmpty()
                && P_STOP_ON_UNSATISFIED_CONSTRAINT != null
                && !P_STOP_ON_UNSATISFIED_CONSTRAINT.isEmpty()
                && P_MAX_ATTEMPTS != null
                && !P_MAX_ATTEMPTS.isEmpty()
                && childrenPerParent > 0
                && mutator != null
                && parentSelector != null
                && constraint != null
                & maxAttempts > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%d, %s=%s, %s=%s, %s=%d, %s=%B", this.getClass().getSimpleName(),
                P_OVERLAP_STYLE, overlapStyle,
                P_CHILDREN_PER_PARENT, childrenPerParent,
                P_MUTATOR, mutator,
                P_CONSTRAINT, constraint,
                P_MAX_ATTEMPTS, maxAttempts,
                P_STOP_ON_UNSATISFIED_CONSTRAINT, stopOnUnsatisfiedConstraint);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MutatingOperator))
            return false;
        
        final MutatingOperator ref = (MutatingOperator) o;
        return childrenPerParent == ref.childrenPerParent
                && stopOnUnsatisfiedConstraint == ref.stopOnUnsatisfiedConstraint
                && maxAttempts == ref.maxAttempts
                && overlapStyle.equals(ref.overlapStyle)
                && mutator.equals(ref.mutator)
                && parentSelector.equals(ref.parentSelector)
                && constraint.equals(ref.constraint);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.overlapStyle);
        hash = 79 * hash + this.childrenPerParent;
        hash = 79 * hash + Objects.hashCode(this.constraint);
        hash = 79 * hash + this.maxAttempts;
        hash = 79 * hash + (this.stopOnUnsatisfiedConstraint ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.mutator);
        hash = 79 * hash + Objects.hashCode(this.parentSelector);
        return hash;
    }
    //</editor-fold>
}
