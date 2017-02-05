package SigmaEC.select;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 * A Comparator that operates over scalar fitness values.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ScalarFitnessComparator<T extends Individual<F>, F extends Fitness> extends FitnessComparator<T, F> {
    final public static String P_MINIMIZE = "minimize";
    final public static boolean DEFAULT_MINIMIZE = false;
    final public static String P_DELTA = "delta";
    final public static double DEFAULT_DELTA = 0.00000001;
    final public static String P_EQUAL_IS_BETTER = "equalIsBetter";
    final public static boolean DEFAULT_EQUAL_IS_BETTER = false;
    
    final private boolean minimize;
    final private double delta;
    final private boolean equalIsBetter;
    
    public boolean minimize() { return minimize; };
    public double getDoubleEqualityDelta() { return delta; }
    public boolean equalIsBetter() { return equalIsBetter; }
    
    public ScalarFitnessComparator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        minimize = parameters.getOptionalBooleanParameter(Parameters.push(base, P_MINIMIZE), DEFAULT_MINIMIZE);
        delta = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DELTA), DEFAULT_DELTA);
        if (Double.isInfinite(delta) || Double.isNaN(delta) || delta <= 0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be positive finite.", this.getClass().getSimpleName(), P_DELTA, delta));
        equalIsBetter = parameters.getOptionalBooleanParameter(Parameters.push(base, P_EQUAL_IS_BETTER), DEFAULT_EQUAL_IS_BETTER);
        assert(repOK());
    }
    
    public ScalarFitnessComparator(final ScalarFitnessComparator ref, final boolean minimize) {
        assert(ref != null);
        this.minimize = minimize;
        delta = ref.delta;
        equalIsBetter = ref.equalIsBetter;
        assert(repOK());
    }
    
    public ScalarFitnessComparator<T, F> invert() {
        return new ScalarFitnessComparator<>(this, !minimize);
    }
    
    @Override
    public int compare(final T ind, final T ind1) {
        assert(ind != null);
        if (ind1 == null)
            return 1; // Always better than null
        final F t = ind.getFitness();
        final F t1 = ind1.getFitness();
        if (Double.isNaN(t1.asScalar()))
            return 1; // Always better than NaN.
        if (Misc.doubleEquals(t.asScalar(), t1.asScalar(), delta))
            return 0;
        if (t.asScalar() < t1.asScalar())
            return (minimize ? 1 : -1);
        else
            return (minimize ? -1 : 1);
    }
    
    @Override
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
                && P_DELTA != null
                && !P_DELTA.isEmpty()
                && !Double.isInfinite(delta)
                && !Double.isNaN(delta)
                && delta >= 0.0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ScalarFitnessComparator))
            return false;
        final ScalarFitnessComparator ref = (ScalarFitnessComparator)o;
        return minimize == ref.minimize
                && equalIsBetter == ref.equalIsBetter
                && Misc.doubleEquals(delta, ref.delta, 0.0000000000001);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.minimize ? 1 : 0);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.delta) ^ (Double.doubleToLongBits(this.delta) >>> 32));
        hash = 79 * hash + (this.equalIsBetter ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%f]", this.getClass().getSimpleName(),
                P_MINIMIZE, minimize,
                P_EQUAL_IS_BETTER, equalIsBetter,
                P_DELTA, delta);
    }
    // </editor-fold>
}
