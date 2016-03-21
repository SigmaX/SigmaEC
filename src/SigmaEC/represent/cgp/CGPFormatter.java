package SigmaEC.represent.cgp;

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
        throw new UnsupportedOperationException();
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
