package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.List;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 */
public class AdditiveObjective<T extends DoubleVectorPhenotype> extends ObjectiveFunction<T>
{
    private final List<ObjectiveFunction<T>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    public AdditiveObjective(final List<ObjectiveFunction<T>> objectives, final int numDimensions) throws IllegalArgumentException {
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
    public double fitness(final T ind)
    {
        double sum = 0;
        for (ObjectiveFunction<? super T> obj : objectives)
            sum += obj.fitness(ind);
        assert(repOK());
        return sum;
    }

    @Override
    public void setGeneration(int i) {
        for (final ObjectiveFunction o : objectives)
            o.setGeneration(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objectives != null
                && !Misc.containsNulls(objectives)
                && Misc.allElementsHaveDimension(objectives, numDimensions);
    }
    
    @Override
    public String toString()
    {
        return String.format("[AdditiveObjective: Objectives=%s]", objectives);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof AdditiveObjective))
            return false;
        
        final AdditiveObjective cRef = (AdditiveObjective) o;
        return numDimensions == cRef.numDimensions
                && objectives.size() == cRef.objectives.size()
                && objectives.equals(cRef.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
