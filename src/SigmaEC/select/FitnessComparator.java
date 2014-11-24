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
public class FitnessComparator extends ContractObject implements Comparator<Double> {
    final public static String P_MINIMIZE = "minimize";
    final public static String P_DOUBLE_EQUALITY_DELTA = "doubleEqualityDelta";
    
    final private boolean minimize;
    final private double doubleEqualityDelta;
    
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
            this.doubleEqualityDelta = 0.000001;
        assert(repOK());
    }
    
    public FitnessComparator(final boolean minimize) {
        this.minimize = minimize;
        this.doubleEqualityDelta = 0.000001;
        assert(repOK());
    }
    
    @Override
    public int compare(final Double t, final Double t1) {
        if (Double.isNaN(t1))
            return 1; // Always better than NaN.
        if (t < t1)
            return (minimize ? 1 : -1);
        if (Misc.doubleEquals(t, t1, doubleEqualityDelta))
            return 0;
        else
            return (minimize ? -1 : 1);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    public final boolean repOK() {
        return P_MINIMIZE != null
                && !P_MINIMIZE.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FitnessComparator))
            return false;
        final FitnessComparator ref = (FitnessComparator)o;
        return minimize == ref.minimize;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.minimize ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: minimize=%s]", this.getClass().getSimpleName(), minimize);
    }
    // </editor-fold>
}
