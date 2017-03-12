package SigmaEC.util.math.dist;

import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class PowerLawDistribution extends Distribution {
    public final static String P_POWER = "power";
    public final static String P_RANDOM = "random";
    public final static String P_MIN = "min";
    public final static String P_MAX = "max";
    
    private final double power;
    private final Random random;
    private final double min;
    private final double max;
    
    public PowerLawDistribution(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        power = parameters.getDoubleParameter(Parameters.push(base, P_POWER));
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        min = parameters.getDoubleParameter(Parameters.push(base, P_MIN));
        if (!Double.isFinite(min) || min <= 0)
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be positive and finite.", this.getClass().getSimpleName(), Parameters.push(base, P_MIN), min));
        max = parameters.getOptionalDoubleParameter(Parameters.push(base, P_MAX), 1.0);
        if (!Double.isFinite(max) || max <= 0)
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be positive and finite.", this.getClass().getSimpleName(), Parameters.push(base, P_MAX), max));
        
        assert(repOK());
    }
    
    @Override
    public double sample() {
        // The equation for sampling from a power law can be derived using the
        // Inverse Transform sampling method.
        // http://stackoverflow.com/questions/918736/random-number-generator-that-produces-a-power-law-distribution
        return Math.pow( (Math.pow(max, power + 1) - Math.pow(min, power + 1))*random.nextDouble() + Math.pow(min, power + 1), 1/(power + 1));
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_POWER != null
                && !P_POWER.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && Double.isFinite(power)
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PowerLawDistribution))
            return false;
        final PowerLawDistribution ref = (PowerLawDistribution)o;
        return Misc.doubleEquals(power, ref.power)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.power) ^ (Double.doubleToLongBits(this.power) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_POWER, power,
                P_RANDOM, random);
    }
    // </editor-fold>
}
