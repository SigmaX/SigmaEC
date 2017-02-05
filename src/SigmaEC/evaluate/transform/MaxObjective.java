package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class MaxObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T, ScalarFitness> {
    private final static String P_OBJECTIVES = "objectives";
    private final List<ObjectiveFunction<T, ScalarFitness>> objectives;

    @Override
    public int getNumDimensions() {
        return objectives.get(0).getNumDimensions();
    }
    
    public MaxObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        if (objectives == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is null.");
        if (objectives.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is empty.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives contains null value.");
        if (!Misc.allElementsHaveDimension(objectives, objectives.get(0).getNumDimensions()))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives have different number of dimensions.");
        assert(repOK());
    }
    
    public MaxObjective(final List<ObjectiveFunction<T, ScalarFitness>> objectives) {
        if (objectives == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is null.");
        if (objectives.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is empty.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives contains null value.");
        if (!Misc.allElementsHaveDimension(objectives, objectives.get(0).getNumDimensions()))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives have different number of dimensions.");
        this.objectives = objectives;
        assert(repOK());
    }
    
    @Override
    public ScalarFitness fitness(final T ind) {
        double max = Double.NEGATIVE_INFINITY;
        for (ObjectiveFunction<? super T, ScalarFitness> obj : objectives)
            max = Math.max(max, obj.fitness(ind).asScalar());
        assert(repOK());
        return new ScalarFitness(max);
    }

    @Override
    public void setStep(final int i) {
        for (final ObjectiveFunction o : objectives)
            o.setStep(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objectives != null
                && !objectives.isEmpty()
                && !Misc.containsNulls(objectives)
                && Misc.allElementsHaveDimension(objectives, objectives.get(0).getNumDimensions());
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, objectives=%s]", this.getClass().getSimpleName(), getNumDimensions(), objectives);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MaxObjective))
            return false;
        
        final MaxObjective cRef = (MaxObjective) o;
        return objectives.size() == cRef.objectives.size()
                && objectives.equals(cRef.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
