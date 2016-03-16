package SigmaEC.evaluate.objective;

import SigmaEC.represent.Individual;
import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.util.Parameters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * An objective that launches an external command to perform fitness evaluation.
 * 
 * @author Eric O. Scott
 */
public class ExternalObjective<T extends Individual> extends ObjectiveFunction<T> {
    public final static String P_DIMENSIONS = "dimensions";
    public final static String P_COMMAND = "command";
    public final static String P_FORMATTER = "formatter";
    
    private final int dimensions;
    private final String command;
    private final GenomeFormatter<T> formatter;

    public ExternalObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        dimensions = parameters.getIntParameter(Parameters.push(base, P_DIMENSIONS));
        command = parameters.getStringParameter(Parameters.push(base, P_COMMAND));
        formatter = parameters.getInstanceFromParameter(Parameters.push(base, P_FORMATTER), GenomeFormatter.class);
        assert(repOK());
    }
    
    @Override
    public double fitness(final T ind) {
        try {
            final Process p = Runtime.getRuntime().exec(command);
            final Writer carlSimInput = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            carlSimInput.write(formatter.genomeToString(ind));
            carlSimInput.close(); // Sends EOF
            p.waitFor();
            
            System.err.println(streamToString(p.getErrorStream()));
            return Double.valueOf(streamToString(p.getInputStream()));
        } catch (final Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String streamToString(final InputStream s) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(s));
        final StringBuilder sb = new StringBuilder();
        String line = "";			
        while ((line = reader.readLine())!= null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void setStep(final int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() {
        return dimensions;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DIMENSIONS != null
                && !P_DIMENSIONS.isEmpty()
                && P_COMMAND != null
                && !P_COMMAND.isEmpty()
                && P_FORMATTER != null
                && !P_FORMATTER.isEmpty()
                && dimensions > 0
                && command != null
                && !command.isEmpty()
                && formatter != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ExternalObjective))
            return false;
        final ExternalObjective ref = (ExternalObjective)o;
        return dimensions == ref.dimensions
                && command.equals(ref.command)
                && formatter.equals(ref.formatter);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.dimensions;
        hash = 17 * hash + Objects.hashCode(this.command);
        hash = 17 * hash + Objects.hashCode(this.formatter);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_DIMENSIONS, dimensions,
                P_COMMAND, command,
                P_FORMATTER, formatter);
    }
    // </editor-fold>
}
