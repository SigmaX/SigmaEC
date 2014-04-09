package SigmaEC.measure;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;
import SigmaEC.util.math.Statistics;
import java.io.IOException;
import java.util.List;

/**
 * Measures the mean, standard deviation, maximum and minimum, and best-so-far
 * fitness of a population according to some ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual, P extends Phenotype> implements PopulationMetric<T>
{
    final private ObjectiveFunction<P> objective;
    final private Decoder<T, P> decoder;
    private double bestSoFar = Double.NEGATIVE_INFINITY;
    
    public FitnessStatisticsPopulationMetric(final ObjectiveFunction<P> objective, final Decoder<T, P> decoder)
    {
        if (objective == null)
            throw new IllegalArgumentException("FitnessStatisticsPopulationMetric: objective was null.");
        this.objective = objective;
        this.decoder = decoder;
        assert(repOK());
    }
    
    /** Prints a row of the form "run, generation, mean, std, max, min, bsf". */
    @Override
    public FitnessStatisticsMeasurement measurePopulation(int run, int generation, List<T> population) throws IOException
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
    public void flush() throws IOException { }

    @Override
    public void close() throws IOException { }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return objective != null
                && decoder != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[FitnessStatisticsPopulationMetric: Objective=%s, Decoder=%s]", objective, decoder);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        FitnessStatisticsPopulationMetric cRef = (FitnessStatisticsPopulationMetric) o;
        return objective.equals(cRef.objective)
                && decoder.equals(cRef.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 29 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
