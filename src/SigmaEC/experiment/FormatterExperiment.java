package SigmaEC.experiment;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A program that takes genomes as input on stdin, processes them in some way,
 * and prints a new representation of the individuals to stdout.
 * 
 * @author Eric O. Scott
 */
public class FormatterExperiment extends Experiment {
    public final static String P_INPUT_FORMATTER = "inputFormatter";
    public final static String P_OUTPUT_FORMATTER = "outputFormatter";
    public final static String P_DECODER = "decoder";
    
    private final GenomeFormatter inputFormatter;
    private final GenomeFormatter outputFormatter;
    private final Option<Decoder<Individual, Individual>> decoder;
    private String result = "";
    
    public FormatterExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        inputFormatter = parameters.getInstanceFromParameter(Parameters.push(base, P_INPUT_FORMATTER), GenomeFormatter.class);
        outputFormatter = parameters.getInstanceFromParameter(Parameters.push(base, P_OUTPUT_FORMATTER), GenomeFormatter.class);
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        assert(repOK());
    }
    
    @Override
    public void run() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final StringBuilder sb = new StringBuilder();
        String line = "";	
        try {
            while ((line = reader.readLine())!= null) {
                final Individual rawInd = inputFormatter.stringToGenome(line);
                final Individual processedInd = decoder.isDefined() ? decoder.get().decode(rawInd) : rawInd;
                final String indString = outputFormatter.genomeToString(processedInd);
                sb.append(indString);
                System.out.println(indString);
            }
        }
        catch (final IOException e) {
            e.printStackTrace(System.err);
            Logger.getLogger(FormatterExperiment.class.toString()).log(Level.SEVERE, "", e);
            System.exit(1);
        }
        result = sb.toString();
    }

    @Override
    public String getResult() {
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_INPUT_FORMATTER != null
                && !P_INPUT_FORMATTER.isEmpty()
                && P_OUTPUT_FORMATTER != null
                && !P_OUTPUT_FORMATTER.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && inputFormatter != null
                && outputFormatter != null
                && decoder != null
                && result != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FormatterExperiment))
            return false;
        final FormatterExperiment ref = (FormatterExperiment)o;
        return result.equals(ref.result)
                && inputFormatter.equals(ref.inputFormatter)
                && outputFormatter.equals(ref.outputFormatter)
                && decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.inputFormatter);
        hash = 23 * hash + Objects.hashCode(this.outputFormatter);
        hash = 23 * hash + Objects.hashCode(this.decoder);
        hash = 23 * hash + Objects.hashCode(this.result);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, result=%s]", this.getClass().getSimpleName(),
                P_INPUT_FORMATTER, inputFormatter,
                P_OUTPUT_FORMATTER, outputFormatter,
                P_DECODER, decoder,
                result);
    }
    // </editor-fold>
}
