package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;

/**
 * Composes several objectives into a single, time-varying objective.  At each
 * interval steps, the objective function changes into the next objective in the
 * list.
 * 
 * @author Eric O. Scott
 */
public class SwitchingObjective<T extends Individual> extends ObjectiveFunction<T>{
    public final static String P_OBJECTIVES = "objectives";
    public final static String P_INTERVAL = "interval";
    public final static String P_REPEAT = "repeat";
    
    private final List<ObjectiveFunction<T>> objectives;
    private final int interval;
    private final boolean repeat;
    
    private int currentStep = 0;
    
    public SwitchingObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        if (!Misc.allElementsHaveDimension(objectives, objectives.get(0).getNumDimensions()))
            throw new IllegalStateException(String.format("%s: in parameter '%s', not all objectives have the same dimension.", this.getClass().getSimpleName(), Parameters.push(base, P_OBJECTIVES)));
        interval = parameters.getIntParameter(Parameters.push(base, P_INTERVAL));
        if (interval <= 0)
            throw new IllegalStateException(String.format("%s: parameter '%s' is %d, but must be greater than zero.", this.getClass().getSimpleName(), Parameters.push(base, P_INTERVAL), interval));
        repeat = parameters.getOptionalBooleanParameter(Parameters.push(base, P_REPEAT), false);
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        assert(ind != null);
        return currentObjective().fitness(ind);
    }
    
    private ObjectiveFunction<T> currentObjective() {
        final int stepsIntoCycle = repeat ?
                currentStep % (interval*objectives.size()) :
                Math.min(currentStep, interval*objectives.size() - 1);
        final int objectiveIndex = stepsIntoCycle/interval;
        assert(objectiveIndex >= 0);
        assert(objectiveIndex < objectives.size());
        return objectives.get(objectiveIndex);
    }

    @Override
    public void setStep(final int i) {
        assert(i >= 0);
        currentStep = i;
    }

    @Override
    public int getNumDimensions() {
        return objectives.get(0).getNumDimensions();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_OBJECTIVES != null
                && !P_OBJECTIVES.isEmpty()
                && P_INTERVAL != null
                && !P_INTERVAL.isEmpty()
                && P_REPEAT != null
                && !P_REPEAT.isEmpty()
                && objectives != null
                && !objectives.isEmpty()
                && !Misc.containsNulls(objectives)
                && interval > 0
                && currentStep >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SwitchingObjective))
            return false;
        final SwitchingObjective ref = (SwitchingObjective)o;
        return interval == ref.interval
                && repeat == ref.repeat
                && objectives.equals(ref.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.objectives);
        hash = 47 * hash + this.interval;
        hash = 47 * hash + (this.repeat ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%B, %s=%s]", this.getClass().getSimpleName(),
                P_INTERVAL, interval,
                P_REPEAT, repeat,
                P_OBJECTIVES, objectives);
    }
    // </editor-fold>
}
