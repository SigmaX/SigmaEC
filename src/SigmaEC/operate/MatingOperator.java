package SigmaEC.operate;

import SigmaEC.meta.Operator;
import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Individual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Takes a population of individuals represented by linear genomes and creates
 * a new generation.
 * 
 * @see LinearGenomeMator
 * @author Eric 'Siggy' Scott
 */
public class MatingOperator<T extends Individual> extends Operator<T> {
    private final static String P_MATOR = "mator";
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_MAX_ATTEMPTS = "maxAttempts";
    public final static String P_STOP_ON_UNSATISFIED_CONSTRAINT = "stopOnUnsatisfiedConstraint";
    public final static int DEFAULT_CONSTRAINT_ATTEMPTS = 10000;
        
    private final Mator<T> mator;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    private final Option<Constraint<T>> constraint;
    private final int maxAttempts;
    private final boolean stopOnUnsatisfiedConstraint;
    
    public MatingOperator(final Parameters parameters, final String base) throws IllegalArgumentException {        
        assert(parameters != null);
        assert(base != null);
        this.mator = parameters.getInstanceFromParameter(Parameters.push(base, P_MATOR), Mator.class);
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        maxAttempts = parameters.getOptionalIntParameter(Parameters.push(base, P_MAX_ATTEMPTS), DEFAULT_CONSTRAINT_ATTEMPTS);
        if (maxAttempts < 1)
            throw new IllegalStateException(String.format("[%s: parameters '%s' is set to %d, but must be positive.", this.getClass().getSimpleName(), Parameters.push(base, P_MAX_ATTEMPTS), maxAttempts));
        stopOnUnsatisfiedConstraint = parameters.getOptionalBooleanParameter(Parameters.push(base, P_STOP_ON_UNSATISFIED_CONSTRAINT), true);
        
        if (mator == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": procreator is null.");
        if (mator.getNumChildren() != mator.getNumParents())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": mator.getNumChildren() must equal mator.getNumParents()");
        assert(repOK());
    }
    
    /**
     * @return A generation of offspring.
     */
    @Override
    public List<T> operate(final int run, final int generation, final List<T> population) {
        assert(population.size() >= mator.getNumParents());
        assert(population.size()%mator.getNumChildren() == 0);
        assert(Misc.containsOnlyClass(population, population.get(0).getClass()));
        
        final List<T> offspring = new ArrayList();
        for(int totalChildren = 0; totalChildren < population.size(); totalChildren += mator.getNumChildren()) {
            List<T> parents = new ArrayList<T>();
            for (int i = 0; i < mator.getNumParents(); i++)
                parents.add(parentSelector.selectIndividual(population));
            offspring.addAll(mate(parents));
        }
        assert(population.size() == offspring.size());
        assert(repOK());
        return offspring;
    }
    
    private List<T> mate(final List<T> parents) {
        assert(parents != null);
        assert(!parents.isEmpty());
        if (!constraint.isDefined())
            return mator.mate(parents);
        assert(!constraintIsViolatedForAny(parents));
        int attempt = 0;
        while (attempt < maxAttempts) {
            final List<T> children = mator.mate(parents);
            if (!constraintIsViolatedForAny(children)) {
                if (attempt > 0)
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, String.format("Valid individuals generated after %d mating attempts.", attempt));
                assert(repOK());
                return children;
            }
            attempt++;
        }
        if (stopOnUnsatisfiedConstraint)
            throw new IllegalStateException(String.format("%s: Failed to find a valid gene value after attempting %d mating events: stopping.", this.getClass().getSimpleName(), maxAttempts));
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,
                String.format("Failed to find a valid gene value after attempting %d mating events: falling back to cloning.", maxAttempts));
        assert(repOK());
        return parents;
    }
    
    private boolean constraintIsViolatedForAny(final List<T> inds) {
        assert(inds != null);
        assert(constraint.isDefined());
        for (final T t : inds)
            if (constraint.get().isViolated(t))
                return true;
        return false;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_CONSTRAINT != null
                && !P_CONSTRAINT.isEmpty()
                && P_MATOR != null
                && !P_MATOR.isEmpty()
                && P_STOP_ON_UNSATISFIED_CONSTRAINT != null
                && !P_STOP_ON_UNSATISFIED_CONSTRAINT.isEmpty()
                && P_MAX_ATTEMPTS != null
                && !P_MAX_ATTEMPTS.isEmpty()
                && parentSelector != null
                && mator != null
                && constraint != null
                && maxAttempts > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%d, %s=%B, %s=%s]", this.getClass().getSimpleName(),
                P_MATOR, mator,
                P_MAX_ATTEMPTS, maxAttempts,
                P_STOP_ON_UNSATISFIED_CONSTRAINT, stopOnUnsatisfiedConstraint,
                P_CONSTRAINT, constraint);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MatingOperator))
            return false;
        final MatingOperator ref = (MatingOperator) o;
        return stopOnUnsatisfiedConstraint == ref.stopOnUnsatisfiedConstraint
                && maxAttempts == ref.maxAttempts
                && mator.equals(ref.mator)
                && constraint.equals(ref.constraint);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.mator);
        hash = 13 * hash + Objects.hashCode(this.constraint);
        hash = 13 * hash + this.maxAttempts;
        hash = 13 * hash + (this.stopOnUnsatisfiedConstraint ? 1 : 0);
        return hash;
    }
    //</editor-fold>
}
