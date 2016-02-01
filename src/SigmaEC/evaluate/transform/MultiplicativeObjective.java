package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;

/**
 * A decorator that multiplies two or more objective functions.
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class MultiplicativeObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T> {
    public final static String P_OBJECTIVES = "objectives";
    private final List<ObjectiveFunction<T>> objectives;
    private final int numDimensions;

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    public MultiplicativeObjective(final Parameters parameters, final String base) throws IllegalStateException {
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
    
    @Override
    public double fitness(final T ind) {
        double product = 1.0;
        for (ObjectiveFunction<? super T> obj : objectives)
            product *= obj.fitness(ind);
        assert(repOK());
        return product;
    }


    @Override
    public void setStep(int i) {
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
        if (!(o instanceof MultiplicativeObjective))
            return false;
        
        final MultiplicativeObjective cRef = (MultiplicativeObjective) o;
        return numDimensions == cRef.numDimensions
                && objectives.size() == cRef.objectives.size()
                && objectives.equals(cRef.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.objectives);
        hash = 11 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
