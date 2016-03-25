package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 * An Objective with Gaussian noise that follows a constant signal-to-noise ratio.
 * 
 * @author Eric O. Scott
 */
public class MultiplicativeNoiseObjective<T> extends ObjectiveFunction<T> {
    public final static String P_OBJECTIVE = "objective";
    public final static String P_STD_FRACTION = "stdFraction";
    public final static String P_RANDOM = "random";
    public final static String P_GLOBAL_BEST_FITNESS = "globalBestFitness";
    
    private final ObjectiveFunction<? super T> objective;
    private final double stdFraction;
    private final SRandom random;
    private final double globalBestFitness;
    
    public MultiplicativeNoiseObjective(final Parameters parameters, final String base) {
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(String.format("%s: objective is null.", this.getClass().getSimpleName()));
        
        stdFraction = parameters.getDoubleParameter(Parameters.push(base, P_STD_FRACTION));
        if (Double.isInfinite(stdFraction) || Double.isNaN(stdFraction))
            throw new IllegalStateException(String.format("%s: %s is %f, but must be finite.", this.getClass().getSimpleName(), P_STD_FRACTION, stdFraction));
        if (stdFraction < 0.0)
            throw new IllegalStateException(String.format("%s: %s is %f, but must be positive.", this.getClass().getSimpleName(), P_STD_FRACTION, stdFraction));
        
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        globalBestFitness = parameters.getIntParameter(Parameters.push(base, P_GLOBAL_BEST_FITNESS));
        if (!Double.isFinite(globalBestFitness) || Double.isNaN(globalBestFitness))
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be finite.", this.getClass().getSimpleName(), Parameters.push(base, P_GLOBAL_BEST_FITNESS), globalBestFitness));
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        final double f = objective.fitness(ind);
        return f + stdFraction*Math.abs(f - globalBestFitness)*random.nextGaussian();
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
                && P_STD_FRACTION != null
                && !P_STD_FRACTION.isEmpty()
                && P_GLOBAL_BEST_FITNESS != null
                && !P_GLOBAL_BEST_FITNESS.isEmpty()
                && objective != null
                && random != null
                && Double.isFinite(stdFraction)
                && !Double.isNaN(stdFraction)
                && stdFraction >= 0
                && Double.isFinite(globalBestFitness)
                && !Double.isNaN(globalBestFitness);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MultiplicativeNoiseObjective))
            return false;
        
        final MultiplicativeNoiseObjective ref = (MultiplicativeNoiseObjective) o;
        return Misc.doubleEquals(stdFraction, ref.stdFraction)
                && Misc.doubleEquals(globalBestFitness, ref.globalBestFitness)
                && objective.equals(ref.objective)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.objective);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.stdFraction) ^ (Double.doubleToLongBits(this.stdFraction) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.random);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.globalBestFitness) ^ (Double.doubleToLongBits(this.globalBestFitness) >>> 32));
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_STD_FRACTION, stdFraction,
                P_GLOBAL_BEST_FITNESS, globalBestFitness,
                P_OBJECTIVE, objective,
                P_RANDOM, random);
    }
    //</editor-fold>
}
