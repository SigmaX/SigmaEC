package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.CloneDecoder;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
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
public class DoubleVectorPopulationMetric<T extends Individual> extends PopulationMetric<T> {
    public final static String P_DECODER = "decoder";
    public final static String P_BEST_ONLY = "bestOnly";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    public final static String P_DIMENSIONS = "numDimensions";
    
    private final Decoder<T, DoubleVectorIndividual> decoder;
    private final Option<FitnessComparator<T>> fitnessComparator;
    private final int numDimensions;
    
    public DoubleVectorPopulationMetric(final Parameters parameters, final String base) {
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
    public MultipleMeasurement measurePopulation(final int run, final int step, final Population<T> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        final List<DoubleVectorMeasurement> measurements = new ArrayList<DoubleVectorMeasurement>() {{
            for (int i = 0; i < population.numSuppopulations(); i++) {
                if (fitnessComparator.isDefined()) { // Record only the best individual in each subpopulation.
                    final T best = population.getBest(i, fitnessComparator.get());
                    add(measureIndividual(run, step, i, best));
                }
                else // Record all individuals.
                    for(final T ind : population.getSubpopulation(i))
                        add(measureIndividual(run, step, i, ind));
            }
        }};
        assert(repOK());
        return new MultipleMeasurement(measurements);
    }
    
    private DoubleVectorMeasurement measureIndividual(final int run, final int step, final int subpop, final T individual) {
        assert(run >= 0);
        assert(step >= 0);
        assert(subpop >= 0);
        assert(individual != null);
        final DoubleVectorIndividual decodedInd = decoder.decode(individual);
        assert(decodedInd.size() == numDimensions);
        return new DoubleVectorMeasurement(run, step, subpop, individual.getFitness(), individual.getID(), decodedInd.getGenomeArray());
    }

    @Override
    public String csvHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("run, step, subpopulation, individualID, fitness");
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
        if (!(o instanceof DoubleVectorPopulationMetric))
            return false;
        final DoubleVectorPopulationMetric ref = (DoubleVectorPopulationMetric)o;
        return decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
    
    public static class DoubleVectorMeasurement extends Measurement {
        private final int run;
        private final int step;
        private final int subpopulation;
        private final double fitness;
        private final long indID;
        private final double[] values;

        public DoubleVectorMeasurement(final int run, final int step, final int subpopulation, final double fitness, final long indID, final double[] values) {
            assert(run >= 0);
            assert(step >= 0);
            assert(subpopulation >= 0);
            assert(indID >= 0);
            assert(values != null);
            this.run = run;
            this.step = step;
            this.subpopulation = subpopulation;
            this.fitness = fitness;
            this.indID = indID;
            this.values = Arrays.copyOf(values, values.length);
            assert(repOK());
        }
        
        @Override
        public int getRun() { return run; }

        @Override
        public int getStep() { return step; }
        
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d, %d, %d, %d, %f", run, step, subpopulation, indID, fitness));
            for (final double d : values)
                sb.append(", ").append(d);
            assert(repOK());
            return sb.toString();
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return run >= 0
                    && step >= 0
                    && subpopulation >= 0
                    && indID >= 0
                    && values != null;
        }

        @Override
        public boolean equals(final Object o) {
            if(!(o instanceof DoubleVectorMeasurement))
                return false;
            final DoubleVectorMeasurement ref = (DoubleVectorMeasurement)o;
            return run == ref.run
                    && step == ref.step
                    && subpopulation == ref.subpopulation
                    && indID == ref.indID
                    && Misc.doubleEquals(fitness, ref.fitness)
                    && Misc.doubleArrayEquals(values, ref.values);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + this.run;
            hash = 79 * hash + this.step;
            hash = 79 * hash + this.subpopulation;
            hash = 79 * hash + (int) (Double.doubleToLongBits(this.fitness) ^ (Double.doubleToLongBits(this.fitness) >>> 32));
            hash = 79 * hash + (int) (this.indID ^ (this.indID >>> 32));
            hash = 79 * hash + Arrays.hashCode(this.values);
            return hash;
        }
        // </editor-fold>
    }
}
