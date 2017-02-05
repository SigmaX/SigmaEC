package SigmaEC.evaluate;

import SigmaEC.meta.Operator;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.meta.Fitness;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class EvaluationOperator<T extends Individual<F>, P, F extends Fitness> extends Operator<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_OBJECTIVE = "objective";
    public final static String P_REEVALUATE = "reevaluate";
    
    private final Option<Decoder<T, P>> decoder;
    private final ObjectiveFunction<P, F> objective;
    private final boolean reevaluate;
    
    public EvaluationOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        reevaluate = parameters.getOptionalBooleanParameter(Parameters.push(base, P_REEVALUATE), false);
        assert(repOK());
    }
    
    public EvaluationOperator(final Decoder<T, P> decoder, final ObjectiveFunction<P, F> objective, final boolean reevaluate) {
        assert(decoder != null);
        assert(objective != null);
        this.decoder = new Option<>(decoder);
        this.objective = objective;
        this.reevaluate = reevaluate;
        assert(repOK());
    }
    
    /** Evaluate the fitness of all the individuals in a population.
     * 
     * @param run Ignored.
     * @param generation Ignored.
     * @param parentPopulation The population to be evaluated.
     * @return A population of new individuals with their fitness value (re)set
     *   and their parents attribute cleared.
     */
    @Override
    public List<T> operate(final int run, final int generation, final List<T> parentPopulation) {
        return new ArrayList<T>(parentPopulation.size()) {{
                for (final T ind : parentPopulation) {
                    if (!reevaluate && ind.isEvaluated())
                        // Keep existing fitness value
                        add((T) ind.clearParents());
                    else {
                        // Evaluate or re-evaluate fitness
                        add(evaluate(ind));
                    }
                }
        }};
    }
    
    public T evaluate(final T ind) {
        final P phenotype = decoder.isDefined() ? decoder.get().decode(ind) : (P) ind;
        final F fitness = objective.fitness(phenotype);
        return (T) ind.clearParents().setFitness(fitness);
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
        if (!(o instanceof EvaluationOperator))
            return false;
        final EvaluationOperator ref = (EvaluationOperator) o;
        return decoder.equals(ref.decoder)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.decoder);
        hash = 67 * hash + Objects.hashCode(this.objective);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_DECODER, decoder,
                P_OBJECTIVE, objective);
    }
    // </editor-fold>
}
