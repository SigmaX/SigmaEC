package SigmaEC.measure;

import SigmaEC.evaluate.VectorFitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.select.VectorFitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class VectorFitnessStatisticsPopulationMetric<T extends Individual<VectorFitness>> extends PopulationMetric<T, VectorFitness> {
    final public static String P_ROW_PREFIX = "rowPrefix";
    public final static String P_SCALAR_COMPARATOR = "scalarFitnessComparator";
    public final static String P_VECTOR_COMPARATOR = "vectorFitnessComparator";
    public final static String P_NUM_DIMENSIONS = "numFitnessDimensions";
    
    private final FitnessComparator<T, VectorFitness> scalarFitnessComparator;
    private final VectorFitnessComparator<T, VectorFitness> vectorFitnessComparator;
    private final String rowPrefix;
    private final int numFitnessDimensions;
    private final VectorBestSoFarTable<T> bestSoFarTable;
    
    public VectorFitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        rowPrefix = parameters.getOptionalStringParameter(Parameters.push(base, P_ROW_PREFIX), "");
        scalarFitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_SCALAR_COMPARATOR), FitnessComparator.class);
        vectorFitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_VECTOR_COMPARATOR), FitnessComparator.class);
        numFitnessDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        bestSoFarTable = new VectorBestSoFarTable<>(scalarFitnessComparator, vectorFitnessComparator, numFitnessDimensions);
        assert(repOK());
    }

    @Override
    public Measurement measurePopulation(final int run, final int step, final Population<T, VectorFitness> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        ping(step, population);
        final List<VectorFitnessStatisticsMeasurement> measurements = new ArrayList<VectorFitnessStatisticsMeasurement>() {{
            for (int pop = 0; pop < population.numSuppopulations(); pop++) {
                    add(measureSubpopulationOnScalar(run, step, pop, population));
                for (int dim = 0; dim < numFitnessDimensions; dim++)
                    add(measureSubpopulationOnDimension(run, step, pop, dim, population));
            }  
        }};
        assert(repOK());
        return new MultipleMeasurement<>(measurements);   
    }
    
    private VectorFitnessStatisticsMeasurement measureSubpopulationOnScalar(final int run, final int step, final int subpop, final Population<T, VectorFitness> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(subpop >= 0);
        assert(population != null);
        assert(subpop < population.numSuppopulations());
        
        final List<T> subPopulation = population.getSubpopulation(subpop);
        
        final double[] fitnesses = new double[subPopulation.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = subPopulation.get(i).getFitness().asScalar();

        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);

        final double best = Statistics.best(subPopulation, scalarFitnessComparator).getFitness().asScalar();
        final double worst = Statistics.worst(subPopulation, scalarFitnessComparator).getFitness().asScalar();

        final T bsfInd = bestSoFarTable.getScalarBestSoFar(subpop);
        final double bestFitness = bsfInd.getFitness().asScalar();
        final long bestID = bsfInd.getID();
        return new VectorFitnessStatisticsMeasurement(rowPrefix, -1, "All", run, step, subpop, mean, std, best, worst, bestFitness, bestID);
    }
    
    private VectorFitnessStatisticsMeasurement measureSubpopulationOnDimension(final int run, final int step, final int subpop, final int dimension, final Population<T, VectorFitness> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(subpop >= 0);
        assert(population != null);
        assert(subpop < population.numSuppopulations());
        
        final List<T> subPopulation = population.getSubpopulation(subpop);
        
        final double[] fitnesses = new double[subPopulation.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = subPopulation.get(i).getFitness().getFitness(dimension);

        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);

        final VectorFitnessComparator<T, VectorFitness> comparatorForDimension = new VectorFitnessComparator<>(vectorFitnessComparator, dimension);
        final double best = Statistics.best(subPopulation, comparatorForDimension).getFitness().getFitness(dimension);
        final double worst = Statistics.worst(subPopulation, comparatorForDimension).getFitness().getFitness(dimension);

        final T bsfInd = bestSoFarTable.getBestSoFar(subpop, dimension);
        final double bestFitness = bsfInd.getFitness().getFitness(dimension);
        final long bestID = bsfInd.getID();
        return new VectorFitnessStatisticsMeasurement(rowPrefix, dimension, bsfInd.getFitness().getDimensionName(dimension), run, step, subpop, mean, std, best, worst, bestFitness, bestID);
    }

    @Override
    public void ping(final int step, final Population<T, VectorFitness> population) {
        assert(step >= 0);
        assert(population != null);
        if (step == 0)
            bestSoFarTable.init(step, population);
        else
            bestSoFarTable.update(step, population);
    }

    @Override
    public String csvHeader() {
        final String extraColumn = (rowPrefix.isEmpty()) ? "" : "experiment, ";
        return String.format("%ssubFunctionID, subFunction, run, step, subpopulation, mean, std, best, worst, bsf, bsf_individualID", extraColumn);
    }

    @Override
    public void reset() {
        bestSoFarTable.reset();
    }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_ROW_PREFIX != null
                && !P_ROW_PREFIX.isEmpty()
                && P_SCALAR_COMPARATOR != null
                && !P_SCALAR_COMPARATOR.isEmpty()
                && P_VECTOR_COMPARATOR != null
                && !P_VECTOR_COMPARATOR.isEmpty()
                && numFitnessDimensions > 0
                && rowPrefix != null
                && scalarFitnessComparator != null
                && vectorFitnessComparator != null
                && bestSoFarTable != null
                && bestSoFarTable.getNumFitnessDimensions() == numFitnessDimensions;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VectorFitnessStatisticsPopulationMetric))
            return false;
        final VectorFitnessStatisticsPopulationMetric ref = (VectorFitnessStatisticsPopulationMetric)o;
        return numFitnessDimensions == ref.numFitnessDimensions
                && rowPrefix.equals(ref.rowPrefix)
                && scalarFitnessComparator.equals(ref.scalarFitnessComparator)
                && vectorFitnessComparator.equals(ref.vectorFitnessComparator)
                && bestSoFarTable.equals(ref.bestSoFarTable);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.scalarFitnessComparator);
        hash = 43 * hash + Objects.hashCode(this.vectorFitnessComparator);
        hash = 43 * hash + Objects.hashCode(this.rowPrefix);
        hash = 43 * hash + this.numFitnessDimensions;
        hash = 43 * hash + Objects.hashCode(this.bestSoFarTable);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%s, bestSoFarTable=%s]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numFitnessDimensions,
                P_ROW_PREFIX, rowPrefix,
                P_SCALAR_COMPARATOR, scalarFitnessComparator,
                P_VECTOR_COMPARATOR, vectorFitnessComparator,
                bestSoFarTable);
    }
    // </editor-fold>
    
    public static class VectorFitnessStatisticsMeasurement extends Measurement {
        private final String rowPrefix;
        private final int subFunctionID;
        private final String subFunction;
        private final int run;
        private final int step;
        private final int subpopulation;
        private final double mean;
        private final double std;
        private final double best;
        private final double worst;
        private final double bestSoFar;
        private final long bsfIndividualID;
        
        public VectorFitnessStatisticsMeasurement(final String rowPrefix, final int subFunctionID, final String subFunction, final int run_, final int step_,
                final int subpopulation_, final double mean_, final double std_, final double best_, final double worst_, final double bsf_,final long bsfIndividualID_) {
            assert(rowPrefix != null);
            assert(subFunctionID >= -1);
            assert(subFunction != null);
            assert(!subFunction.isEmpty());
            this.rowPrefix = rowPrefix;
            this.subFunctionID = subFunctionID;
            this.subFunction = subFunction;
            run = run_;
            step = step_;
            subpopulation = subpopulation_;
            mean = mean_;
            std = std_;
            best = best_;
            worst = worst_;
            bestSoFar = bsf_;
            bsfIndividualID = bsfIndividualID_;
            assert(repOK());
        }
        
        public String getRowPrefix() { return rowPrefix; }
        public int getSubFunctionID() { return subFunctionID; }
        public String getSubFunction() { return subFunction; }
        @Override public int getRun() { return run; }
        @Override public int getStep() { return step; }
        public double getMean() { return mean; }
        public double getStd() { return std; }
        public double getBest() { return best; }
        public double getWorst() { return worst; }
        public double getBestSoFar() { return bestSoFar; }
        public long getBestSoFarID() { return bsfIndividualID; }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public String toString() {
            return String.format("%s%d, %s, %d, %d, %d, %f, %f, %s, %s, %s, %d",
                    rowPrefix, subFunctionID, subFunction, run, step, subpopulation, mean, std, best, worst, bestSoFar, bsfIndividualID);
        
        }

        @Override
        public final boolean repOK() {
            return rowPrefix != null
                    && subFunctionID >= -1
                    && subFunction != null
                    && !subFunction.isEmpty()
                    && run >= 0
                    && step >= 0
                    && subpopulation >= 0
                    && bsfIndividualID >= 0;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof VectorFitnessStatisticsMeasurement))
                return false;
            final VectorFitnessStatisticsMeasurement ref = (VectorFitnessStatisticsMeasurement)o;
            return subFunctionID == ref.subFunctionID
                    && subFunction.equals(ref.subFunction)
                    && rowPrefix.equals(ref.rowPrefix)
                    && run == ref.run
                    && step == ref.step
                    && subpopulation == ref.subpopulation
                    && Misc.doubleEquals(mean, ref.mean)
                    && Misc.doubleEquals(std, ref.std)
                    && Misc.doubleEquals(best, ref.best)
                    && Misc.doubleEquals(worst, ref.worst)
                    && Misc.doubleEquals(bestSoFar, ref.bestSoFar)
                    && bsfIndividualID == ref.bsfIndividualID;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.rowPrefix);
            hash = 23 * hash + this.subFunctionID;
            hash = 23 * hash + Objects.hashCode(this.subFunction);
            hash = 23 * hash + this.run;
            hash = 23 * hash + this.step;
            hash = 23 * hash + this.subpopulation;
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.mean) ^ (Double.doubleToLongBits(this.mean) >>> 32));
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.best) ^ (Double.doubleToLongBits(this.best) >>> 32));
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.worst) ^ (Double.doubleToLongBits(this.worst) >>> 32));
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.bestSoFar) ^ (Double.doubleToLongBits(this.bestSoFar) >>> 32));
            hash = 23 * hash + (int) (this.bsfIndividualID ^ (this.bsfIndividualID >>> 32));
            return hash;
        }
        // </editor-fold>
    }
}
