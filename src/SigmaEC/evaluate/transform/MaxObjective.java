package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import java.util.List;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class MaxObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T>
{
    private final List<ObjectiveFunction<T>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    public MaxObjective(final List<ObjectiveFunction<T>> objectives, final int numDimensions) throws IllegalArgumentException {
        if (numDimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is <= 0, must be positive.");
        if (objectives == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is null.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives contains null value.");
        if (!Misc.allElementsHaveDimension(objectives, numDimensions))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions does not match the dimensionality of all objectives.");
        
        this.objectives = objectives;
        this.numDimensions = numDimensions;
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        double max = Double.NEGATIVE_INFINITY;
        for (ObjectiveFunction<? super T> obj : objectives)
            max = Math.max(max, obj.fitness(ind));
        assert(repOK());
        return max;
    }

    @Override
    public void setGeneration(final int i) {
        for (final ObjectiveFunction o : objectives)
            o.setGeneration(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objectives != null
                && numDimensions > 0
                && !Misc.containsNulls(objectives)
                && Misc.allElementsHaveDimension(objectives, numDimensions);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, objectives=%s]", this.getClass().getSimpleName(), numDimensions, objectives);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MaxObjective))
            return false;
        
        final MaxObjective cRef = (MaxObjective) o;
        return numDimensions == cRef.numDimensions
                && objectives.size() == cRef.objectives.size()
                && objectives.equals(cRef.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        hash = 67 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
