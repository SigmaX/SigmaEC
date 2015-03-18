package SigmaEC.evaluate;

import SigmaEC.Generator;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class EvaluationGenerator<T extends Individual, P> extends Generator<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_OBJECTIVE = "objective";
    
    private final Option<Decoder<T, P>> decoder;
    private final ObjectiveFunction<P> objective;
    
    public EvaluationGenerator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        assert(repOK());
    }
    
    @Override
    public List<T> produceGeneration(final List<T> parentPopulation) {
        return new ArrayList<T>(parentPopulation.size()) {{
                for (final T ind : parentPopulation) {
                    final P phenotype = decoder.isDefined() ? decoder.get().decode(ind) : (P) ind;
                    final double fitness = objective.fitness(phenotype);
                    assert(!Double.isNaN(fitness)) : String.format("The following individual, decoder, and phenotype yielded a fitness value of NaN, which is not allowed.\nindiviual: %s\ndecoder: %s\nphenotype: %s", ind, decoder, phenotype);
                    add((T) ind.setFitness(fitness));
                }
        }};
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
        if (!(o instanceof EvaluationGenerator))
            return false;
        final EvaluationGenerator ref = (EvaluationGenerator) o;
        return decoder.equals(ref.decoder)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 71 * hash + (this.objective != null ? this.objective.hashCode() : 0);
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
