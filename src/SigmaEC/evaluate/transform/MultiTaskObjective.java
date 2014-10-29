package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * Evaluates an individual's fitness on several objectives simultaneously, and
 * computing its average fitness.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MultiTaskObjective<P> extends ObjectiveFunction<P> {
    public final static String P_OBJECTIVES = "objectives";
    
    private final List<ObjectiveFunction<P>> objectives;
    
    public MultiTaskObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        if (!allSameDimensions(objectives))
            throw new IllegalStateException(String.format("%s: all objectivs must have the same dimentionality.", this.getClass().getSimpleName()));
        assert(repOK());
    }
    
    public MultiTaskObjective(final List<ObjectiveFunction<P>> objectives) {
        assert(objectives != null);
        assert(!objectives.isEmpty());
        assert(!Misc.containsNulls(objectives));
        assert(allSameDimensions(objectives));
        this.objectives = objectives;
        assert(repOK());
    }
    
    @Override
    public double fitness(final P ind) {
        double sum = 0.0;
        for (final ObjectiveFunction<P> o : objectives)
            sum += o.fitness(ind);
        return sum/objectives.size();
    }
    
    private boolean allSameDimensions(final List<ObjectiveFunction<P>> objectives) {
        final int dimensions = objectives.get(0).getNumDimensions();
        for (int i = 1; i < objectives.size(); i++)
            if (objectives.get(i).getNumDimensions() != dimensions)
                return false;
        return true;
    }

    @Override
    public int getNumDimensions() {
        return objectives.get(0).getNumDimensions();
    }
    
    @Override
    public void setGeneration(int i) {
        for (final ObjectiveFunction o : objectives)
            o.setGeneration(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return objectives != null
                && !objectives.isEmpty()
                && !Misc.containsNulls(objectives)
                && allSameDimensions(objectives);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MultiTaskObjective))
            return false;
        final MultiTaskObjective ref = (MultiTaskObjective) o;
        return objectives.equals(ref.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: objectives=%d]", this.getClass().getSimpleName(), objectives.toString());
    }
    // </editor-fold>
}
