package SigmaEC.measure;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Measures the mean, standard deviation, maximum and minimum, and best-so-far
 * fitness of a population according to some ObjectiveFunction.
 * 
 * Since it keeps track of best-so-far information, this class is not immutable.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual<F>, P, F extends Fitness> extends PopulationMetric<T, F> {
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_EVALUATOR = "auxiliaryEvaluator";
    
    private final FitnessComparator<T, F> fitnessComparator;
    private final Option<EvaluationOperator<T, P, F>> auxiliaryEvaluator;
    private final List<T> bestSoFar = new ArrayList<>();
    private int lastBSFUpdate = -1;
    
    public FitnessStatisticsPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        auxiliaryEvaluator = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationOperator.class);
        assert(repOK());
    }
    
    public FitnessStatisticsPopulationMetric(final FitnessComparator<T, F> fitnessComparator, final Option<EvaluationOperator<T, P, F>> auxiliaryEvaluator) {
        assert(fitnessComparator != null);
        assert(auxiliaryEvaluator != null);
        this.fitnessComparator = fitnessComparator;
        this.auxiliaryEvaluator = auxiliaryEvaluator;
        assert(repOK());
    }
    

    @Override
    public void ping(final int step, final Population<T, F> population) {
        if (step - lastBSFUpdate > 1)
            throw new IllegalStateException(String.format("%s: the ping() method was called after an interval of %d steps.  It must be called every step in order to maintain a valid record of the best-so-far individual.", this.getClass().getSimpleName(), step - lastBSFUpdate));
        if (step == lastBSFUpdate)
            return;
        // Update best so far memory
        final List<T> bestsOfStep = getBestsOfStep(population);
        if (step == 0 && lastBSFUpdate == -1) {
            assert(bestSoFar.isEmpty());
            bestSoFar.addAll(bestsOfStep);
        }
        else {
            assert(!bestSoFar.isEmpty());
            final List<T> newBests = updateBestSoFars(bestsOfStep, bestSoFar);
            bestSoFar.clear();
            bestSoFar.addAll(newBests);
        }
        lastBSFUpdate = step;
    }
    
    /** Prints a row of the form "run, step, mean, std, best, worst, bsf" for each subpopulation. */
    @Override
    public synchronized MultipleMeasurement<FitnessStatisticsMeasurement> measurePopulation(final int run, final int step, final Population<T, F> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        ping(step, population);
        final List<FitnessStatisticsMeasurement> measurements = new ArrayList<FitnessStatisticsMeasurement>() {{
            for (int pop = 0; pop < population.numSuppopulations(); pop++) {
                add(measureSubpopulation(run, step, pop, population));
            }  
        }};
        assert(repOK());
        return new MultipleMeasurement<>(measurements);
    }
    
    private FitnessStatisticsMeasurement measureSubpopulation(final int run, final int step, final int subpop, final Population<T, F> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(subpop >= 0);
        assert(population != null);
        assert(subpop < population.numSuppopulations());

        // If we have an auxiliary evaluator, use it to compute the fitness statistics
        final List<T> evaluatedSubpop = auxiliaryEvaluator.isDefined() ?
                auxiliaryEvaluator.get().operate(0, 0, population.getSubpopulation(subpop)) :
                population.getSubpopulation(subpop);
        final double[] fitnesses = new double[evaluatedSubpop.size()];
        for (int i = 0; i < fitnesses.length; i++)
            fitnesses[i] = evaluatedSubpop.get(i).getFitness().asScalar();

        final double mean = Statistics.mean(fitnesses);
        final double std = Statistics.std(fitnesses, mean);

        final T best = Statistics.best(evaluatedSubpop, fitnessComparator);
        final T worst = Statistics.worst(evaluatedSubpop, fitnessComparator);

        // Record the auxilliary fitness of the true best individual
        final F bestFitness = auxiliaryEvaluator.isDefined() ? 
                auxiliaryEvaluator.get().evaluate(bestSoFar.get(subpop)).getFitness()
                : bestSoFar.get(subpop).getFitness();
        // But record the ID of the original best individual
        final long bestID = bestSoFar.get(subpop).getID();
        return new FitnessStatisticsMeasurement(run, step, subpop, mean, std, best.getFitness(), worst.getFitness(), bestFitness, bestID);
    }
    
    
    private List<T> getBestsOfStep(final Population<T, F> population) {
        return new ArrayList<T>() {{
           for (int i = 0; i < population.numSuppopulations(); i++)
               add(population.getBest(i, fitnessComparator));
        }};
    }
    
    private List<T> updateBestSoFars(final List<T> bestOfStep, final List<T> previousBestSoFars) {
        assert(bestOfStep != null);
        assert(!Misc.containsNulls(bestOfStep));
        assert(previousBestSoFars != null);
        assert(!Misc.containsNulls(previousBestSoFars));
        assert(bestOfStep.size() == previousBestSoFars.size());
        return new ArrayList<T>() {{
            for (int i = 0; i < bestOfStep.size(); i++) {
                if (fitnessComparator.betterThan(bestOfStep.get(i), previousBestSoFars.get(i)))
                    add(bestOfStep.get(i));
                else
                    add(previousBestSoFars.get(i));
            }
        }};
    }

    @Override
    public String csvHeader() {
        return "run, step, subpopulation, mean, std, best, worst, bsf, bsf_individualID";
    }

    @Override
    public void reset() {
        bestSoFar.clear();
        lastBSFUpdate = -1;
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
                && fitnessComparator != null
                && auxiliaryEvaluator != null
                && bestSoFar != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, bestSoFar=%s]", this.getClass().getSimpleName(),
                P_COMPARATOR, fitnessComparator,
                P_EVALUATOR, auxiliaryEvaluator,
                bestSoFar);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FitnessStatisticsPopulationMetric))
            return false;
        final FitnessStatisticsPopulationMetric ref = (FitnessStatisticsPopulationMetric) o;
        return fitnessComparator.equals(ref.fitnessComparator)
                && bestSoFar.equals(ref.bestSoFar)
                && auxiliaryEvaluator.equals(ref.auxiliaryEvaluator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 53 * hash + Objects.hashCode(this.auxiliaryEvaluator);
        hash = 53 * hash + Objects.hashCode(this.bestSoFar);
        return hash;
    }
    //</editor-fold>
    
    public static class FitnessStatisticsMeasurement<F extends Fitness> extends Measurement {
        private final int run;
        private final int step;
        private final int subpopulation;
        private final double mean;
        private final double std;
        private final F best;
        private final F worst;
        private final F bestSoFar;
        private final long bsfIndividualID;
        
        public FitnessStatisticsMeasurement(final int run_, final int step_, final int subpopulation_, final double mean_, final double std_, final F best_, final F worst_, final F bsf_, final long bsfIndividualID_) {
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

        @Override public int getRun() { return run; }
        @Override public int getStep() { return step; }
        public double getMean() { return mean; }
        public double getStd() { return std; }
        public F getBest() { return best; }
        public F getWorst() { return worst; }
        public F getBestSoFar() { return bestSoFar; }
        public long getBestSoFarID() { return bsfIndividualID; }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public String toString() {
            return String.format("%d, %d, %d, %f, %f, %s, %s, %s, %d",
                    run, step, subpopulation, mean, std, best, worst, bestSoFar, bsfIndividualID);
        }

        @Override
        public final boolean repOK() {
            return run >= 0
                    && step >= 0
                    && subpopulation >= 0
                    && bsfIndividualID >= 0;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof FitnessStatisticsPopulationMetric.FitnessStatisticsMeasurement))
                return false;
            final FitnessStatisticsMeasurement ref = (FitnessStatisticsMeasurement)o;
            return run == ref.run
                    && step == ref.step
                    && subpopulation == ref.subpopulation
                    && Misc.doubleEquals(mean, ref.mean)
                    && Misc.doubleEquals(std, ref.std)
                    && best.equals(ref.best)
                    && worst.equals(ref.worst)
                    && bestSoFar.equals(ref.bestSoFar)
                    && bsfIndividualID == ref.bsfIndividualID;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + this.run;
            hash = 97 * hash + this.step;
            hash = 97 * hash + this.subpopulation;
            hash = 97 * hash + (int) (Double.doubleToLongBits(this.mean) ^ (Double.doubleToLongBits(this.mean) >>> 32));
            hash = 97 * hash + (int) (Double.doubleToLongBits(this.std) ^ (Double.doubleToLongBits(this.std) >>> 32));
            hash = 97 * hash + Objects.hashCode(this.best);
            hash = 97 * hash + Objects.hashCode(this.worst);
            hash = 97 * hash + Objects.hashCode(this.bestSoFar);
            hash = 97 * hash + (int) (this.bsfIndividualID ^ (this.bsfIndividualID >>> 32));
            return hash;
        }
        // </editor-fold>
    }
}
