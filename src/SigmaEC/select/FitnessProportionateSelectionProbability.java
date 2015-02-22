package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class FitnessProportionateSelectionProbability<T extends Individual, P> extends SelectionProbability<T> {
    final public static String P_DECODER = "decoder";
    final public static String P_OBJECTIVE = "objective";
    
    final private Decoder<T, P> decoder;
    final private ObjectiveFunction<P> objective;

    public FitnessProportionateSelectionProbability(final Parameters params, final String base) {
        assert(params != null);
        assert(base != null);
        decoder = params.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        objective = params.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        assert(repOK());
    }
    
    @Override
    public double[] probability(final List<T> population) {
        assert(population != null);
        assert(population.size() > 0);
        final double[] fitnesses = new double[population.size()];
        double sum = 0;
        for (final double d : fitnesses)
            sum += d;
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] /= sum;
        assert(repOK());
        return fitnesses;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && decoder != null
                && objective != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FitnessProportionateSelectionProbability))
            return false;
        final FitnessProportionateSelectionProbability ref = (FitnessProportionateSelectionProbability) o;
        return decoder.equals(ref.decoder)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 17 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: decoder=%s, objective=%d]", this.getClass().getSimpleName(), decoder, objective);
    }
    // </editor-fold>
}
