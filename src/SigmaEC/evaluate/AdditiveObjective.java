package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.List;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 */
public class AdditiveObjective<T extends DoubleVectorPhenotype> implements ObjectiveFunction<T>
{
    private final List<ObjectiveFunction<? super T>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    public AdditiveObjective(List<ObjectiveFunction<? super T>> objectives, int numDimensions) throws IllegalArgumentException
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
        double sum = 0;
        for (ObjectiveFunction<? super T> obj : objectives)
            sum += obj.fitness(ind);
        assert(repOK());
        return sum;
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
