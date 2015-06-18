package SigmaEC.select;

import SigmaEC.Generator;
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
public class RemoveWorstSelectionGenerator<T extends Individual> extends Generator<T> {
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    
    private final FitnessComparator<T> fitnessComparator;
    
    public RemoveWorstSelectionGenerator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }

    @Override
    public List<T> produceGeneration(final List<T> parentPopulation) {
        assert(parentPopulation != null);
        assert(!parentPopulation.isEmpty());
        final double worst = Statistics.worst(parentPopulation, fitnessComparator).getFitness();
        final double best = Statistics.best(parentPopulation, fitnessComparator).getFitness();
        if (Misc.doubleEquals(worst, best))
            return parentPopulation;
        // Return a population containing everything except the worst individuals
        final List<T> newPopulation = new ArrayList<T>() {{
            for (final T ind : parentPopulation)
                if (!Misc.doubleEquals(worst, ind.getFitness()))
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
        if (!(o instanceof RemoveWorstSelectionGenerator))
            return false;
        final RemoveWorstSelectionGenerator ref = (RemoveWorstSelectionGenerator)o;
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
