package SigmaEC.experiment;

import SigmaEC.Problem;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.BitGene;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleGene;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.represent.Phenotype;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class DoubleGAProblem<P extends Phenotype> implements Problem<LinearGenomeIndividual<DoubleGene>, P> {
    private final DoubleGAParameters parameters;
    private final Decoder<LinearGenomeIndividual<DoubleGene>, P> decoder;
    private final ObjectiveFunction<P> objective;
    
    public DoubleGAProblem(final DoubleGAParameters parameters, final Decoder<LinearGenomeIndividual<DoubleGene>, P> decoder, final ObjectiveFunction<P> objective) {
        assert(parameters != null);
        assert(decoder != null);
        this.parameters = parameters;
        this.decoder = decoder;
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public Decoder<LinearGenomeIndividual<DoubleGene>, P> getDecoder() {
        return decoder;
    }

    @Override
    public ObjectiveFunction<P> getObjective() {
        return objective;
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }

    @Override
    public final boolean repOK() {
        return parameters != null
                && decoder != null
                && objective != null;
    }
    
}
