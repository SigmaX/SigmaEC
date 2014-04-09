package SigmaEC.experiment;

import SigmaEC.Problem;
import SigmaEC.experiment.GAParameters;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.BitGene;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.represent.Phenotype;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class GAProblem<P extends Phenotype> implements Problem<LinearGenomeIndividual<BitGene>, P> {
    private final GAParameters parameters;
    private final Decoder<LinearGenomeIndividual<BitGene>, P> decoder;
    private final ObjectiveFunction<P> objective;
    
    public GAProblem(final GAParameters parameters, final Decoder<LinearGenomeIndividual<BitGene>, P> decoder, final ObjectiveFunction<P> objective) {
        assert(parameters != null);
        assert(decoder != null);
        this.parameters = parameters;
        this.decoder = decoder;
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public Decoder<LinearGenomeIndividual<BitGene>, P> getDecoder() {
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
