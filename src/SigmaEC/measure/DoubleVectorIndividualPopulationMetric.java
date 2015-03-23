package SigmaEC.measure;

import SigmaEC.represent.CloneDecoder;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
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
public class DoubleVectorIndividualPopulationMetric<T extends Individual> extends PopulationMetric<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_BEST_ONLY = "bestOnly";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    
    private final Decoder<T, DoubleVectorIndividual> decoder;
    private final Option<FitnessComparator<T>> fitnessComparator;
    
    public DoubleVectorIndividualPopulationMetric(final Parameters parameters, final String base) {
        final Option<Decoder> decoderOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        if (decoderOpt.isDefined())
            decoder = decoderOpt.get();
        else
            decoder = new CloneDecoder(parameters, base);
        final boolean bestOnly = parameters.getBooleanParameter(Parameters.push(base, P_BEST_ONLY));
        final Option<FitnessComparator<DoubleVectorIndividual>> fitnessComparatorOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        if (!bestOnly && fitnessComparatorOpt.isDefined())
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, String.format("ignoring '%s' because '%s' is false.", P_FITNESS_COMPARATOR, P_BEST_ONLY));
        if (bestOnly && !fitnessComparatorOpt.isDefined())
            throw new IllegalStateException(String.format("%s: '%s' is true, but '%s' is undefined.", this.getClass().getSimpleName(), P_BEST_ONLY, P_FITNESS_COMPARATOR));
        fitnessComparator = bestOnly ? fitnessComparatorOpt : Option.NONE;
        assert(repOK());
    }
    
    @Override
    public MultipleDoubleArrayMeasurement measurePopulation(int run, int generation, final List<T> population) {
        final List<double[]> arrays = new ArrayList<double[]>() {{
            if (fitnessComparator.isDefined()) {
                final T best = Statistics.max(population, fitnessComparator.get());
                add(Misc.prepend(best.getID(), decoder.decode(best).getGenomeArray()));
            }
            else
                for(T ind : population)
                    add(Misc.prepend(ind.getID(), decoder.decode(ind).getGenomeArray()));
        }};
        assert(repOK());
        return new MultipleDoubleArrayMeasurement(run, generation, arrays);
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
