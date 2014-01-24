package SigmaEC.measure;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.util.math.Statistics;
import java.io.IOException;
import java.util.List;

/**
 * Measures the mean, standard deviation, maximum and minimum fitness of a
 * population according to some ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual> implements PopulationMetric<T>
{
    final private ObjectiveFunction<T> objective;
    
    public FitnessStatisticsPopulationMetric(ObjectiveFunction<T> objective)
    {
        if (objective == null)
            throw new IllegalArgumentException("FitnessStatisticsPopulationMetric: objective was null.");
        this.objective = objective;
        assert(repOK());
    }
    
    /** Prints a row of the form "generation, mean, std, max, min". */
    @Override
    public String measurePopulation(int run, int generation, List<T> population) throws IOException
    {
        double[] fitnesses = new double[population.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = objective.fitness(population.get(i));
        double mean = Statistics.mean(fitnesses);
        return String.format("%d, %d, %f, %f, %f, %f\n", run, generation, mean, Statistics.std(fitnesses, mean), Statistics.max(fitnesses), Statistics.min(fitnesses));
    }

    @Override
    public void flush() throws IOException { }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return objective != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[FitnessStatisticsPopulationMetric: Objective=%s", objective);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        FitnessStatisticsPopulationMetric cRef = (FitnessStatisticsPopulationMetric) o;
        return objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
