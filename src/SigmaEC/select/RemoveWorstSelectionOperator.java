package SigmaEC.select;

import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A selection operator that preserves all individuals except for the worst one,
 * and any individual with the same fitness as the worst one.
 * 
 * @author Eric O. Scott
 */
public class RemoveWorstSelectionOperator<T extends Individual<F>, F extends Fitness> extends Operator<T> {
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    
    private final FitnessComparator<T, F> fitnessComparator;
    
    public RemoveWorstSelectionOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }

    @Override
    public List<T> operate(final int run, final int generation, final List<T> parentPopulation) {
        assert(parentPopulation != null);
        assert(!parentPopulation.isEmpty());
        final F worst = Statistics.worst(parentPopulation, fitnessComparator).getFitness();
        final F best = Statistics.best(parentPopulation, fitnessComparator).getFitness();
        if (worst.equals(best))
            return parentPopulation;
        // Return a population containing everything except the worst individuals
        final List<T> newPopulation = new ArrayList<T>() {{
            for (final T ind : parentPopulation)
                if (!worst.equals(ind.getFitness()))
                    add(ind);
        }};
        assert(repOK());
        return newPopulation;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_FITNESS_COMPARATOR != null
                && !P_FITNESS_COMPARATOR.isEmpty()
                && fitnessComparator != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RemoveWorstSelectionOperator))
            return false;
        final RemoveWorstSelectionOperator ref = (RemoveWorstSelectionOperator)o;
        return fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.fitnessComparator);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_FITNESS_COMPARATOR, fitnessComparator);
    }
    // </editor-fold>
}
