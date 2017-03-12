package SigmaEC.util.math.dist;

import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class UniformDistribution extends Distribution {
    public final static String P_RANDOM = "random";
    public final static String P_MIN = "min";
    public final static String P_MAX = "max";
    
    private final Random random;
    private final double min;
    private final double max;
    
    public UniformDistribution(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        min = parameters.getOptionalIntParameter(Parameters.push(base, P_MIN), 0);
        if (!Double.isFinite(min) || min <= 0)
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be positive and finite.", this.getClass().getSimpleName(), Parameters.push(base, P_MIN), min));
        max = parameters.getOptionalIntParameter(Parameters.push(base, P_MAX), 0);
        if (!Double.isFinite(max) || max <= 0)
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be positive and finite.", this.getClass().getSimpleName(), Parameters.push(base, P_MAX), max));
        assert(repOK());
    }

    @Override
    public double sample() {
            return random.nextDouble()*(max - min) + min;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return random != null
                && Double.isFinite(min)
                && Double.isFinite(max)
                && max > min;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof UniformDistribution))
            return false;
        final UniformDistribution ref = (UniformDistribution)o;
        return Misc.doubleEquals(max, ref.max)
                && Misc.doubleEquals(min, ref.min)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.random);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_MIN, min,
                P_MAX, max,
                P_RANDOM, random);
    }
    // </editor-fold>
}
