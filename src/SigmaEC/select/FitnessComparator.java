package SigmaEC.select;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Comparator;

/**
 * A Comparator that encodes whether we are minimizing or maximizing.
 * Typically you want one Comparator object per EA, to be used everywhere
 * fitnesses need to be compared.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessComparator<T extends Individual> extends ContractObject implements Comparator<T> {
    final public static String P_MINIMIZE = "minimize";
    final public static String P_DOUBLE_EQUALITY_DELTA = "doubleEqualityDelta";
    final public static String P_EQUAL_IS_BETTER = "equalIsBetter";
    
    final private boolean minimize;
    final private double doubleEqualityDelta;
    final private boolean equalIsBetter;
    
    public FitnessComparator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        final Option<Boolean> minimizeOpt = parameters.getOptionalBooleanParameter(Parameters.push(base, P_MINIMIZE));
        if (minimizeOpt.isDefined())
            this.minimize = minimizeOpt.get();
        else
            this.minimize = false;
        final Option<Double> deltaOpt = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DOUBLE_EQUALITY_DELTA));
        if (deltaOpt.isDefined())
            this.doubleEqualityDelta = deltaOpt.get();
        else
            this.doubleEqualityDelta = 0.00000001;
        final Option<Boolean> equalIsBetterOpt = parameters.getOptionalBooleanParameter(Parameters.push(base, P_EQUAL_IS_BETTER));
        equalIsBetter = equalIsBetterOpt.isDefined() ? equalIsBetterOpt.get() : false;
        assert(repOK());
    }
    
    @Override
    public int compare(final T ind, final T ind1) {
        assert(ind != null);
        if (ind1 == null)
            return 1; // Always better than null
        final double t = ind.getFitness();
        final double t1 = ind1.getFitness();
        if (Double.isNaN(t1))
            return 1; // Always better than NaN.
        if (t < t1)
            return (minimize ? 1 : -1);
        if (Misc.doubleEquals(t, t1, doubleEqualityDelta))
            return 0;
        else
            return (minimize ? -1 : 1);
    }
    
    public boolean betterThan(final T ind, final T ind1) {
        assert(ind != null);
        if (ind1 == null)
            return true; // Always better than null
        if (equalIsBetter)
            return compare(ind, ind1) >= 0;
        else
            return compare(ind, ind1) > 0;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    public final boolean repOK() {
        return P_MINIMIZE != null
                && !P_MINIMIZE.isEmpty()
                && P_DOUBLE_EQUALITY_DELTA != null
                && !P_DOUBLE_EQUALITY_DELTA.isEmpty()
                && Double.isFinite(doubleEqualityDelta)
                && doubleEqualityDelta >= 0.0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FitnessComparator))
            return false;
        final FitnessComparator ref = (FitnessComparator)o;
        return minimize == ref.minimize
                && equalIsBetter == ref.equalIsBetter
                && Misc.doubleEquals(doubleEqualityDelta, ref.doubleEqualityDelta, 0.0000000000001);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.minimize ? 1 : 0);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.doubleEqualityDelta) ^ (Double.doubleToLongBits(this.doubleEqualityDelta) >>> 32));
        hash = 79 * hash + (this.equalIsBetter ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%f]", this.getClass().getSimpleName(),
                P_MINIMIZE, minimize,
                P_EQUAL_IS_BETTER, equalIsBetter,
                P_DOUBLE_EQUALITY_DELTA, doubleEqualityDelta);
    }
    // </editor-fold>
}
