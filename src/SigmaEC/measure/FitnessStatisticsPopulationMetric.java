package SigmaEC.measure;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;

/**
 * Measures the mean, standard deviation, maximum and minimum, and best-so-far
 * fitness of a population according to some ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual, P> extends PopulationMetric<T> {
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_EVALUATOR = "auxiliaryEvaluator";
    public final static String P_MODULO = "modulo";
    
    private final FitnessComparator<T> fitnessComparator;
    private final Option<EvaluationOperator<T,P>> auxiliaryEvaluator;
    private final int modulo;
    private T bestSoFar = null;
    private T auxiliaryBestSoFar = null;
    
    public FitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        auxiliaryEvaluator = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationOperator.class);
        final Option<Integer> moduloOpt = parameters.getOptionalIntParameter(Parameters.push(base, P_MODULO));
        modulo = moduloOpt.isDefined() ? moduloOpt.get() : 1;
        if (modulo <= 0)
            throw new IllegalStateException(String.format("%s: %s is %d, must be positive.", this.getClass().getSimpleName(), P_MODULO, modulo));
        assert(repOK());
    }
    
    /** Prints a row of the form "run, generation, mean, std, best, worst, bsf" for each subpopulation. */
    @Override
    public MultipleMeasurement measurePopulation(final int run, final int step, final Population<T> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        
        final List<FitnessStatisticsMeasurement> measurements = new ArrayList<FitnessStatisticsMeasurement>() {{
            for (int pop = 0; pop < population.numSuppopulations(); pop++) {
                add(measureSubpopulation(run, step, pop, population));
            }  
        }};
        
        assert(repOK());
        return new MultipleMeasurement(measurements);
    }
    
    private FitnessStatisticsMeasurement measureSubpopulation(final int run, final int step, final int subpop, final Population<T> population) {
        // Choose the best-so-far individual *without* consulting the auxillary evaluator
        
        final T trueBest = population.getBest(subpop, fitnessComparator);
        if (fitnessComparator.betterThan(trueBest, bestSoFar)) {
            bestSoFar = trueBest;
            // Evaluate the auxillary fitness of bestSoFar
            if (auxiliaryEvaluator.isDefined())
                auxiliaryBestSoFar = auxiliaryEvaluator.get().operate(0, 0, new ArrayList<T>() {{ add(bestSoFar); }}).get(0);
        }

        // Don't measure anything else until the sepcified interval has elapsed
        if (step % modulo != 0)
            return null;

        // If we have an auxiliary evaluator, use it to compute the fitness statistics
        final List<T> evaluatedSubpop = auxiliaryEvaluator.isDefined() ?
                auxiliaryEvaluator.get().operate(0, 0, population.getSubpopulation(subpop)) :
                population.getSubpopulation(subpop);
        final double[] fitnesses = new double[evaluatedSubpop.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = evaluatedSubpop.get(i).getFitness();

        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);

        final T best = Statistics.best(evaluatedSubpop, fitnessComparator);
        final T worst = Statistics.worst(evaluatedSubpop, fitnessComparator);

        final double bsf = auxiliaryEvaluator.isDefined() ? auxiliaryBestSoFar.getFitness() : bestSoFar.getFitness();
        return new FitnessStatisticsMeasurement(run, step, subpop, mean, std, best.getFitness(), worst.getFitness(), bsf, bestSoFar.getID());
    }

    @Override
    public void reset() {
        bestSoFar = null;
        auxiliaryBestSoFar = null;
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
                && auxiliaryEvaluator != null
                && modulo > 0
                && !(bestSoFar != null && auxiliaryEvaluator.isDefined() && auxiliaryBestSoFar == null);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%d, bestSoFar=%s, auxillaryBestSoFar=%s]", this.getClass().getSimpleName(),
                P_COMPARATOR, fitnessComparator,
                P_EVALUATOR, auxiliaryEvaluator,
                P_MODULO, modulo,
                bestSoFar, auxiliaryBestSoFar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        final FitnessStatisticsPopulationMetric ref = (FitnessStatisticsPopulationMetric) o;
        return modulo == ref.modulo
                && fitnessComparator.equals(ref.fitnessComparator)
                && (bestSoFar == null ? ref.bestSoFar == null : bestSoFar.equals(ref.bestSoFar))
                && (auxiliaryBestSoFar == null ? ref.auxiliaryBestSoFar == null : auxiliaryBestSoFar.equals(ref.auxiliaryBestSoFar))
                && auxiliaryEvaluator.equals(ref.auxiliaryEvaluator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 67 * hash + (this.auxiliaryEvaluator != null ? this.auxiliaryEvaluator.hashCode() : 0);
        hash = 67 * hash + this.modulo;
        hash = 67 * hash + (this.bestSoFar != null ? this.bestSoFar.hashCode() : 0);
        hash = 67 * hash + (this.auxiliaryBestSoFar != null ? this.auxiliaryBestSoFar.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
