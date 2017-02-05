package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.meta.Fitness;
import SigmaEC.util.Parameters;
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
public class CachedObjective<P, F extends Fitness> extends ObjectiveFunction<P, F> {
    final public static String P_FLUSH = "flush";
    final public static String P_OBJECTIVE = "objective";
    
    final private boolean flush;
    final private ObjectiveFunction<P, F> objective;
    final private Map<P, F> memory = new HashMap<P, F>();
    
    public CachedObjective(final Parameters params, final String base) {
        assert(params != null);
        assert(base != null);
        flush = params.getBooleanParameter(Parameters.push(base, P_FLUSH));
        objective = params.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }
    
    @Override
    public F fitness(final P ind) {
        assert(ind != null);
        if (memory.containsKey(ind))
            return memory.get(ind);
        else {
            final F fitness = objective.fitness(ind);
            memory.put(ind, fitness);
            return fitness;
        }
    }

    @Override
    public void setStep(final int i) {
        if (flush)
            memory.clear();
        objective.setStep(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && memory != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: flush=%s, objective=%s]", this.getClass().getSimpleName(), flush, objective.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CachedObjective))
            return false;
        
        final CachedObjective cRef = (CachedObjective) o;
        return flush == cRef.flush
                && objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.flush ? 1 : 0);
        hash = 97 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
