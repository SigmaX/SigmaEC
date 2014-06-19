package SigmaEC.measure;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.List;

/**
 * Measures the mean, standard deviation, maximum and minimum, and best-so-far
 * fitness of a population according to some ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual, P> extends PopulationMetric<T>
{
    final private static String P_OBJECTIVE = "objective";
    final private static String P_DECODER = "decoder";
    
    final private ObjectiveFunction<P> objective;
    final private Decoder<T, P> decoder;
    private double bestSoFar = Double.NEGATIVE_INFINITY;
    
    public FitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        this.decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        assert(repOK());
    }
    
    /** Prints a row of the form "run, generation, mean, std, max, min, bsf". */
    @Override
    public FitnessStatisticsMeasurement measurePopulation(int run, int generation, List<T> population)
    {
        final double[] fitnesses = new double[population.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = objective.fitness(decoder.decode(population.get(i)));
        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);
        final double max = Statistics.max(fitnesses);
        final double min = Statistics.min(fitnesses);
        if (max > bestSoFar)
            bestSoFar = max;
        return new FitnessStatisticsMeasurement(run, generation, new double[] { mean, std, max, min, bestSoFar });
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return objective != null
                && decoder != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: objective=%s, decoder=%s, bestSoFar=%f]", this.getClass().getSimpleName(), objective, decoder, bestSoFar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        final FitnessStatisticsPopulationMetric cRef = (FitnessStatisticsPopulationMetric) o;
        return objective.equals(cRef.objective)
                && decoder.equals(cRef.decoder)
                && Misc.doubleEquals(bestSoFar, cRef.bestSoFar);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 79 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.bestSoFar) ^ (Double.doubleToLongBits(this.bestSoFar) >>> 32));
        return hash;
    }
    //</editor-fold>
}
