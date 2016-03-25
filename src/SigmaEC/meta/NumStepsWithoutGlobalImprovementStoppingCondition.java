package SigmaEC.meta;

import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class NumStepsWithoutGlobalImprovementStoppingCondition<T extends Individual> extends StoppingCondition<T> {
    public final static String P_NUM_STEPS_WITHOUT_IMPROVEMENT = "numStepsWithoutImprovement";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    
    private final int numStepsAllowedWithoutImprovement;
    private final FitnessComparator<T> fitnessComparator;
    
    private T bestSoFar;
    private int stepsPassedSinceLastImprovement;
    
    public NumStepsWithoutGlobalImprovementStoppingCondition(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numStepsAllowedWithoutImprovement = parameters.getIntParameter(Parameters.push(base, P_NUM_STEPS_WITHOUT_IMPROVEMENT));
        if (numStepsAllowedWithoutImprovement < 0)
            throw new IllegalStateException(String.format("%s: %s is negative, must be >= 0.", this.getClass().getSimpleName(), P_NUM_STEPS_WITHOUT_IMPROVEMENT));
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        reset();
        assert(repOK());
    }
    
    @Override
    public final void reset() {
        bestSoFar = null;
        stepsPassedSinceLastImprovement = 0;
    }
    
    @Override
    public boolean stop(final Population<T> population, int step) {
        assert(step >= 0);
        final T bestOfGen = population.getBest(fitnessComparator);
        return stopInd(bestOfGen, step);
    }

    @Override
    public boolean stopInd(T individual, int step) {
        if (fitnessComparator.betterThan(individual, bestSoFar)) {
            // Only reset step count if the new BSF is *strictly* better than the old one
            if (fitnessComparator.compare(individual, bestSoFar) > 0) {
                stepsPassedSinceLastImprovement = 0;
            }
            else
                stepsPassedSinceLastImprovement++;
            bestSoFar = individual;
        }
        else
            stepsPassedSinceLastImprovement++;
        assert(repOK());
        return (stepsPassedSinceLastImprovement >= numStepsAllowedWithoutImprovement);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_FITNESS_COMPARATOR != null
                && !P_FITNESS_COMPARATOR.isEmpty()
                && P_NUM_STEPS_WITHOUT_IMPROVEMENT != null
                && !P_NUM_STEPS_WITHOUT_IMPROVEMENT.isEmpty()
                && numStepsAllowedWithoutImprovement >= 0
                && fitnessComparator != null
                && stepsPassedSinceLastImprovement >= 0
                && stepsPassedSinceLastImprovement <= numStepsAllowedWithoutImprovement + 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof NumStepsWithoutGlobalImprovementStoppingCondition))
            return false;
        final NumStepsWithoutGlobalImprovementStoppingCondition ref = (NumStepsWithoutGlobalImprovementStoppingCondition)o;
        return numStepsAllowedWithoutImprovement == ref.numStepsAllowedWithoutImprovement
                && fitnessComparator.equals(ref.fitnessComparator)
                && bestSoFar.equals(ref.bestSoFar)
                && stepsPassedSinceLastImprovement == ref.stepsPassedSinceLastImprovement;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.numStepsAllowedWithoutImprovement;
        hash = 97 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 97 * hash + (this.bestSoFar != null ? this.bestSoFar.hashCode() : 0);
        hash = 97 * hash + this.stepsPassedSinceLastImprovement;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, stepsPassedWithoutImprovement=%d]", this.getClass().getSimpleName(),
                P_NUM_STEPS_WITHOUT_IMPROVEMENT, numStepsAllowedWithoutImprovement,
                P_FITNESS_COMPARATOR, fitnessComparator,
                stepsPassedSinceLastImprovement);
    }
    // </editor-fold>
}
