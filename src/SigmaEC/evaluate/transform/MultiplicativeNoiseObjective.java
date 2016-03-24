package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class MultiplicativeNoiseObjective<T> extends ObjectiveFunction<T> {
    public final static String P_OBJECTIVE = "objective";
    public final static String P_DISTRIBUTION = "distribution";
    public final static String P_STD = "std";
    public final static String P_NOISE_RATIO = "noiseRatio";
    public final static String P_RANDOM = "random";
    
    private final ObjectiveFunction<? super T> objective;
    private final Option<Double> std;
    private final Option<Double> noiseRatio;
    private final SRandom random;
    
    public MultiplicativeNoiseObjective(final Parameters parameters, final String base) {
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (objective == null)
            throw new IllegalArgumentException(String.format("%s: objective is null.", this.getClass().getSimpleName()));
        
        std = parameters.getOptionalDoubleParameter(Parameters.push(base, P_STD));
        if (std.isDefined()) {
        if (Double.isInfinite(std.get()) || Double.isNaN(std.get()))
            throw new IllegalStateException(String.format("%s: %s is %f, but must be finite.", this.getClass().getSimpleName(), P_STD, std.get()));
        if (std.get() < 0.0)
            throw new IllegalStateException(String.format("%s: %s is %f, but must be positive.", this.getClass().getSimpleName(), P_STD, std.get()));}
        
        noiseRatio = parameters.getOptionalDoubleParameter(Parameters.push(base, P_NOISE_RATIO));
        if (noiseRatio.isDefined()) {
        if (Double.isInfinite(noiseRatio.get()) || Double.isNaN(noiseRatio.get()))
            throw new IllegalStateException(String.format("%s: %s is %f, but must be finite.", this.getClass().getSimpleName(), P_NOISE_RATIO, noiseRatio.get()));
        if (noiseRatio.get() < 0.0 || noiseRatio.get() > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, but must be in the range [0, 1].", this.getClass().getSimpleName(), P_NOISE_RATIO, noiseRatio.get()));}
        
        if (!(std.isDefined() ^ noiseRatio.isDefined()))
            throw new IllegalStateException(String.format("%s: one of '%s' or '%s' must be defined, and not both.", this.getClass().getSimpleName(), Parameters.push(base, P_STD), Parameters.push(base, P_NOISE_RATIO)));
        
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        // FIXME Uniform noise needs to be able to both add and subtract
        return std.isDefined() ? objective.fitness(ind) + std.get()*random.nextGaussian()
                : objective.fitness(ind)*(1 + noiseRatio.get()*random.nextDouble());
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
                && std != null
                && noiseRatio != null
                && std.isDefined() ^ noiseRatio.isDefined()
                && !(std.isDefined() && (std.get() < 0 || Double.isInfinite(std.get()) || Double.isNaN(std.get())))
                && !(noiseRatio.isDefined() && (noiseRatio.get() < 0 || noiseRatio.get() > 1.0));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MultiplicativeNoiseObjective))
            return false;
        
        final MultiplicativeNoiseObjective ref = (MultiplicativeNoiseObjective) o;
        return std.equals(ref.std)
                && noiseRatio.equals(ref.noiseRatio)
                && objective.equals(ref.objective)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.objective);
        hash = 97 * hash + Objects.hashCode(this.std);
        hash = 97 * hash + Objects.hashCode(this.noiseRatio);
        hash = 97 * hash + Objects.hashCode(this.random);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_STD, std,
                P_NOISE_RATIO, noiseRatio,
                P_OBJECTIVE, objective,
                P_RANDOM, random);
    }
    //</editor-fold>
}
