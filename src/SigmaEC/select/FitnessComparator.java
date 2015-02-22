package SigmaEC.select;

import SigmaEC.ContractObject;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
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
public class FitnessComparator<T extends Individual, P> extends ContractObject implements Comparator<T> {
    final public static String P_MINIMIZE = "minimize";
    final public static String P_DOUBLE_EQUALITY_DELTA = "doubleEqualityDelta";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_DECODER = "decoder";
    
    final private boolean minimize;
    final private double doubleEqualityDelta;
    final private ObjectiveFunction<P> objective;
    final private Decoder<T, P> decoder;
    
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
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        assert(repOK());
    }
    
    @Override
    public int compare(final T ind, final T ind1) {
        assert(ind != null);
        if (ind1 == null)
            return 1; // Always better than null
        final double t = objective.fitness(decoder.decode(ind));
        final double t1 = objective.fitness(decoder.decode(ind1));
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
                && !P_MINIMIZE.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_DOUBLE_EQUALITY_DELTA != null
                && !P_DOUBLE_EQUALITY_DELTA.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && objective != null
                && decoder != null
                && Double.isFinite(doubleEqualityDelta)
                && doubleEqualityDelta >= 0.0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FitnessComparator))
            return false;
        final FitnessComparator ref = (FitnessComparator)o;
        return minimize == ref.minimize
                && Misc.doubleEquals(doubleEqualityDelta, ref.doubleEqualityDelta, 0.0000000000001)
                && objective.equals(ref.objective)
                && decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.minimize ? 1 : 0);
        hash = 13 * hash + (int) (Double.doubleToLongBits(this.doubleEqualityDelta) ^ (Double.doubleToLongBits(this.doubleEqualityDelta) >>> 32));
        hash = 13 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 13 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: minimize=%s, doubleEqualityDelta=%f, objective=%s, decoder=%s]", this.getClass().getSimpleName(), minimize, doubleEqualityDelta, objective, decoder);
    }
    // </editor-fold>
}
