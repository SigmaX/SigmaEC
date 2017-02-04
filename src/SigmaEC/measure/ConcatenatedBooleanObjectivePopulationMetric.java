package SigmaEC.measure;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.ConcatenatedBooleanFunction;
import SigmaEC.evaluate.objective.function.ConcatenatedTruthTableObjective;
import SigmaEC.evaluate.objective.function.TruthTableObjective;
import SigmaEC.measure.FitnessStatisticsPopulationMetric.FitnessStatisticsMeasurement;
import SigmaEC.meta.Population;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A special metric that takes a ConcatenatedBooleanFunction and measures each
 * individual's performance on each subFunction that makes up the overall objective.
 * 
 * @author Eric O. Scott
 */
public class ConcatenatedBooleanObjectivePopulationMetric<T extends Individual> extends PopulationMetric<T> {
    final public static String P_ROW_PREFIX = "rowPrefix";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_DECODER = "decoder";
    public final static String P_TARGET_FUNCTION = "targetFunction";
    public final static String P_PARTIAL_MATCHES = "partialMatches";
    
    private final FitnessComparator<T> fitnessComparator;
    private final Decoder<T, BooleanFunction> decoder;
    private final ConcatenatedBooleanFunction targetFunction;
    private final boolean partialMatches;
    private final String rowPrefix;
    
    private final FitnessStatisticsPopulationMetric<T, BooleanFunction> targetFunctionMetric;
    private final List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> subFunctionMetrics;
        
    public ConcatenatedBooleanObjectivePopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), ConcatenatedBooleanFunction.class);
        partialMatches = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PARTIAL_MATCHES), true);
        targetFunctionMetric = new FitnessStatisticsPopulationMetric(fitnessComparator, new Option<>(new EvaluationOperator(decoder, new TruthTableObjective(targetFunction), 1, true)));
        subFunctionMetrics = setupSubfunctionMetrics();
        final Option<String> rowPrefixOpt = parameters.getOptionalStringParameter(Parameters.push(base, P_ROW_PREFIX));
        rowPrefix = rowPrefixOpt.isDefined() ? rowPrefixOpt.get() + ", " : "";
        assert(repOK());
    }
    
    private List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> setupSubfunctionMetrics() {
        final List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> metrics = new ArrayList<>();
        for (int i = 0; i < targetFunction.getNumFunctions(); i++) {
            // Create an objective function that specifically evaluates only the ith subFunction
            final ConcatenatedTruthTableObjective ttObjective = new ConcatenatedTruthTableObjective(targetFunction, i, partialMatches);
            final EvaluationOperator<T, BooleanFunction> evaluator = new EvaluationOperator(decoder, ttObjective, 1, true);
            metrics.add(new FitnessStatisticsPopulationMetric(fitnessComparator, new Option<>(evaluator)));
        }
        return metrics;
    }

    @Override
    public MultipleMeasurement<MultiFunctionFitnessStatisticsMeasurement> measurePopulation(final int run, final int step, final Population<T> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        ping(step, population);
        final List<MultiFunctionFitnessStatisticsMeasurement> measurements = new ArrayList<>();
        for (final FitnessStatisticsMeasurement f : targetFunctionMetric.measurePopulation(run, step, population).getMeasurements())
            measurements.add(new MultiFunctionFitnessStatisticsMeasurement(rowPrefix, -1, "All", f));
        for (int i = 0; i < subFunctionMetrics.size(); i++) {
            final FitnessStatisticsPopulationMetric<T, BooleanFunction> metric = subFunctionMetrics.get(i);
            for (final FitnessStatisticsMeasurement f : metric.measurePopulation(run, step, population).getMeasurements())
                measurements.add(new MultiFunctionFitnessStatisticsMeasurement(rowPrefix, i, targetFunction.getFunction(i).getClass().getSimpleName(), f));
        }
        return new MultipleMeasurement<>(measurements);
    }
    
    @Override
    public String csvHeader() {
        final String extraColumn = (rowPrefix.isEmpty()) ? "" : "experiment, ";
        return String.format("%ssubFunctionID, subFunction, %s", extraColumn, subFunctionMetrics.get(0).csvHeader());
    }

    @Override
    public void ping(final int step, final Population<T> population) {
        targetFunctionMetric.ping(step, population);
        for (final PopulationMetric metric : subFunctionMetrics)
            metric.ping(step, population);
    }

    @Override
    public void reset() {
        targetFunctionMetric.reset();
        for (final PopulationMetric metric : subFunctionMetrics)
            metric.reset();
    }

    @Override
    public void flush() {
        targetFunctionMetric.flush();
        for (final PopulationMetric metric : subFunctionMetrics)
            metric.flush();
    }

    @Override
    public void close() {
        targetFunctionMetric.close();
        for (final PopulationMetric metric : subFunctionMetrics)
            metric.close();
    }
    
    public static class MultiFunctionFitnessStatisticsMeasurement extends Measurement {
        private final String rowPrefix;
        private final int subFunctionID;
        private final String subFunction;
        private final FitnessStatisticsMeasurement fitnessStatistics;
        
        public MultiFunctionFitnessStatisticsMeasurement(final String rowPrefix, final int subFunctionID, final String subFunction, final FitnessStatisticsMeasurement fitnessStatistics) {
            assert(rowPrefix != null);
            assert(subFunctionID >= -1);
            assert(subFunction != null);
            assert(!subFunction.isEmpty());
            assert(fitnessStatistics != null);
            this.rowPrefix = rowPrefix;
            this.subFunctionID = subFunctionID;
            this.subFunction = subFunction;
            this.fitnessStatistics = fitnessStatistics;
            assert(repOK());
        }
        
        @Override
        public int getRun() {
            return fitnessStatistics.getRun();
        }

        @Override
        public int getStep() {
            return fitnessStatistics.getStep();
        }
        
        public String getRowPrefix() {
            return rowPrefix;
        }
        
        public int getSubFunctionID() {
            return subFunctionID;
        }
        
        public String getSubFunction() {
            return subFunction;
        }
        
        public FitnessStatisticsMeasurement getFitnessStatistics() {
            return fitnessStatistics;
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public String toString() {
            return String.format("%s%d, %s, %s", rowPrefix, subFunctionID, subFunction, fitnessStatistics);
        
        }

        @Override
        public final boolean repOK() {
            return rowPrefix != null
                    && subFunctionID >= -1
                    && subFunction != null
                    && !subFunction.isEmpty()
                    && fitnessStatistics != null;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof MultiFunctionFitnessStatisticsMeasurement))
                return false;
            final MultiFunctionFitnessStatisticsMeasurement ref = (MultiFunctionFitnessStatisticsMeasurement)o;
            return subFunctionID == ref.subFunctionID
                    && subFunction.equals(ref.subFunction)
                    && rowPrefix.equals(ref.rowPrefix)
                    && fitnessStatistics.equals(ref.fitnessStatistics);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + Objects.hashCode(this.rowPrefix);
            hash = 89 * hash + this.subFunctionID;
            hash = 89 * hash + Objects.hashCode(this.subFunction);
            hash = 89 * hash + Objects.hashCode(this.fitnessStatistics);
            return hash;
        }
        // </editor-fold>
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_TARGET_FUNCTION != null
                && !P_TARGET_FUNCTION.isEmpty()
                && P_PARTIAL_MATCHES != null
                && !P_PARTIAL_MATCHES.isEmpty()
                && fitnessComparator != null
                && decoder != null
                && targetFunction != null
                && subFunctionMetrics != null
                && subFunctionMetrics.size() == targetFunction.getNumFunctions()
                && !Misc.containsNulls(subFunctionMetrics);
    }
    

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConcatenatedBooleanObjectivePopulationMetric))
            return false;
        final ConcatenatedBooleanObjectivePopulationMetric ref = (ConcatenatedBooleanObjectivePopulationMetric)o;
        return partialMatches == ref.partialMatches
                && fitnessComparator.equals(ref.fitnessComparator)
                && targetFunction.equals(ref.targetFunction)
                && decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 67 * hash + Objects.hashCode(this.decoder);
        hash = 67 * hash + Objects.hashCode(this.targetFunction);
        hash = 67 * hash + (this.partialMatches ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_PARTIAL_MATCHES, partialMatches,
                P_COMPARATOR, fitnessComparator,
                P_TARGET_FUNCTION, targetFunction,
                P_DECODER, decoder);
    }
    // </editor-fold>
}
