package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 * A decorator that multiplies an objective function's fitness by a scalar factor.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ScalarMultipliedObjective<T extends Individual> extends ObjectiveFunction<T> {
    public final static String P_OBJECTIVE = "objective";
    public final static String P_MULTIPLIER = "multiplier";
    public final static String P_OFFSET = "offset";
    
    private final ObjectiveFunction<T> objective;
    private final double multiplier;
    private final double offset;

    @Override
    public int getNumDimensions() {
        return objective.getNumDimensions();
    }
    
    public ScalarMultipliedObjective(final Parameters parameters, final String base) throws IllegalStateException {
        assert(parameters != null);
        assert(base != null);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        multiplier = parameters.getDoubleParameter(Parameters.push(base, P_MULTIPLIER));
        offset = parameters.getOptionalDoubleParameter(Parameters.push(base, P_OFFSET), 0.0);
        if (!Double.isFinite(multiplier))
            throw new IllegalArgumentException(String.format("%s: parameter '%s' must be finite.", this.getClass().getSimpleName(), Parameters.push(base, P_MULTIPLIER)));
        if (!Double.isFinite(offset))
            throw new IllegalArgumentException(String.format("%s: parameter '%s' must be finite.", this.getClass().getSimpleName(), Parameters.push(base, P_OFFSET)));
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        double product = multiplier * objective.fitness(ind) + offset;
        assert(repOK());
        return product;
    }


    @Override
    public void setStep(final int i) {
        objective.setStep(i);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_MULTIPLIER != null
                && !P_MULTIPLIER.isEmpty()
                && P_OFFSET != null
                && !P_OFFSET.isEmpty()
                && objective != null
                && Double.isFinite(multiplier)
                && Double.isFinite(offset);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_MULTIPLIER, multiplier,
                P_OFFSET, offset,
                P_OBJECTIVE, objective);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ScalarMultipliedObjective))
            return false;
        
        final ScalarMultipliedObjective ref = (ScalarMultipliedObjective) o;
        return Misc.doubleEquals(multiplier, ref.multiplier)
                && Misc.doubleEquals(offset, ref.offset)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.objective);
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.multiplier) ^ (Double.doubleToLongBits(this.multiplier) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.offset) ^ (Double.doubleToLongBits(this.offset) >>> 32));
        return hash;
    }
    //</editor-fold>
}

