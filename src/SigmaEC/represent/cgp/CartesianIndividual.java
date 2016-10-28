package SigmaEC.represent.cgp;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class CartesianIndividual extends Individual implements BooleanFunction {
    private static long id = 0;
    
    private final CGPParameters cgpParameters;
    private final Node[][] nodes;
    private final int[] outputSources;
    private final Option<Double> fitness;
    private final Option<List<Set<Integer>>> inputExecutionPaths;

    // <editor-fold defaultstate="collapsed" desc="Accessors and Producers">
    @Override
    public int arity() {
        return cgpParameters.numInputs();
    }

    @Override
    public int numOutputs() {
        return cgpParameters.numOutputs();
    }
    
    public CGPParameters cgpParameters() {
        return cgpParameters;
    }
    
    public BooleanFunction getFunction(final int layer, final int node) {
            assert(layer >= 0);
        assert(layer < cgpParameters.numLayers());
        assert(node >= 0);
        assert(node < cgpParameters.numNodesPerLayer());
        return nodes[layer][node].function;
    }
    
    public int[] getInputs(final int layer, final int node) {
        assert(layer >= 0);
        assert(layer < cgpParameters.numLayers());
        assert(node >= 0);
        assert(node < cgpParameters.numNodesPerLayer());
        return Arrays.copyOf(nodes[layer][node].inputSources, nodes[layer][node].inputSources.length);
    }
    
    public int[] getOutputSources() {
        return Arrays.copyOf(outputSources, outputSources.length);
    }
    
    public Set<Integer> getExecutionPaths(final int layer, final int node) {
        assert(layer >= 0);
        assert(layer < cgpParameters.numLayers());
        assert(node >= 0);
        assert(node < cgpParameters.numNodesPerLayer());
        return new HashSet<>(nodes[layer][node].executionPaths.get());
    }
    
    public Set<Integer> getExecutionPaths(final int input) {
        assert(input >= 0);
        assert(input < cgpParameters.numInputs());
        return new HashSet<>(inputExecutionPaths.get().get(input));
    }
    
    @Override
    public long getID() {
        return id++;
    }

    @Override
    public double getFitness() {
        if (fitness.isDefined())
            return fitness.get();
        else
            throw new IllegalStateException(String.format("%s: attempted to read the fitness of an individual whose fitness has not been evaluated.", this.getClass().getSimpleName()));
    }
    
    @Override
    public boolean isEvaluated() {
        return fitness.isDefined();
    }

    @Override
    public CartesianIndividual setFitness(double fitness) {
        return new Builder(this).setFitness(fitness).build();
    }
    
    @Override
    public CartesianIndividual clearFitness() {
        return new Builder(this).clearFitness().build();
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

    private CartesianIndividual(final Builder builder) {
        assert(builder != null);
        cgpParameters = builder.cgpParameters;
        nodes = builder.nodes;
        outputSources = builder.outputSources;
        fitness = builder.fitness;
        inputExecutionPaths = builder.inputExecutionPaths;
        assert(repOK());
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
    
    /** Annotate each node in this circuit with a set listing the outputs that it
     * makes a direct or indirect contribution to.
     * 
     * @return A copy of this individual that has annotated nodes.
     */
    public CartesianIndividual computeExecutionPaths() {
        final Builder newInd = new Builder(this);
        // Annotate the nodes that feed directly into each of the outputs
        for (int o = 0; o < cgpParameters.numOutputs(); o++) {
            final int oSource = outputSources[o];
            if (isInputID(oSource))
                newInd.addInputExecutionPath(oSource, o);
            else {
                final int sourceLayer = nodeIDtoLayer(oSource);
                final int sourceColumn = nodeIDtoColumn(oSource);
                newInd.addExecutionPath(sourceLayer, sourceColumn, o);
            }
        }
        
        // Propogate those annotations backwards through the graph
        for (int i = cgpParameters.numLayers() - 1; i >= 0; i--) {
            for (int j = 0; j < cgpParameters.numNodesPerLayer(); j++) {
                // Retrieve the set ofexecution paths this node belongs to
                if (!newInd.getExecutionPaths(i, j).isDefined()) // If they aren't defined at this point, then it means the node is not part of any execution path
                    newInd.addExecutionPaths(i, j, new HashSet<Integer>()); // So add the empty set
                final Option<Set<Integer>> executionPaths = newInd.getExecutionPaths(i, j);
                
                // Retrieve the set of nodes/inputs that feed into this one (parents)
                final int[] nodeSources = nodes[i][j].inputSources;
                // Annotate each one with the execution paths of the child
                for (final int k : nodeSources) {
                    if (isInputID(k)) // Executin paths of input nodes are stored differently
                        newInd.addInputExecutionPaths(k, executionPaths.get());
                    else {
                        final int kLayer = nodeIDtoLayer(k);
                        final int kColumn = nodeIDtoColumn(k);
                        newInd.addExecutionPaths(kLayer, kColumn, executionPaths.get());
                    }
                }
            }
        }
        assert(repOK());
        return newInd.build();
    }
    
    private boolean isInputID(final int id) {
        return id < cgpParameters.numInputs();
    }
    
    private int nodeIDtoLayer(final int id) {
        assert(id >= cgpParameters.numInputs());
        assert(id < cgpParameters.numInputs() + cgpParameters.numLayers()*cgpParameters.numNodesPerLayer());
        return (id - cgpParameters.numInputs())/cgpParameters.numNodesPerLayer();
    }
    
    private int nodeIDtoColumn(final int id) {
        assert(id >= cgpParameters.numInputs());
        assert(id < cgpParameters.numInputs() + cgpParameters.numLayers()*cgpParameters.numNodesPerLayer());
        return (id - cgpParameters.numInputs()) % cgpParameters.numNodesPerLayer();
    }
    
    public static class Builder {
        private final CGPParameters cgpParameters;
        private Node[][] nodes;
        private int[] outputSources;
        private Option<Double> fitness = Option.NONE;
        private Option<List<Set<Integer>>> inputExecutionPaths = Option.NONE;
        
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
    
        private Builder(final CartesianIndividual ref) {
            assert(ref != null);
            cgpParameters = ref.cgpParameters;
            nodes = ref.nodes;
            outputSources = ref.outputSources;
            fitness = ref.fitness;
        }
        
        public CartesianIndividual build() {
            return new CartesianIndividual(this);
        }
        
        public Builder setFitness(final double fitness) {
            this.fitness = new Option<>(fitness);
            return this;
        }
        
        public Builder clearFitness() {
            fitness = Option.NONE;
            return this;
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
        
        public Builder addExecutionPath(final int layer, final int node, final int output) {
            assert(layer >= 0);
            assert(layer < cgpParameters.numLayers());
            assert(node >= 0);
            assert(node < cgpParameters.numNodesPerLayer());
            nodes[layer][node] = nodes[layer][node].addExecutionPath(output);
            assert(nodeFitsConstraints(nodes[layer][node], layer));
            return this;
        }
        
        public Builder addExecutionPaths(final int layer, final int node, final Set<Integer> outputs) {
            assert(layer >= 0);
            assert(layer < cgpParameters.numLayers());
            assert(node >= 0);
            assert(node < cgpParameters.numNodesPerLayer());
            nodes[layer][node] = nodes[layer][node].addExecutionPaths(outputs);
            assert(nodeFitsConstraints(nodes[layer][node], layer));
            return this;
        }
        
        public Builder addInputExecutionPath(final int input, final int output) {
            assert(input >= 0);
            assert(input < cgpParameters.numInputs());
            if (!inputExecutionPaths.isDefined()) {
                final List<Set<Integer>> emptySets = new ArrayList<>();
                for (int i = 0; i < cgpParameters.numInputs(); i++)
                    emptySets.add(new HashSet<Integer>());
                inputExecutionPaths = new Option<>(emptySets);
            }
            inputExecutionPaths.get().get(input).add(output);
            return this;
        }
        
        public Builder addInputExecutionPaths(final int input, final Set<Integer> outputs) {
            assert(input >= 0);
            assert(input < cgpParameters.numInputs());
            if (!inputExecutionPaths.isDefined()) {
                final List<Set<Integer>> emptySets = new ArrayList<>();
                for (int i = 0; i < cgpParameters.numInputs(); i++)
                    emptySets.add(new HashSet<Integer>());
                inputExecutionPaths = new Option<>(emptySets);
            }
            inputExecutionPaths.get().get(input).addAll(outputs);
            return this;
        }
        
        public Option<Set<Integer>> getExecutionPaths(final int layer, final int node) {
            assert(layer >= 0);
            assert(layer < cgpParameters.numLayers());
            assert(node >= 0);
            assert(node < cgpParameters.numNodesPerLayer());
            return nodes[layer][node].executionPaths;
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
        final Option<Set<Integer>> executionPaths;
        
        Node(final BooleanFunction function, final int[] inputSources) {
            this(function, inputSources, Option.NONE);
        }
        
        private Node(final BooleanFunction function, final int[] inputSources, final Option<Set<Integer>> executionPaths) {
            assert(function != null);
            assert(inputSources != null);
            assert(inputSources.length >= function.arity());
            this.function = function;
            this.inputSources = Arrays.copyOf(inputSources, inputSources.length);
            this.executionPaths = executionPaths;
            assert(repOK());
        }
        
        public List<Integer> getExecutionPaths() {
            assert(repOK());
            if (!executionPaths.isDefined())
                throw new IllegalStateException("Cannot access attribute—this field has not been defined.");
            return new ArrayList<>(executionPaths.get());
        }
        
        public Node addExecutionPath(final int output) {
            assert(repOK());
            final Set<Integer> newSet = executionPaths.isDefined() ? new HashSet<>(executionPaths.get()) : new HashSet<Integer>();
            newSet.add(output);
            return new Node(function, inputSources, new Option<>(newSet));
        }
        
        public Node addExecutionPaths(final Set<Integer> outputs) {
            assert(repOK());
            final Set<Integer> newSet = executionPaths.isDefined() ? new HashSet<>(executionPaths.get()) : new HashSet<Integer>();
            newSet.addAll(outputs);
            return new Node(function, inputSources, new Option<>(newSet));
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return function != null
                    && inputSources != null
                    && inputSources.length >= function.arity()
                    && executionPaths != null
                    && !(executionPaths.isDefined() && Misc.containsNulls(executionPaths.get()));
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Node))
                return false;
            final Node ref = (Node)o;
            return Arrays.equals(inputSources, ref.inputSources)
                    && function.equals(ref.function)
                    && executionPaths.equals(ref.executionPaths);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + Objects.hashCode(this.function);
            hash = 37 * hash + Arrays.hashCode(this.inputSources);
            hash = 37 * hash + Objects.hashCode(this.executionPaths);
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: function=%s, inputSources=%s, executionPaths=%s]", this.getClass().getSimpleName(),
                    function, Arrays.toString(inputSources), executionPaths);
        }
        // </editor-fold>
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return cgpParameters != null
                && nodes != null
                && nodes.length == cgpParameters.numLayers()
                && outputSources.length == cgpParameters.numOutputs()
                && !Misc.containsNulls(nodes)
                && Misc.allRowsEqualLength(nodes)
                && sourcesFitConstraints(nodes)
                && fitness != null;
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
                && fitness.equals(ref.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.cgpParameters);
        hash = 19 * hash + Arrays.deepHashCode(this.nodes);
        hash = 19 * hash + Arrays.hashCode(this.outputSources);
        hash = 19 * hash + Objects.hashCode(this.fitness);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: cgpParameters=%s, nodes=%s, outputSources=%s, fitness=%s]", this.getClass().getSimpleName(),
                cgpParameters, Arrays.toString(nodes), Arrays.toString(outputSources), fitness);
    }
    // </editor-fold>
}