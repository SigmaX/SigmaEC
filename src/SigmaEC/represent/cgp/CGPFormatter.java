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
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class CGPFormatter extends GenomeFormatter<CartesianIndividual> {
    public final static String P_OUTPUT_FORMAT = "outputFormat";
    
    public static enum OutputFormat { TIKZ, DOT };
    private final OutputFormat outputFormat;
    
    public CGPFormatter(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        outputFormat = OutputFormat.valueOf(parameters.getStringParameter(Parameters.push(base, P_OUTPUT_FORMAT)));
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
    
    private static String cgpToDot(final CartesianIndividual individual) {
        assert(individual != null);
        final CGPParameters p = individual.cgpParmaeters();
        final StringBuilder sb = new StringBuilder("graph X {\n");
        
        throw new UnsupportedOperationException();
    }
    
    private static String cgpToTikz(final CartesianIndividual individual) {
        assert(individual != null);
        final CGPParameters p = individual.cgpParmaeters();
        final StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[circuit logic US, every circuit symbol/.style={thick}]\n");
        // Inputs
        final int spacing = 2;
        for (int i = 0; i < p.numInputs(); i++)
            sb.append(String.format("\t\\node[buffer gate, point down,draw=none] (node%d) at (%d,0) {\\rotatebox{90}{$I_{%d}$}};\n", i, i*spacing, i));
        // Nodes and edges
        for (int i = 0; i < p.numLayers(); i++) {
            for (int j = 0; j < p.numNodesPerLayer(); j++) {
                final int thisNodeID = p.numInputs() + i*p.numNodesPerLayer() + j;
                // Print node
                final BooleanFunction function = individual.getFunction(i, j);
                sb.append(String.format("\t\\node[%s,inputs={%s}, point down] (node%d) at (%d,-%d) {};\n", functionName(function), functionInputs(function), thisNodeID, j*spacing, (i + 1)*spacing));
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
            sb.append(String.format("\t\\node[buffer gate, point down,draw=none] (out%d) at (%d,-%d) {\\rotatebox{90}{$O_{%d}$}};\n", i, i*spacing, spacing*(p.numLayers() + 1), i));
            sb.append(String.format("\t\t\\draw (node%d.output) -- ++(down:5mm) -| (out%d);\n", outputSources[i], i));
        }
        sb.append("\\end{tikzpicture}\n");
        return sb.toString();
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
