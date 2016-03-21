package SigmaEC.represent.cgp;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class CartesianIndividual extends Individual implements BooleanFunction {
    private static long id = 0;
    
    private final CGPParameters cgpParameters;
    private final Node[][] nodes;
    private final int[] outputSources;
    private final double fitness;

    @Override
    public int arity() {
        return cgpParameters.numInputs();
    }

    @Override
    public int numOutputs() {
        return cgpParameters.numOutputs();
    }
    
    public CGPParameters cgpParmaeters() {
        return cgpParameters;
    }
    
    @Override
    public boolean[] execute(final boolean[] input) {
        assert(input != null);
        assert(input.length == cgpParameters.numInputs());
        final boolean[] intermediateOutputs = Arrays.copyOf(input, cgpParameters.numInputs() + cgpParameters.numLayers()*cgpParameters.numNodesPerLayer());
        for (int layer = 0; layer < cgpParameters.numLayers(); layer++) {
            for (int n = 0; n < cgpParameters.numNodesPerLayer(); n++) {
                final Node node = nodes[layer][n];
                final boolean[] nodeInput = new boolean[node.function.arity()];
                for (int i = 0; i < nodeInput.length; i++) {
                    nodeInput[i] = intermediateOutputs[node.inputSources[i]];
                }
                final boolean[] result = node.function.execute(nodeInput);
                assert(result.length == 1);
                intermediateOutputs[cgpParameters.numInputs() + layer*cgpParameters.numNodesPerLayer()] = result[0];
            }
        }
        final boolean[] output = new boolean[cgpParameters.numOutputs()];
        for (int i = 0; i < output.length; i++) {
            output[i] = intermediateOutputs[outputSources[i]];
        }
        assert(repOK());
        return output;
    }
    
    public static class Builder {
        private final CGPParameters cgpParameters;
        private Node[][] nodes;
        private int[] outputSources;
        
        public Builder(final CGPParameters cgpParameters, final int[] outputSources) {
            assert(cgpParameters !=  null);
            assert(outputSources != null);
            assert(outputSources.length == cgpParameters.numOutputs());
            this.cgpParameters = cgpParameters;
            nodes = new Node[cgpParameters.numLayers()][];
            for (int i = 0; i < cgpParameters.numLayers(); i++)
                nodes[i] = new Node[cgpParameters.numNodesPerLayer()];
            this.outputSources = outputSources;
        }
        
        public CartesianIndividual build() {
            return new CartesianIndividual(this);
        }
        
        public Builder setFunction(final int layer, final int node, final BooleanFunction function, final int[] inputSources) {
            assert(layer >= 0);
            assert(layer < cgpParameters.numLayers());
            assert(node >= 0);
            assert(node < cgpParameters.numNodesPerLayer());
            nodes[layer][node] = new Node(function, inputSources);
            assert(nodeFitsConstraints(nodes[layer][node], layer));
            return this;
        }

        private boolean nodeFitsConstraints(final Node node, final int layer) {
            assert(node != null);
            assert(layer >= 0);
            assert(layer < cgpParameters.numLayers());
            final int[] sources = node.inputSources;
            if (sources.length != cgpParameters.maxArity())
                return false;
            for (int source = 0; source < cgpParameters.maxArity(); source++) {
                if (sources[source] >= cgpParameters.numInputs() + layer*cgpParameters.numNodesPerLayer())
                    return true;
                if (layer >= cgpParameters.levelsBack()
                    && (sources[source] < cgpParameters.numInputs() + (layer-cgpParameters.levelsBack())*cgpParameters.numNodesPerLayer()))
                    return true;
                if (layer < cgpParameters.levelsBack() && sources[source] < 0)
                    return false;
            }
            return true;
        }
    }
    
    private static class Node extends ContractObject {
        final BooleanFunction function;
        final int[] inputSources;
        
        Node(final BooleanFunction function, final int[] inputSources) {
            assert(function != null);
            assert(inputSources != null);
            assert(inputSources.length >= function.arity());
            this.function = function;
            this.inputSources = Arrays.copyOf(inputSources, inputSources.length);
            assert(repOK());
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return function != null
                    && inputSources != null
                    && inputSources.length >= function.arity();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Node))
                return false;
            final Node ref = (Node)o;
            return Arrays.equals(inputSources, ref.inputSources)
                    && function.equals(ref.function);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + Objects.hashCode(this.function);
            hash = 47 * hash + Arrays.hashCode(this.inputSources);
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: function=%s, inputSources=%s]", this.getClass().getSimpleName(),
                    function, Arrays.toString(inputSources));
        }
        // </editor-fold>
    }
    
    private CartesianIndividual(final Builder builder) {
        assert(builder != null);
        cgpParameters = builder.cgpParameters;
        nodes = builder.nodes;
        outputSources = builder.outputSources;
        fitness = Double.NaN;
        assert(repOK());
    }
    
    private CartesianIndividual(final CartesianIndividual ref, final double newFitness) {
        assert(ref != null);
        cgpParameters = ref.cgpParameters;
        nodes = ref.nodes;
        outputSources = ref.outputSources;
        fitness = newFitness;
        assert(repOK());
    }
    
    // <editor-fold defaultstate="collapsed" desc="Accessors and Producers">
    @Override
    public long getID() {
        return id++;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public Individual setFitness(double fitness) {
        return new CartesianIndividual(this, fitness);
    }

    @Override
    public boolean hasParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Option<List<Individual>> getParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual setParents(List<? extends Individual> parents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual clearParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return cgpParameters != null
                && nodes != null
                && nodes.length == cgpParameters.numLayers()
                && outputSources.length == cgpParameters.numOutputs()
                && !Misc.containsNulls(nodes)
                && Misc.allRowsEqualLength(nodes)
                && sourcesFitConstraints(nodes);
    }
    
    private boolean sourcesFitConstraints(final Node[][] nodes) {
        assert(nodes != null);
        assert(!Misc.containsNulls(nodes));
        assert(Misc.allRowsEqualLength(nodes));
        
        for (int layer = 0; layer < cgpParameters.numLayers(); layer++) {
            if (nodes[layer].length != cgpParameters.numNodesPerLayer())
                return false;
            for (int node = 0; node < cgpParameters.numNodesPerLayer(); node++) {
                if (!nodeFitsConstraints(nodes[layer][node], layer))
                    return false;
            }
        }
        return true;
    }
    
    private boolean nodeFitsConstraints(final Node node, final int layer) {
        assert(node != null);
        assert(layer >= 0);
        assert(layer < cgpParameters.numLayers());
        final int[] sources = node.inputSources;
        if (sources.length != cgpParameters.maxArity())
            return false;
        for (int source = 0; source < cgpParameters.maxArity(); source++) {
            if ((sources[source] > cgpParameters.numInputs() + layer*cgpParameters.numNodesPerLayer())
                    || (sources[source] < cgpParameters.numInputs() + (layer-1-cgpParameters.levelsBack())*cgpParameters.numNodesPerLayer())
                    || (sources[source] < 0))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CartesianIndividual))
            return false;
        final CartesianIndividual ref = (CartesianIndividual)o;
        return cgpParameters.equals(ref.cgpParameters)
                && Arrays.equals(outputSources, ref.outputSources)
                && Arrays.deepEquals(nodes, ref.nodes)
                && Misc.doubleEquals(fitness, ref.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.cgpParameters);
        hash = 61 * hash + Arrays.deepHashCode(this.nodes);
        hash = 61 * hash + Arrays.hashCode(this.outputSources);
        hash = 61 * hash + (int) (Double.doubleToLongBits(this.fitness) ^ (Double.doubleToLongBits(this.fitness) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: cgpParameters=%s, nodes=%s, outputSources=%s, fitness=%f]", this.getClass().getSimpleName(),
                cgpParameters, Arrays.toString(nodes), Arrays.toString(outputSources), fitness);
    }
    // </editor-fold>
}