package SigmaEC.measure;

import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.List;

/**
 * Measures the mean, standard deviation, maximum and minimum, and best-so-far
 * fitness of a population according to some ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual, P> extends PopulationMetric<T> {
    final private static String P_COMPARATOR = "fitnessComparator";
    
    final private FitnessComparator<T> fitnessComparator;
    private T bestSoFar = null;
    private long bestSoFarID = -1;
    
    public FitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }
    
    /** Prints a row of the form "run, generation, mean, std, max, min, bsf". */
    @Override
    public FitnessStatisticsMeasurement measurePopulation(final int run, final int generation, final List<T> population) {
        final double[] fitnesses = new double[population.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = population.get(i).getFitness();
        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);
        
        final T max = Statistics.max(population, fitnessComparator);
        final T min = Statistics.min(population, fitnessComparator);
        
        if (fitnessComparator.betterThan(max, bestSoFar)) {
            bestSoFar = max;
            bestSoFarID = max.getID();
        }
        return new FitnessStatisticsMeasurement(run, generation, mean, std, max.getFitness(), min.getFitness(), bestSoFar.getFitness(), bestSoFarID);
    }

    @Override
    public void reset() {
        bestSoFar = null;
        bestSoFarID = -1;
    }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return fitnessComparator != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, bestSoFar=%s]", this.getClass().getSimpleName(),
                P_COMPARATOR, fitnessComparator, bestSoFar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        final FitnessStatisticsPopulationMetric cRef = (FitnessStatisticsPopulationMetric) o;
        return fitnessComparator.equals(cRef.fitnessComparator)
                && bestSoFar.equals(bestSoFar);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 71 * hash + (this.bestSoFar != null ? this.bestSoFar.hashCode() : 0);
        hash = 71 * hash + (int) (this.bestSoFarID ^ (this.bestSoFarID >>> 32));
        return hash;
    }
    //</editor-fold>
}
