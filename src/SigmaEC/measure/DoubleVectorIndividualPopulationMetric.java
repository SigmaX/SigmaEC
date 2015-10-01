package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.CloneDecoder;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prints a population of DoubleVectorIndividuals.  If the population has
 * a different kind of genotype, but a phenotype that can be interpreted as a
 * DoubleVectorIndividual, a decoder may be used to print the phenotypes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleVectorIndividualPopulationMetric<T extends DoubleVectorIndividual> extends PopulationMetric<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_BEST_ONLY = "bestOnly";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    public final static String P_DIMENSIONS = "numDimensions";
    
    private final Decoder<T, DoubleVectorIndividual> decoder;
    private final Option<FitnessComparator<T>> fitnessComparator;
    private final int numDimensions;
    
    public DoubleVectorIndividualPopulationMetric(final Parameters parameters, final String base) {
        final Option<Decoder> decoderOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        if (decoderOpt.isDefined())
            decoder = decoderOpt.get();
        else
            decoder = new CloneDecoder(parameters, base);
        final boolean bestOnly = parameters.getBooleanParameter(Parameters.push(base, P_BEST_ONLY));
        final Option<FitnessComparator<T>> fitnessComparatorOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        if (!bestOnly && fitnessComparatorOpt.isDefined())
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, String.format("ignoring '%s' because '%s' is false.", P_FITNESS_COMPARATOR, P_BEST_ONLY));
        if (bestOnly && !fitnessComparatorOpt.isDefined())
            throw new IllegalStateException(String.format("%s: '%s' is true, but '%s' is undefined.", this.getClass().getSimpleName(), P_BEST_ONLY, P_FITNESS_COMPARATOR));
        fitnessComparator = bestOnly ? fitnessComparatorOpt : Option.NONE;
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_DIMENSIONS));
        assert(repOK());
    }
    
    @Override
    public MultipleDoubleArrayMeasurement measurePopulation(int run, int generation, final Population<T> population) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(population != null);
        final List<double[]> arrays = new ArrayList<double[]>() {{
            for (int i = 0; i < population.numSuppopulations(); i++) {
                if (fitnessComparator.isDefined()) { // Record only the best individual in each subpopulation.
                    final T best = population.getBest(i, fitnessComparator.get());
                    final double[] genome = decoder.decode(best).getGenomeArray();
                    assert(genome.length == numDimensions);
                    add(Misc.prepend(i, Misc.prepend(best.getID(), genome)));
                }
                else // Record all individuals.
                    for(final T ind : population.getSubpopulation(i)) {
                        final double[] genome = decoder.decode(ind).getGenomeArray();
                        assert(genome.length == numDimensions);
                        add(Misc.prepend(i, Misc.prepend(ind.getID(), genome)));
                    }
            }
        }};
        assert(repOK());
        return new MultipleDoubleArrayMeasurement(run, generation, arrays);
    }

    @Override
    public String csvHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("run, generation, subpopulation");
        for (int i = 0; i < numDimensions; i++)
            sb.append(", V").append(i);
        return sb.toString();
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }
    
    @Override
    public void close() { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK()
    {
        return decoder != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%s: decoder=%s]", this.getClass().getSimpleName(), decoder);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DoubleVectorIndividualPopulationMetric))
            return false;
        final DoubleVectorIndividualPopulationMetric ref = (DoubleVectorIndividualPopulationMetric)o;
        return decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
