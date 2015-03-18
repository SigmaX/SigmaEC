package SigmaEC.measure;

import SigmaEC.evaluate.EvaluationGenerator;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Option;
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
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_EVALUATOR = "auxillaryEvaluator";
    public final static String P_MODULO = "modulo";
    
    private final FitnessComparator<T> fitnessComparator;
    private final Option<EvaluationGenerator<T,P>> auxillaryEvaluator;
    private final int modulo;
    private T bestSoFar = null;
    private long bestSoFarID = -1;
    
    public FitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        auxillaryEvaluator = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationGenerator.class);
        final Option<Integer> moduloOpt = parameters.getOptionalIntParameter(Parameters.push(base, P_MODULO));
        modulo = moduloOpt.isDefined() ? moduloOpt.get() : 1;
        if (modulo <= 0)
            throw new IllegalStateException(String.format("%s: %s is %d, must be positive.", this.getClass().getSimpleName(), P_MODULO, modulo));
        assert(repOK());
    }
    
    /** Prints a row of the form "run, generation, mean, std, max, min, bsf". */
    @Override
    public FitnessStatisticsMeasurement measurePopulation(final int run, final int step, List<T> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        if (step % modulo != 0)
            return null;
        
        final double[] fitnesses = new double[population.size()];
        if (auxillaryEvaluator.isDefined())
                population = auxillaryEvaluator.get().produceGeneration(population);
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
        return new FitnessStatisticsMeasurement(run, step, mean, std, max.getFitness(), min.getFitness(), bestSoFar.getFitness(), bestSoFarID);
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
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
                && P_MODULO != null
                && !P_MODULO.isEmpty()
                && fitnessComparator != null
                && auxillaryEvaluator != null
                && modulo > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%d, bestSoFar=%s, bestSoFarID=%d]", this.getClass().getSimpleName(),
                P_COMPARATOR, fitnessComparator,
                P_EVALUATOR, auxillaryEvaluator,
                P_MODULO, modulo,
                bestSoFar, bestSoFarID);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        final FitnessStatisticsPopulationMetric ref = (FitnessStatisticsPopulationMetric) o;
        return modulo == ref.modulo
                && bestSoFarID == ref.bestSoFarID
                && fitnessComparator.equals(ref.fitnessComparator)
                && bestSoFar.equals(ref.bestSoFar)
                && auxillaryEvaluator.equals(ref.auxillaryEvaluator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 37 * hash + (this.auxillaryEvaluator != null ? this.auxillaryEvaluator.hashCode() : 0);
        hash = 37 * hash + this.modulo;
        hash = 37 * hash + (this.bestSoFar != null ? this.bestSoFar.hashCode() : 0);
        hash = 37 * hash + (int) (this.bestSoFarID ^ (this.bestSoFarID >>> 32));
        return hash;
    }
    //</editor-fold>
}
