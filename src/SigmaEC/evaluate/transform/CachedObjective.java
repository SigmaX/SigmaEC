package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import java.util.HashMap;
import java.util.Map;

/**
 * Decorates an ObjectiveFunction with memoization, caching the results of
 * previous evaluations.  This is effective if evaluating the objective is
 * significantly more expensive than evaluating the Individual's hashcode()
 * method.
 * 
 * @author Eric 'Siggy' Scott
 */
public class CachedObjective<P> extends ObjectiveFunction<P>
{
    final private ObjectiveFunction<P> objective;
    final private Map<P, Double> memory = new HashMap<P, Double>();
    
    public CachedObjective(final ObjectiveFunction<P> objective) {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }
    
    @Override
    public double fitness(final P ind) {
        assert(ind != null);
        if (memory.containsKey(ind))
            return memory.get(ind);
        else {
            double fitness = objective.fitness(ind);
            memory.put(ind, fitness);
            return fitness;
        }
    }

    @Override
    public void setGeneration(final int i) {
        objective.setGeneration(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && memory != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: objective=%s]", this.getClass().getSimpleName(), objective.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CachedObjective))
            return false;
        
        final CachedObjective cRef = (CachedObjective) o;
        return objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>

}
