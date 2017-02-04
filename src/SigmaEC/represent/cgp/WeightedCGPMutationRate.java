package SigmaEC.represent.cgp;

import SigmaEC.measure.ConcatenatedBooleanObjectivePopulationMetric;
import SigmaEC.measure.ConcatenatedBooleanObjectivePopulationMetric.MultiFunctionFitnessStatisticsMeasurement;
import SigmaEC.measure.MultipleMeasurement;
import SigmaEC.meta.Population;
import SigmaEC.operate.MutationRate;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Eric O. Scott
 */
public class WeightedCGPMutationRate extends MutationRate {
    public final static String P_MIN = "min";
    public final static String P_MAX = "max";
    public final static String P_C = "c";
    public final static String P_DECODER = "decoder";
    public final static String P_CGP_PARAMETERS = "cgpParameters";
    public final static String P_POPULATION_METRIC = "populationMetric";
    public final static String P_SCHEME = "weightingScheme";
    
    private final double min;
    private final double max;
    private final double c;
    private final Decoder<IntVectorIndividual, CartesianIndividual> decoder;
    private final CGPParameters cgpParameters;
    private final ConcatenatedBooleanObjectivePopulationMetric populationMetric;
    public static enum WeightingScheme { LINEAR, EXPONENTIAL };
    private final WeightingScheme weightingScheme;
    
    public WeightedCGPMutationRate(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        min = parameters.getDoubleParameter(Parameters.push(base, P_MIN));
        max = parameters.getDoubleParameter(Parameters.push(base, P_MAX));
        if (max < min)
            throw new IllegalStateException(String.format("%s: parameter '%s' is %f, but must be greater than or equal to '%s', which is %f.",
                    this.getClass().getSimpleName(), Parameters.push(base, P_MAX), max, Parameters.push(base, P_MIN), min));
        c = parameters.getDoubleParameter(Parameters.push(base, P_C));
        decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        cgpParameters = parameters.getInstanceFromParameter(Parameters.push(base, P_CGP_PARAMETERS), CGPParameters.class);
        populationMetric = parameters.getInstanceFromParameter(Parameters.push(base, P_POPULATION_METRIC), ConcatenatedBooleanObjectivePopulationMetric.class);
        weightingScheme = WeightingScheme.valueOf(parameters.getOptionalStringParameter(Parameters.push(base, P_SCHEME), WeightingScheme.LINEAR.toString()));
        assert(repOK());
    }
    
    @Override
    public double getRateForGene(final int gene, final int step, final LinearGenomeIndividual ind) {
        assert(ind != null);
        assert(gene >= 0);
        assert(gene < ind.size());
        assert(ind instanceof IntVectorIndividual);
        
        
        // Compute the fitness of the circuit on each sub-function
        final MultipleMeasurement<MultiFunctionFitnessStatisticsMeasurement> fitnesses = populationMetric.measurePopulation(0, step, new Population(new ArrayList() {{ add(ind); }}));
        
        // If this gene affects only the output source, rate is directly proportional to subFunction fitness
        if (cgpParameters.isOutputSource(gene)) {
            for (final MultiFunctionFitnessStatisticsMeasurement m : fitnesses.getMeasurements())
                if (m.getSubFunctionID() == cgpParameters.getOutputForGene(gene))
                    return min + (max - min)*m.getFitnessStatistics().getBest();
            throw new IllegalStateException();
        }
        
        // Determine which subFunctions this gene affects the solution for
        final Set<Integer> paths = getExecutionPathsForGene(gene, ind);
        if (paths.isEmpty()) {
            assert(repOK());
            return max;
        }
        
        switch (weightingScheme) {
            default:
            case LINEAR:
                double sumFitnessCompliments = 0.0;
                for (final MultiFunctionFitnessStatisticsMeasurement m : fitnesses.getMeasurements())
                    if (paths.contains(m.getSubFunctionID())) {
                        assert(m.getFitnessStatistics().getBest() >= 0);
                        assert(m.getFitnessStatistics().getBest() <= 1.0);
                        sumFitnessCompliments += 1 - m.getFitnessStatistics().getBest();
                    }
                assert(repOK());
                return min + (max - min)/paths.size()*sumFitnessCompliments;
                
            case EXPONENTIAL:
                double sumFitness = 0.0;
                for (final MultiFunctionFitnessStatisticsMeasurement m : fitnesses.getMeasurements())
                    if (paths.contains(m.getSubFunctionID())) {
                        assert(m.getFitnessStatistics().getBest() >= 0);
                        assert(m.getFitnessStatistics().getBest() <= 1.0);
                        sumFitness += m.getFitnessStatistics().getBest();
                    }
                assert(repOK());
                return min + (max - min)*Math.exp(c/paths.size()*sumFitness);
        }
    }
    
    private Set<Integer> getExecutionPathsForGene(final int gene, final LinearGenomeIndividual ind) {
        final CartesianIndividual dInd = decoder.decode((IntVectorIndividual) ind).computeExecutionPaths();
        final int layer = cgpParameters.getLayerForGene(gene);
        final int node = cgpParameters.getNodeForGene(gene);
        return dInd.getExecutionPaths(layer, node);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_MAX != null
                && !P_MAX.isEmpty()
                && P_MIN != null
                && !P_MIN.isEmpty()
                && P_C != null
                && !P_C.isEmpty()
                && P_CGP_PARAMETERS != null
                && !P_CGP_PARAMETERS.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_POPULATION_METRIC != null
                && !P_POPULATION_METRIC.isEmpty()
                && Double.isFinite(min)
                && min >= 0.0
                && min <= 1.0
                && Double.isFinite(max)
                && max >= 0.0
                && max <= 1.0
                && Double.isFinite(c)
                && decoder != null
                && cgpParameters != null
                && populationMetric != null
                && weightingScheme != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WeightedCGPMutationRate))
            return false;
        final WeightedCGPMutationRate ref = (WeightedCGPMutationRate)o;
        return Misc.doubleEquals(min, ref.min)
                && Misc.doubleEquals(max, ref.max)
                && Misc.doubleEquals(c, ref.c)
                && weightingScheme.equals(ref.weightingScheme)
                && cgpParameters.equals(ref.cgpParameters)
                && decoder.equals(ref.decoder)
                && populationMetric.equals(ref.populationMetric);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.c) ^ (Double.doubleToLongBits(this.c) >>> 32));
        hash = 67 * hash + Objects.hashCode(this.decoder);
        hash = 67 * hash + Objects.hashCode(this.cgpParameters);
        hash = 67 * hash + Objects.hashCode(this.populationMetric);
        hash = 67 * hash + Objects.hashCode(this.weightingScheme);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%f, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MIN, min,
                P_MAX, max,
                P_C, c,
                P_SCHEME, weightingScheme,
                P_CGP_PARAMETERS, cgpParameters,
                P_DECODER, decoder,
                P_POPULATION_METRIC, populationMetric);
    }
    // </editor-fold>
}
