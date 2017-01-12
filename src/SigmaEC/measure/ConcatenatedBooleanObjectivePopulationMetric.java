package SigmaEC.measure;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.ConcatenatedBooleanFunction;
import SigmaEC.evaluate.objective.function.ConcatenatedTruthTableObjective;
import SigmaEC.evaluate.objective.function.TruthTableObjective;
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
    public final static String P_MODULO = "modulo";
    public final static String P_DECODER = "decoder";
    public final static String P_TARGET_FUNCTION = "targetFunction";
    public final static String P_PARTIAL_MATCHES = "partialMatches";
    
    private final FitnessComparator<T> fitnessComparator;
    private final int modulo;
    private final Decoder<T, BooleanFunction> decoder;
    private final ConcatenatedBooleanFunction targetFunction;
    private final boolean partialMatches;
    private final String rowPrefix;
    
    private FitnessStatisticsPopulationMetric<T, BooleanFunction> targetFunctionMetric;
    private List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> subFunctionMetrics;
        
    public ConcatenatedBooleanObjectivePopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), ConcatenatedBooleanFunction.class);
        modulo = parameters.getOptionalIntParameter(Parameters.push(base, P_MODULO), 1);
        partialMatches = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PARTIAL_MATCHES), true);
        if (modulo <= 0)
            throw new IllegalStateException(String.format("%s: %s is %d, must be positive.", this.getClass().getSimpleName(), P_MODULO, modulo));
        targetFunctionMetric = new FitnessStatisticsPopulationMetric(fitnessComparator, new Option<>(new EvaluationOperator(decoder, new TruthTableObjective(targetFunction), 1, true)), modulo);
        subFunctionMetrics = setupSubfunctionMetrics();
        final Option<String> rowPrefixOpt = parameters.getOptionalStringParameter(Parameters.push(base, P_ROW_PREFIX));
        rowPrefix = rowPrefixOpt.isDefined() ? rowPrefixOpt.get() + ", " : "";
        assert(repOK());
    }
    
    private final List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> setupSubfunctionMetrics() {
        final List<FitnessStatisticsPopulationMetric<T, BooleanFunction>> metrics = new ArrayList<>();
        for (int i = 0; i < targetFunction.getNumFunctions(); i++) {
            // Create an objective function that specifically evaluates only the ith subFunction
            final ConcatenatedTruthTableObjective ttObjective = new ConcatenatedTruthTableObjective(targetFunction, i, partialMatches);
            final EvaluationOperator<T, BooleanFunction> evaluator = new EvaluationOperator(decoder, ttObjective, 1, true);
            metrics.add(new FitnessStatisticsPopulationMetric(fitnessComparator, new Option<>(evaluator), modulo));
        }
        return metrics;
    }

    @Override
    public Measurement measurePopulation(final int run, final int step, final Population<T> population) {
        final List<String> functionStrings = new ArrayList<>();
        functionStrings.add(String.format("%s%d, %s, %s", rowPrefix, -1, "All", targetFunctionMetric.measurePopulation(run, step, population)));
        // Prepend the subFunction ID to each measurement
        for (int i = 0; i < subFunctionMetrics.size(); i++) {
            final PopulationMetric metric = subFunctionMetrics.get(i);
            final String arityPostfix = (targetFunction.getFunction(i).arity() == 2) ? "" : "." + String.valueOf(targetFunction.getFunction(i).arity());
            functionStrings.add(String.format("%s%d, %s%s, %s", rowPrefix, i, targetFunction.getFunction(i).getClass().getSimpleName(), arityPostfix, metric.measurePopulation(run, step, population)));
        }
        return new MultipleStringMeasurement(run, step, functionStrings);
    }

    @Override
    public String csvHeader() {
        final String extraColumn = (rowPrefix.isEmpty()) ? "" : "experiment, ";
        return String.format("%ssubFunctionID, subFunction, %s", extraColumn, subFunctionMetrics.get(0).csvHeader());
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

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_MODULO != null
                && !P_MODULO.isEmpty()
                && P_TARGET_FUNCTION != null
                && !P_TARGET_FUNCTION.isEmpty()
                && P_PARTIAL_MATCHES != null
                && !P_PARTIAL_MATCHES.isEmpty()
                && fitnessComparator != null
                && modulo > 0
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
        return modulo == ref.modulo
                && partialMatches == ref.partialMatches
                && fitnessComparator.equals(ref.fitnessComparator)
                && targetFunction.equals(ref.targetFunction)
                && decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 67 * hash + this.modulo;
        hash = 67 * hash + Objects.hashCode(this.decoder);
        hash = 67 * hash + Objects.hashCode(this.targetFunction);
        hash = 67 * hash + (this.partialMatches ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%B, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MODULO, modulo,
                P_PARTIAL_MATCHES, partialMatches,
                P_COMPARATOR, fitnessComparator,
                P_TARGET_FUNCTION, targetFunction,
                P_DECODER, decoder);
    }
    // </editor-fold>
}
