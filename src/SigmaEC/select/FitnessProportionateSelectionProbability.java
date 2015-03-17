package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import SigmaEC.util.math.Vector;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class FitnessProportionateSelectionProbability<T extends Individual, P> extends SelectionProbability<T> {
    final public static String P_MINIMIZE = "minimize";
    final public static String P_OFFSET = "offset";
    
    final private boolean minimize;
    final private double offset;

    public FitnessProportionateSelectionProbability(final Parameters params, final String base) {
        assert(params != null);
        assert(base != null);
        final Option<Boolean> minimizeOpt = params.getOptionalBooleanParameter(Parameters.push(base, P_MINIMIZE));
        minimize = minimizeOpt.isDefined() ? minimizeOpt.get() : false;
        final Option<Double> fitnessBoundOpt = params.getOptionalDoubleParameter(Parameters.push(base, P_OFFSET));
        offset = fitnessBoundOpt.isDefined() ? fitnessBoundOpt.get() : 0.0;
        if (Double.isInfinite(offset) || Double.isNaN(offset))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_OFFSET, offset));
        assert(repOK());
    }
    
    @Override
    public double[] probability(final List<T> population) {
        assert(population != null);
        assert(population.size() > 0);
        // Get fitnesses
        final double[] fitnesses = new double[population.size()];
        for (int i = 0; i < population.size(); i++)
            fitnesses[i] = population.get(i).getFitness();
        
        final double[] p = new double[population.size()];
        double sum = 0;
        // Transform fitnesses into probability densities
        for (int i = 0; i < population.size(); i++) {
            assert(!Double.isInfinite(fitnesses[i]) && !Double.isNaN(fitnesses[i]));
            p[i] = Math.abs(offset - fitnesses[i]);
            sum += p[i];
        }
        // Handle the case that all probabilities are 0
        if (Misc.doubleEquals(0.0, Vector.euclideanNorm(p))) {
            for (int i = 0; i < population.size(); i++)
                p[i] = 1.0;
            sum = population.size();
        }
        // Normalize, and invert the densities if smaller densities are better
        for (int i = 0; i < p.length; i++) {
            p[i] /= sum;
            // If smaller values are better, flip the distribution
            if (minimize)
                p[i] = (1.0 - p[i])/(p.length - 1); // Convert p[x] to (1-p[x])/Z, where Z = n-1 is a new normalizing constant
        }
        assert(Statistics.sumsToOne(p));
        assert(repOK());
        return p;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_MINIMIZE != null
                && !P_MINIMIZE.isEmpty()
                && P_OFFSET != null
                && !P_OFFSET.isEmpty()
                && !Double.isInfinite(offset)
                && !Double.isNaN(offset);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FitnessProportionateSelectionProbability))
            return false;
        final FitnessProportionateSelectionProbability ref = (FitnessProportionateSelectionProbability) o;
        return minimize == ref.minimize
                && Misc.doubleEquals(offset, ref.offset);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.minimize ? 1 : 0);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.offset) ^ (Double.doubleToLongBits(this.offset) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%f]", this.getClass().getSimpleName(),
                P_MINIMIZE, minimize,
                P_OFFSET, offset);
    }
    // </editor-fold>
}
