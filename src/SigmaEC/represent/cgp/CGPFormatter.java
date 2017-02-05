package SigmaEC.represent.cgp;

import SigmaEC.evaluate.objective.function.AND;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.NAND;
import SigmaEC.evaluate.objective.function.NOR;
import SigmaEC.evaluate.objective.function.NOT;
import SigmaEC.evaluate.objective.function.OR;
import SigmaEC.evaluate.objective.function.Shunt;
import SigmaEC.evaluate.objective.function.XOR;
import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Eric O. Scott
 */
public class CGPFormatter extends GenomeFormatter<CartesianIndividual> {
    public final static String P_OUTPUT_FORMAT = "outputFormat";
    public final static String P_ANNOTATE_PATHS = "annotatePaths";
    public final static String P_PATH_COLORS = "pathColors";
    public final static String P_PATHS_TO_HIGHLIGHT = "pathsToHighlight";
    
    private final static String DEFAULT_COLOR = "black";
    private final static String INTRON_COLOR = "gray";
    
    public static enum OutputFormat { TIKZ, DOT };
    private final OutputFormat outputFormat;
    private final boolean annotatePaths;
    private final Option<String[]> pathColors;
    private final Option<Set<Integer>> pathsToHighlight;
    
    public CGPFormatter(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        outputFormat = OutputFormat.valueOf(parameters.getStringParameter(Parameters.push(base, P_OUTPUT_FORMAT)));
        annotatePaths =  parameters.getOptionalBooleanParameter(Parameters.push(base, P_ANNOTATE_PATHS), true);
        pathColors = parameters.getOptionalStringArrayParameter(Parameters.push(base, P_PATH_COLORS));
        final Option<int[]> pathsToHighlightArray = parameters.getOptionalIntArrayParameter(Parameters.push(base, P_PATHS_TO_HIGHLIGHT));
        pathsToHighlight = !pathsToHighlightArray.isDefined() ? Option.NONE : new Option<>(new HashSet<Integer>() {{
                for (final int path : pathsToHighlightArray.get())
                    add(path);
        }});
        assert(repOK());
    }
    
    @Override
    public String genomeToString(final CartesianIndividual individual) {
        assert(individual != null);
        switch(outputFormat) {
            case TIKZ:
                return cgpToTikz(individual);
            case DOT:
                return cgpToDot(individual);
            default:
                throw new IllegalStateException(String.format("%s: invalid output format encountered.", this.getClass().getSimpleName()));
        }
    }
    
    private String cgpToDot(final CartesianIndividual individual) {
        assert(individual != null);
        final CGPParameters p = individual.cgpParameters();
        final StringBuilder sb = new StringBuilder("graph X {\n");
        
        throw new UnsupportedOperationException();
    }
    
    private String cgpToTikz(CartesianIndividual individual) {
        assert(individual != null);
        if (annotatePaths)
            individual = individual.computeExecutionPaths();
        final String[] colors = pathColors.isDefined() ? pathColors.get() : getDefaultColors(individual.numOutputs());
        if (colors.length < individual.numOutputs() + 1)
            throw new IllegalStateException(String.format("%s: received individual with %d outputs, but '%s' only specifies %d colors (need %d).", this.getClass().getSimpleName(), individual.numOutputs(), P_PATH_COLORS, colors.length, individual.numOutputs() + 1));
        
        final CGPParameters p = individual.cgpParameters();
        final StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[circuit logic US, every circuit symbol/.style={thick}]\n");
        // Inputs
        final int spacing = 2;
        for (int i = 0; i < p.numInputs(); i++)
            sb.append(String.format("\t\\node[%s,buffer gate,point down,draw=none] (node%d) at (%d,0) {\\rotatebox{90}{$I_{%d}$}};\n", getColorForInput(colors, individual, i), i, i*spacing, i));
        // Nodes and edges
        for (int i = 0; i < p.numLayers(); i++) {
            for (int j = 0; j < p.numNodesPerLayer(); j++) {
                final int thisNodeID = p.numInputs() + i*p.numNodesPerLayer() + j;
                // Print node
                final BooleanFunction function = individual.getFunction(i, j);
                sb.append(String.format("\t\\node[%s,%s,inputs={%s}, point down] (node%d) at (%d,-%d) {};\n", getColorForNode(colors, individual, i, j), functionName(function), functionInputs(function), thisNodeID, j*spacing, (i + 1)*spacing));
                // Print input edges
                final int[] inputs = individual.getInputs(i, j);
                for (int k = 0; k < inputs.length; k++)
                    sb.append(String.format("\t\t\\draw (node%d.output) -- ++(down:5mm) -| (node%d.input %d);\n", inputs[k], thisNodeID, k + 1));
            }
        }
        // Outputs
        final int[] outputSources = individual.getOutputSources();
        assert(outputSources.length == p.numOutputs());
        for (int i = 0; i < p.numOutputs(); i++) {
            sb.append(String.format("\t\\node[%s,buffer gate, point down,draw=none] (out%d) at (%d,-%d) {\\rotatebox{90}{$O_{%d}$}};\n", getColorForOutput(colors, i), i, i*spacing, spacing*(p.numLayers() + 1), i));
            sb.append(String.format("\t\t\\draw (node%d.output) -- ++(down:5mm) -| (out%d);\n", outputSources[i], i));
        }
        sb.append("\\end{tikzpicture}\n");
        return sb.toString();
    }
    
    private static String[] getDefaultColors(final int numOutputs) {
        final String[] colors = new String[numOutputs + 1];
        colors[0] = INTRON_COLOR;
        for (int i = 1; i < colors.length; i++)
            colors[i] = DEFAULT_COLOR;
        return colors;
    }
    
    private String getColorForNode(final String[] colors, final CartesianIndividual<?> individual, final int layer, final int column) {
        assert(individual != null);
        assert(layer >= 0);
        assert(layer < individual.cgpParameters().numLayers());
        assert(column >= 0);
        assert(column < individual.cgpParameters().numNodesPerLayer());
        if (!annotatePaths)
            return DEFAULT_COLOR;
        if (individual.getExecutionPaths(layer, column).isEmpty())
            return INTRON_COLOR;
        int lowestPath = Integer.MAX_VALUE;
        for (final int executionPath : individual.getExecutionPaths(layer, column))
            if (executionPath < lowestPath && (!pathsToHighlight.isDefined() || pathsToHighlight.get().contains(executionPath)))
                lowestPath = executionPath;
        final boolean onHighlightedPath = !(lowestPath == Integer.MAX_VALUE);
        return !onHighlightedPath ? getNonHighlightedColor() : colors[lowestPath + 1];
    }
    
    private String getColorForInput(final String[] colors, final CartesianIndividual<?> individual, final int input) {
        assert(individual != null);
        assert(input >= 0);
        assert(input < individual.cgpParameters().numInputs());
        if (!annotatePaths)
            return DEFAULT_COLOR;
        if (individual.getExecutionPaths(input).isEmpty())
            return INTRON_COLOR;
        int lowestPath = Integer.MAX_VALUE;
        for (final int executionPath : individual.getExecutionPaths(input))
            if (executionPath < lowestPath && (!pathsToHighlight.isDefined() || pathsToHighlight.get().contains(executionPath)))
                lowestPath = executionPath;
        final boolean onHighlightedPath = !(lowestPath == Integer.MAX_VALUE);
        return !onHighlightedPath ? getNonHighlightedColor() : colors[lowestPath + 1];
    }
    
    private String getNonHighlightedColor() {
        return pathColors.isDefined() ? DEFAULT_COLOR : INTRON_COLOR; 
    }
    
    private String getColorForOutput(final String[] colors, final int output) {
        assert(colors != null);
        assert(!Misc.containsNulls(colors));
        if (!annotatePaths || (pathsToHighlight.isDefined() && !pathsToHighlight.get().contains(output)))
            return DEFAULT_COLOR;
        assert(output >= 0);
        assert(output < colors.length - 1);
        return colors[output + 1];
    }
    
    private static String functionName(final BooleanFunction function) {
        assert(function != null);
        if (function instanceof NAND)
            return "nand gate";
        else if (function instanceof AND)
            return "and gate";
        else if (function instanceof OR)
            return "or gate";
        else if (function instanceof NOR)
            return "nor gate";
        else if (function instanceof XOR)
            return "xor gate";
        else if (function instanceof NOT)
            return "not gate";
        else if (function instanceof Shunt)
            return "buffer gate";
        else
            throw new UnsupportedOperationException();
    }
    
    private static String functionInputs(final BooleanFunction function) {
        assert(function != null);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < function.arity(); i++)
            sb.append("n");
        return sb.toString();
    }

    @Override
    public CartesianIndividual stringToGenome(String individual) {
        throw new UnsupportedOperationException();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_OUTPUT_FORMAT != null
                && !P_OUTPUT_FORMAT.isEmpty()
                && outputFormat != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CGPFormatter))
            return false;
        final CGPFormatter ref = (CGPFormatter)o;
        return outputFormat.equals(ref.outputFormat);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.outputFormat);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_OUTPUT_FORMAT, outputFormat);
    }
    // </editor-fold>
}
