package SigmaEC.meta.island;

import SigmaEC.SRandom;
import SigmaEC.experiment.SimpleExperiment;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A strategy for migration that moves one or more individuals from each island
 * to some other randomly chosen neighbor every time a fixed interval of generations
 * passes.
 * 
 * @author Eric O. Scott
 */
public class RandomMigrationPolicy<T extends Individual> extends MigrationPolicy<T> {
    public final static String P_RANDOM = "random";
    public final static String P_INTERVAL = "interval";
    public final static String P_SOURCE_SELECTOR = "sourceSelector";
    public final static String P_REPLACEMENT_SELECTOR = "replacementSelector";
    public final static String P_ALWAYS_REPLACE = "alwaysReplace";
    public final static String P_COMPARATOR = "fitnessComparator";
    
    private final SRandom random;
    private final int interval;
    private final Selector<T> sourceSelector;
    private final Selector<T> replacementSelector;
    private final boolean alwaysReplace;
    private final Option<FitnessComparator<T>> fitnessComparator;
    
    public RandomMigrationPolicy(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        interval = parameters.getIntParameter(Parameters.push(base, P_INTERVAL));
        sourceSelector = parameters.getInstanceFromParameter(Parameters.push(base, P_SOURCE_SELECTOR), Selector.class);
        replacementSelector = parameters.getInstanceFromParameter(Parameters.push(base, P_REPLACEMENT_SELECTOR), Selector.class);
        alwaysReplace = parameters.getBooleanParameter(Parameters.push(base, P_ALWAYS_REPLACE));
        fitnessComparator = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        if (!alwaysReplace && !fitnessComparator.isDefined())
            throw new IllegalStateException(String.format("%s: parameter '%s' is %B, but '%s' is not defined.  A Comparator is required because individuals will only be replaced if the incoming individual has higher fitness.", this.getClass().getSimpleName(), Parameters.push(base, P_ALWAYS_REPLACE), alwaysReplace, Parameters.push(base, P_COMPARATOR)));
        if (alwaysReplace && fitnessComparator.isDefined())
            Logger.getLogger(SimpleExperiment.class.getName()).log(Level.INFO, String.format("Parameter '%s' is %B, so '%s' is being ignored.", Parameters.push(base, P_ALWAYS_REPLACE), alwaysReplace, Parameters.push(base, P_COMPARATOR)));
        assert(repOK());
    }
    
    @Override
    public void migrateAll(final int step, final Population<T> population, final Topology topology) {
        migrateAll(step, population, topology, Option.NONE);
    }
            
    @Override
    public void migrateAll(final int step, final Population<T> population, final Topology topology, final Option<List<IslandConfiguration>> islandConfigs) {
        assert(step >= 0);
        assert(population != null);
        assert(topology != null);
        assert(population.numSuppopulations() == topology.numIslands());
        assert(!(islandConfigs.isDefined() && (islandConfigs.get().size() != topology.numIslands())));
        assert(!(islandConfigs.isDefined() && islandConfigs.get().isEmpty()));
        
        if ((step % interval) != 0)
            return;
        
        // For each island
        for (int source = 0; source < topology.numIslands(); source++) {
            final Set<Integer> neighbors = topology.getNeighbors(source);
            // Randomly choose a destination from its neighbors
            final int targetIndex = random.nextInt(neighbors.size());
            final int target = Misc.getIthSetElement(neighbors, targetIndex);
            assert(target >= 0);
            assert(target < population.numSuppopulations());
            if (islandConfigs.isDefined())
                migrate(source, target, population, new Option(islandConfigs.get().get(target)));
            else
                migrate(source, target, population, Option.NONE);
        }
        assert(repOK());
    }
    
    private void migrate(final int sourcePopIndex, final int targetPopIndex, final Population<T> population, final Option<IslandConfiguration> targetIsland) {
        assert(sourcePopIndex >= 0);
        assert(sourcePopIndex < population.numSuppopulations());
        assert(targetPopIndex >= 0);
        assert(targetPopIndex < population.numSuppopulations());
        assert(sourcePopIndex != targetPopIndex);
        
        final List<T> sourcePop = population.getSubpopulation(sourcePopIndex);
        final List<T> targetPop = population.getSubpopulation(targetPopIndex);
        final T sourceInd = targetIsland.isDefined() ?
                (T) targetIsland.get().getEvaluator().evaluate(sourceSelector.selectIndividual(sourcePop))
                : sourceSelector.selectIndividual(sourcePop);
        final int targetIndex = replacementSelector.selectIndividualIndex(targetPop);
        final T targetInd = targetPop.get(targetIndex);
        if (alwaysReplace || fitnessComparator.get().betterThan(sourceInd, targetInd))
            targetPop.set(targetIndex, sourceInd);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_INTERVAL != null
                && !P_INTERVAL.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_ALWAYS_REPLACE != null
                && !P_ALWAYS_REPLACE.isEmpty()
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_REPLACEMENT_SELECTOR != null
                && !P_REPLACEMENT_SELECTOR.isEmpty()
                && P_SOURCE_SELECTOR != null
                && !P_SOURCE_SELECTOR.isEmpty()
                && random != null
                && interval > 0
                && sourceSelector != null
                && replacementSelector != null
                && fitnessComparator != null
                && (alwaysReplace || fitnessComparator.isDefined());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RandomMigrationPolicy))
            return false;
        final RandomMigrationPolicy ref = (RandomMigrationPolicy)o;
        return interval == ref.interval
                && alwaysReplace == ref.alwaysReplace
                && random.equals(ref.random)
                && sourceSelector.equals(ref.sourceSelector)
                && replacementSelector.equals(ref.replacementSelector)
                && fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.random);
        hash = 89 * hash + this.interval;
        hash = 89 * hash + Objects.hashCode(this.sourceSelector);
        hash = 89 * hash + Objects.hashCode(this.replacementSelector);
        hash = 89 * hash + (this.alwaysReplace ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.fitnessComparator);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_ALWAYS_REPLACE, alwaysReplace,
                P_INTERVAL, interval,
                P_COMPARATOR, fitnessComparator,
                P_RANDOM, random,
                P_SOURCE_SELECTOR, sourceSelector,
                P_REPLACEMENT_SELECTOR, replacementSelector);
    }
    // </editor-fold>
}
