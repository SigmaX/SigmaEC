package SigmaEC.select;

import SigmaEC.evaluate.VectorFitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;

/**
 * Compares individuals by considering just one specified dimension in a
 * multidimensional fitness space.
 * 
 * @author Eric O. Scott
 */
public class VectorFitnessComparator<T extends Individual<F>, F extends VectorFitness> extends FitnessComparator<T, F> {
    final public static String P_MINIMIZE = "minimize";
    final public static boolean DEFAULT_MINIMIZE = false;
    final public static String P_DELTA = "delta";
    final public static double DEFAULT_DELTA = 0.00000001;
    final public static String P_EQUAL_IS_BETTER = "equalIsBetter";
    final public static boolean DEFAULT_EQUAL_IS_BETTER = false;
    final public static String P_DIMENSION = "dimension";
    
    final private boolean minimize;
    final private double delta;
    final private boolean equalIsBetter;
    final private int dimension;
    
    public boolean minimize() { return minimize; };
    public double getDoubleEqualityDelta() { return delta; }
    public boolean equalIsBetter() { return equalIsBetter; }
    
    public VectorFitnessComparator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        minimize = parameters.getOptionalBooleanParameter(Parameters.push(base, P_MINIMIZE), DEFAULT_MINIMIZE);
        delta = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DELTA), DEFAULT_DELTA);
        if (Double.isInfinite(delta) || Double.isNaN(delta) || delta <= 0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be positive finite.", this.getClass().getSimpleName(), P_DELTA, delta));
        equalIsBetter = parameters.getOptionalBooleanParameter(Parameters.push(base, P_EQUAL_IS_BETTER), DEFAULT_EQUAL_IS_BETTER);
        dimension = parameters.getOptionalIntParameter(Parameters.push(base, P_DIMENSION), 0);
        assert(repOK());
    }
    
    /** Switch dimension. */
    public VectorFitnessComparator(final VectorFitnessComparator ref, final int dimension) {
        assert(ref != null);
        minimize = ref.minimize;
        delta = ref.delta;
        equalIsBetter = ref.equalIsBetter;
        this.dimension = dimension;
        assert(repOK());
    }
    
    public VectorFitnessComparator(final VectorFitnessComparator ref, final boolean minimize) {
        assert(ref != null);
        this.minimize = minimize;
        delta = ref.delta;
        equalIsBetter = ref.equalIsBetter;
        dimension = ref.dimension;
        assert(repOK());
    }
    
    @Override
    public VectorFitnessComparator<T, F> invert() {
        return new VectorFitnessComparator<>(this, !minimize);
    }
    
    @Override
    public int compare(final T ind, final T ind1) {
        assert(ind != null);
        assert(ind.getFitness().numDimensions() > dimension);
        if (ind1 == null)
            return 1; // Always better than null
        assert(ind1.getFitness().numDimensions() > dimension);
        final F t = ind.getFitness();
        final F t1 = ind1.getFitness();
        if (Double.isNaN(t1.getFitness(dimension)))
            return 1; // Always better than NaN.
        if (Misc.doubleEquals(t.getFitness(dimension), t1.getFitness(dimension), delta))
            return 0;
        if (t.getFitness(dimension) < t1.getFitness(dimension))
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
                && P_DIMENSION != null
                && !P_DIMENSION.isEmpty()
                && dimension >= 0
                && !Double.isInfinite(delta)
                && !Double.isNaN(delta)
                && delta >= 0.0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof VectorFitnessComparator))
            return false;
        final VectorFitnessComparator ref = (VectorFitnessComparator)o;
        return minimize == ref.minimize
                && equalIsBetter == ref.equalIsBetter
                && dimension == ref.dimension
                && Misc.doubleEquals(delta, ref.delta, 0.0000000000001);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.minimize ? 1 : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.delta) ^ (Double.doubleToLongBits(this.delta) >>> 32));
        hash = 41 * hash + (this.equalIsBetter ? 1 : 0);
        hash = 41 * hash + this.dimension;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%f]", this.getClass().getSimpleName(),
                P_DIMENSION, dimension,
                P_MINIMIZE, minimize,
                P_EQUAL_IS_BETTER, equalIsBetter,
                P_DELTA, delta);
    }
    // </editor-fold>
}