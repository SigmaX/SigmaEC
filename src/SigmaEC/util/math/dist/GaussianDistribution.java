package SigmaEC.util.math.dist;

import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class GaussianDistribution extends Distribution {
    public final static String P_RANDOM = "random";
    public final static String P_STD = "std";
    
    private final Random random;
    private final double std;
    
    public GaussianDistribution(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        std = parameters.getDoubleParameter(Parameters.push(base, P_STD));
        if (!Double.isFinite(std) || std <= 0)
            throw new IllegalStateException(String.format("%s: '%s' is %f, but must be positive and finite.", this.getClass().getSimpleName(), Parameters.push(base, P_STD), std));
        assert(repOK());
    }

    @Override
    public double sample() {
            return gaussianSample(random)*std;
    }
    
    /** Irwin-Hall approximation of a standard Gaussian distribution. */
    public static double gaussianSample(final Random random) {
        double sum = 0;
        for (int i = 0; i < 12; i++)
            sum += random.nextDouble();
        return sum - 6;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_STD != null
                && !P_STD.isEmpty()
                && random != null
                && std > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GaussianDistribution))
            return false;
        final GaussianDistribution ref = (GaussianDistribution)o;
        return Misc.doubleEquals(std, ref.std)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.random);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_STD, std,
                P_RANDOM, random);
    }
    // </editor-fold>
}
