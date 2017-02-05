package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;

/**
 * A decorator that sums two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 */
public class AdditiveObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T, ScalarFitness> {
    public final static String P_OBJECTIVES = "objectives";
    private final List<ObjectiveFunction<T, ScalarFitness>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    public AdditiveObjective(final Parameters parameters, final String base) throws IllegalStateException {
        assert(parameters != null);
        assert(base != null);
        objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        if (objectives == null || objectives.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is null or empty.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives contains null value.");
        numDimensions = objectives.get(0).getNumDimensions();
        if (!Misc.allElementsHaveDimension(objectives, numDimensions))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives have different number of dimensions—must all be the same dimensionality.");
        assert(repOK());
    }
    
    public AdditiveObjective(final List<ObjectiveFunction<T, ScalarFitness>> objectives) {
        this.objectives = objectives;
        if (objectives == null || objectives.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives is null or empty.");
        if (Misc.containsNulls(objectives))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives contains null value.");
        this.numDimensions = objectives.get(0).getNumDimensions();
        if (!Misc.allElementsHaveDimension(objectives, numDimensions))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objectives have different number of dimensions—must all be the same dimensionality.");
        assert(repOK());
    }
    
    @Override
    public ScalarFitness fitness(final T ind) {
        double sum = 0;
        for (ObjectiveFunction<? super T, ScalarFitness> obj : objectives)
            sum += obj.fitness(ind).asScalar();
        assert(repOK());
        return new ScalarFitness(sum);
    }

    @Override
    public void setStep(final int i) {
        for (final ObjectiveFunction o : objectives)
            o.setStep(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_OBJECTIVES != null
                && !P_OBJECTIVES.isEmpty()
                && objectives != null
                && !objectives.isEmpty()
                && numDimensions > 0
                && !Misc.containsNulls(objectives)
                && Misc.allElementsHaveDimension(objectives, numDimensions);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_OBJECTIVES, objectives);
    }
    
    @Override
    public boolean equals(final Object o) {
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
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.objectives);
        hash = 29 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
