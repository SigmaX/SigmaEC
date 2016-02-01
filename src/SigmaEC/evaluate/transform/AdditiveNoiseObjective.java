package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class AdditiveNoiseObjective<T> extends ObjectiveFunction<T> {
    public final static String P_OBJECTIVE = "objective";
    public final static String P_STD = "std";
    public final static String P_RANDOM = "random";
    
    private final ObjectiveFunction<? super T> objective;
    private final double std;
    private final SRandom random;
    
    public AdditiveNoiseObjective(final Parameters parameters, final String base) {
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(String.format("%s: objective is null.", this.getClass().getSimpleName()));
        std = parameters.getDoubleParameter(Parameters.push(base, P_STD));
        if (Double.isInfinite(std) || Double.isNaN(std))
            throw new IllegalStateException(String.format("%s: %s is $f, but must be finite.", this.getClass().getSimpleName(), P_STD, std));
        if (std <= 0.0)
            throw new IllegalStateException(String.format("%s: %s is $f, but must be positive.", this.getClass().getSimpleName(), P_STD, std));
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        return objective.fitness(ind) + std*random.nextGaussian();
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
        return P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_STD != null
                && !P_STD.isEmpty()
                && objective != null
                && random != null
                && !Double.isInfinite(std)
                && !Double.isNaN(std)
                && std > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_STD, std,
                P_OBJECTIVE, objective,
                P_RANDOM, random);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof AdditiveNoiseObjective))
            return false;
        
        final AdditiveNoiseObjective ref = (AdditiveNoiseObjective) o;
        return Misc.doubleEquals(std, ref.std)
                && objective.equals(ref.objective)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
        hash = 37 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    
    //</editor-fold>
}
