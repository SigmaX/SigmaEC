package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.CloneDecoder;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric O. Scott
 */
public class FormattedPopulationMetric<T extends Individual, P extends Individual> extends PopulationMetric<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_BEST_ONLY = "bestOnly";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    public final static String P_FORMATTER = "formatter";
    
    private final Decoder<T, P> decoder;
    private final Option<FitnessComparator<T>> fitnessComparator;
    private final GenomeFormatter<P> formatter;
    
    public FormattedPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), new CloneDecoder(parameters, base), Decoder.class);
        final boolean bestOnly = parameters.getBooleanParameter(Parameters.push(base, P_BEST_ONLY));
        final Option<FitnessComparator<T>> fitnessComparatorOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        if (!bestOnly && fitnessComparatorOpt.isDefined())
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, String.format("ignoring '%s' because '%s' is false.", P_FITNESS_COMPARATOR, P_BEST_ONLY));
        if (bestOnly && !fitnessComparatorOpt.isDefined())
            throw new IllegalStateException(String.format("%s: '%s' is true, but '%s' is undefined.", this.getClass().getSimpleName(), P_BEST_ONLY, P_FITNESS_COMPARATOR));
        fitnessComparator = bestOnly ? fitnessComparatorOpt : Option.NONE;
        formatter = parameters.getInstanceFromParameter(Parameters.push(base, P_FORMATTER), GenomeFormatter.class);
        assert(repOK());
    }

    @Override
    public MultipleStringMeasurement measurePopulation(final int run, final int step, final Population<T> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        ping(step, population);
        final List<String> measurements = new ArrayList<String>() {{
            for (int i = 0; i < population.numSuppopulations(); i++) {
                if (fitnessComparator.isDefined()) { // Record only the best individual in each subpopulation.
                    final T best = population.getBest(i, fitnessComparator.get());
                    add(formatter.genomeToString(decoder.decode(best)));
                }
                else // Record all individuals.
                    for(final T ind : population.getSubpopulation(i))
                        add(formatter.genomeToString(decoder.decode(ind)));
            }
        }};
        assert(repOK());
        return new MultipleStringMeasurement(run, step, measurements);
    }

    @Override
    public void ping(int step, Population<T> population) {
        // Do nothing
    }

    @Override
    public String csvHeader() {
        return "";
    }

    @Override
    public void reset() {
        // Do nothing
    }

    @Override
    public void flush() {
        // Do nothing
    }

    @Override
    public void close() {
        // Do nothing
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_BEST_ONLY != null
                && !P_BEST_ONLY.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_FITNESS_COMPARATOR != null
                && !P_FITNESS_COMPARATOR.isEmpty()
                && P_FORMATTER != null
                && !P_FORMATTER.isEmpty()
                && decoder != null
                && fitnessComparator != null
                && formatter != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FormattedPopulationMetric))
            return false;
        final FormattedPopulationMetric ref = (FormattedPopulationMetric)o;
        return fitnessComparator.equals(ref.fitnessComparator)
                && formatter.equals(ref.formatter)
                && decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.decoder);
        hash = 97 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 97 * hash + Objects.hashCode(this.formatter);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_DECODER, decoder,
                P_FITNESS_COMPARATOR, fitnessComparator,
                P_FORMATTER, formatter);
    }
    // </editor-fold>
}
