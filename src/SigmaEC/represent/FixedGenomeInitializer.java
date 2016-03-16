package SigmaEC.represent;

import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Initialized a population based on a user-specified set of constant genomes.
 * 
 * @author Eric O. Scott
 */
public class FixedGenomeInitializer<T extends Individual> extends Initializer<T> {
    public final static String P_FORMATTER = "formatter";
    public final static String P_DELIMITER = "delimiter";
    public final static String P_INDIVIDUALS = "individuals";
    
    private final GenomeFormatter<T> formatter;
    private final String delimiter;
    private final List<T> individuals;
    
    private int i = 0;
    
    public FixedGenomeInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        formatter = parameters.getInstanceFromParameter(Parameters.push(base, P_FORMATTER), GenomeFormatter.class);
        delimiter = parameters.getOptionalStringParameter(Parameters.push(base, P_DELIMITER), ";");
        final String individualsStr = parameters.getStringParameter(Parameters.push(base, P_INDIVIDUALS));
        individuals = readIndividuals(individualsStr);
        assert(repOK());
    }
    
    private List<T> readIndividuals(final String individualsStr) {
        assert(individualsStr != null);
        assert(!individualsStr.isEmpty());
        final String[] splitInds = individualsStr.split(delimiter);
        return new ArrayList<T>() {{
            for (final String ind : splitInds)
                add(formatter.stringToGenome(ind));
        }};
    }
    
    @Override
    public List<T> generatePopulation() {
        return new ArrayList<>(individuals); // Defensive copy
    }

    @Override
    public T generateIndividual() {
        return individuals.get(i++ % individuals.size());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_FORMATTER != null
                && !P_FORMATTER.isEmpty()
                && P_DELIMITER != null
                && !P_DELIMITER.isEmpty()
                && P_INDIVIDUALS != null
                && !P_INDIVIDUALS.isEmpty()
                && formatter != null
                && delimiter != null
                && !delimiter.isEmpty()
                && individuals != null
                && !individuals.isEmpty()
                && !Misc.containsNulls(individuals)
                && i >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FixedGenomeInitializer))
            return false;
        final FixedGenomeInitializer ref = (FixedGenomeInitializer)o;
        return i == ref.i
                && delimiter.equals(ref.delimiter)
                && formatter.equals(ref.formatter)
                && individuals.equals(ref.individuals);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.formatter);
        hash = 61 * hash + Objects.hashCode(this.delimiter);
        hash = 61 * hash + Objects.hashCode(this.individuals);
        hash = 61 * hash + this.i;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, i=%d]", this.getClass().getSimpleName(),
                P_DELIMITER, delimiter,
                P_FORMATTER, formatter,
                P_INDIVIDUALS, individuals,
                i);
    }
    // </editor-fold>
}
