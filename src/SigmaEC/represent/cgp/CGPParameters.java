package SigmaEC.represent.cgp;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class CGPParameters extends ContractObject {
    public final static String P_NUM_INPUTS = "numInputs";
    public final static String P_NUM_OUTPUTS = "numOutputs";
    public final static String P_NUM_LAYERS = "numLayers";
    public final static String P_NUM_NODES_PER_LAYER = "numNodesPerLayer";
    public final static String P_MAX_ARITY = "maxArity";
    public final static String P_LEVELS_BACK = "levelsBack";
    public final static String P_NUM_PRIMITIVES = "numPrimitives";
    
    
    private final int numInputs;
    private final int numOutputs;
    private final int numLayers;
    private final int nodesPerLayer;
    private final int maxArity;
    private final int levelsBack;
    private final int numPrimitives;
    
    // <editor-fold defaultstate="collapsed" desc="Acessors">
    public int numInputs() {
        return numInputs;
    }

    public int numOutputs() {
        return numOutputs;
    }

    public int numLayers() {
        return numLayers;
    }

    public int numNodesPerLayer() {
        return nodesPerLayer;
    }

    public int maxArity() {
        return maxArity;
    }

    public int levelsBack() {
        return levelsBack;
    }

    public int numPrimitives() {    
        return numPrimitives;
    }
    // </editor-fold>
    
    public CGPParameters(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numInputs = parameters.getIntParameter(Parameters.push(base, P_NUM_INPUTS));
        numOutputs = parameters.getIntParameter(Parameters.push(base, P_NUM_OUTPUTS));
        numLayers = parameters.getIntParameter(Parameters.push(base, P_NUM_LAYERS));
        nodesPerLayer = parameters.getIntParameter(Parameters.push(base, P_NUM_NODES_PER_LAYER));
        maxArity = parameters.getIntParameter(Parameters.push(base, P_MAX_ARITY));
        levelsBack = parameters.getIntParameter(Parameters.push(base, P_LEVELS_BACK));
        numPrimitives = parameters.getIntParameter(Parameters.push(base, P_NUM_PRIMITIVES));
        assert(repOK());
    }
    
    public int getNumDimensions() {
        return numLayers*nodesPerLayer*(maxArity() + 1) + numOutputs();
    }
    
    public int[] getMinBounds() {
        final int[] mins = new int[getNumDimensions()];
        for (int layer = 0; layer < numLayers; layer++) {
            final int minSourceValueForLayer = (layer == 0 || layer < levelsBack) ? 0
                                                : numInputs + (layer - levelsBack)*nodesPerLayer;
            for (int node = 0; node < nodesPerLayer; node++) {
                final int functionIndex = (layer*nodesPerLayer + node)*(maxArity + 1);
                mins[functionIndex] = 0;
                for (int i = 1; i <= maxArity; i++) {
                    mins[functionIndex + i] = minSourceValueForLayer;
                }
            }
        }
        // Outputs
        for (int i = mins.length - numOutputs; i < mins.length; i++)
            mins[i] = 0;
        return mins;
    }
    
    public int[] getMaxBounds() {
        final int[] maxes = new int[getNumDimensions()];
        for (int layer = 0; layer < numLayers; layer++) {
            final int maxSourceValueForLayer = numInputs + layer*nodesPerLayer;
            for (int node = 0; node < nodesPerLayer; node++) {
                final int functionIndex = (layer*numNodesPerLayer() + node)*(maxArity + 1);
                maxes[functionIndex] = numPrimitives;
                for (int i = 1; i <= maxArity; i++) {
                    maxes[functionIndex + i] = maxSourceValueForLayer;
                }
            }
        }
        // Outputs
        for (int i = maxes.length - numOutputs; i < maxes.length; i++)
            maxes[i] = numInputs + numLayers*nodesPerLayer;
        return maxes;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_PRIMITIVES != null
                && !P_NUM_PRIMITIVES.isEmpty()
                && P_LEVELS_BACK != null
                && !P_LEVELS_BACK.isEmpty()
                && P_MAX_ARITY != null
                && !P_MAX_ARITY.isEmpty()
                && P_NUM_NODES_PER_LAYER != null
                && !P_NUM_NODES_PER_LAYER.isEmpty()
                && P_NUM_INPUTS != null
                && !P_NUM_INPUTS.isEmpty()
                && P_NUM_LAYERS != null
                && !P_NUM_LAYERS.isEmpty()
                && P_NUM_OUTPUTS != null
                && !P_NUM_OUTPUTS.isEmpty()
                && numInputs > 0
                && numOutputs > 0
                && numLayers > 0
                && nodesPerLayer > 0
                && maxArity > 0
                && levelsBack > 0
                && numPrimitives > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CGPParameters))
            return false;
        final CGPParameters ref = (CGPParameters)o;
        return numInputs == ref.numInputs
                && numOutputs == ref.numOutputs
                && numLayers == ref.numLayers
                && nodesPerLayer == ref.nodesPerLayer
                && maxArity == ref.maxArity
                && levelsBack == ref.levelsBack
                && numPrimitives == ref.numPrimitives;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.numInputs;
        hash = 53 * hash + this.numOutputs;
        hash = 53 * hash + this.numLayers;
        hash = 53 * hash + this.nodesPerLayer;
        hash = 53 * hash + this.maxArity;
        hash = 53 * hash + this.levelsBack;
        hash = 53 * hash + this.numPrimitives;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%d, %s=%d, %s=%d, %s=%d, %s=%d]", this.getClass().getSimpleName(),
                P_NUM_INPUTS, numInputs,
                P_NUM_OUTPUTS, numOutputs,
                P_NUM_LAYERS, numLayers,
                P_NUM_NODES_PER_LAYER, nodesPerLayer,
                P_MAX_ARITY, maxArity,
                P_LEVELS_BACK, levelsBack,
                P_NUM_PRIMITIVES, numPrimitives);
    }
    // </editor-fold>
}
