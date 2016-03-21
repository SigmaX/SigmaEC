package SigmaEC.represent.cgp;

import SigmaEC.represent.Decoder;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class IntVectorToCartesianIndividualDecoder extends Decoder<IntVectorIndividual, CartesianIndividual> {
    public final static String P_CGP_PARAMETERS = "cgpParameters";
    public final static String P_PRIMITIVES = "primitives";
    
    private final CGPParameters cgpParameters;
    private final List<BooleanFunction> functions;
    
    public IntVectorToCartesianIndividualDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        cgpParameters = parameters.getInstanceFromParameter(Parameters.push(base, P_CGP_PARAMETERS), CGPParameters.class);
        functions = parameters.getInstancesFromParameter(Parameters.push(base, P_PRIMITIVES), BooleanFunction.class);
        assert(repOK());
    }
    
    @Override
    public CartesianIndividual decode(final IntVectorIndividual individual) {
        assert(individual != null);
        assert(individual.size() == cgpParameters.numLayers() * cgpParameters.numNodesPerLayer() * (cgpParameters.maxArity() + 1) + cgpParameters.numOutputs());
        final CartesianIndividual.Builder builder = new CartesianIndividual.Builder(cgpParameters, getOutputSources(individual));
        for (int layer = 0; layer < cgpParameters.numLayers(); layer++) {
            for (int node = 0; node < cgpParameters.numNodesPerLayer(); node++) {
                final BooleanFunction function = functions.get(individual.getElement((layer*cgpParameters.numNodesPerLayer() + node)*(cgpParameters.maxArity() + 1)));
                final int[] inputSources = new int[cgpParameters.maxArity()];
                for (int i = 0; i < cgpParameters.maxArity(); i++)
                    inputSources[i] = individual.getElement((layer*cgpParameters.numNodesPerLayer() + node)*(cgpParameters.maxArity() + 1) + 1 + i);
                builder.setFunction(layer, node, function, inputSources);
            }
        }
        assert(repOK());
        return builder.build();
    }
    
    private int[] getOutputSources(final IntVectorIndividual individual) {
        assert(individual != null);
        final int[] outputSources = new int[cgpParameters.numOutputs()];
        for (int i = 0; i < outputSources.length; i++) {
            outputSources[i] = individual.getElement(cgpParameters.numLayers()*cgpParameters.numNodesPerLayer()*(cgpParameters.maxArity() + 1) + i);
        }
        return outputSources;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_PRIMITIVES != null
                && !P_PRIMITIVES.isEmpty()
                && P_CGP_PARAMETERS != null
                && !P_CGP_PARAMETERS.isEmpty()
                && cgpParameters != null
                && functions != null
                && !functions.isEmpty()
                && !Misc.containsNulls(functions);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IntVectorToCartesianIndividualDecoder))
            return false;
        final IntVectorToCartesianIndividualDecoder ref = (IntVectorToCartesianIndividualDecoder)o;
        return cgpParameters.equals(ref.cgpParameters)
                && functions.equals(ref.functions);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.cgpParameters);
        hash = 31 * hash + Objects.hashCode(this.functions);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_CGP_PARAMETERS, cgpParameters,
                P_PRIMITIVES, functions);
    }
    // </editor-fold>
}
