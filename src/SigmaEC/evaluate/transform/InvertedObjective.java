package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Parameters;


/**
 * A decorator that turns a maximization problem into a minimization problem and
 * vice versa.
 * 
 * @author Eric 'Siggy' Scott
 */
public class InvertedObjective<T> extends ObjectiveFunction<T, ScalarFitness>
{
    public final static String P_OBJECTIVE = "objective";
    final private ObjectiveFunction<? super T, ScalarFitness> objective;
    
    public InvertedObjective(final Parameters parameters, final String base) {
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(String.format("%s: objective is null.", this.getClass().getSimpleName()));
        assert(repOK());
    }

    public InvertedObjective(final ObjectiveFunction<T, ScalarFitness> objective) {
        if (objective == null)
            throw new IllegalArgumentException(String.format("%s: objective is null.", this.getClass().getSimpleName()));
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public ScalarFitness fitness(final T ind) {
        return new ScalarFitness(-objective.fitness(ind).asScalar());
    }
    
    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }

    @Override
    public void setStep(int i) {
        objective.setStep(i);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && objective.repOK();
    }
    
    @Override
    public String toString() {
        return String.format("[%s: Objective=%s]", this.getClass().getSimpleName(), objective.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof InvertedObjective))
            return false;
        
        final InvertedObjective cRef = (InvertedObjective) o;
        return objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>

    
}
