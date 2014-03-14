package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.List;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class MaxObjective<T extends DoubleVectorPhenotype> implements ObjectiveFunction<T>
{
    private final List<ObjectiveFunction<? super T>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    public MaxObjective(List<ObjectiveFunction<? super T>> objectives, int numDimensions) throws IllegalArgumentException
    {
        if (objectives == null)
            throw new IllegalArgumentException("AdditiveObjective: objectives is null.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException("AdditiveObjective: objectives contains null value.");
        
        this.objectives = objectives;
        this.numDimensions = numDimensions;
        assert(repOK());
    }
    
    @Override
    public double fitness(T ind)
    {
        double max = Double.NEGATIVE_INFINITY;
        for (ObjectiveFunction<? super T> obj : objectives)
            max = Math.max(max, obj.fitness(ind));
        assert(repOK());
        return max;
    }

    @Override
    public void setGeneration(int i) {
        for (final ObjectiveFunction o : objectives)
            o.setGeneration(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return objectives != null
                && !Misc.containsNulls(objectives);
    }
    
    @Override
    public String toString()
    {
        return String.format("[MaxObjective: Objectives=%s]", objectives);
    }
    
    @Override
    public boolean equals(Object o)
    {
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
