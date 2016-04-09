package SigmaEC.meta.island;

import SigmaEC.SRandom;
import SigmaEC.experiment.SimpleExperiment;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.FitnessComparator;
import SigmaEC.select.Selector;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A strategy for migration that generates and injects random individuals into
 * randomly selected islands.  This method ignores the connection topology
 * among islands.
 * 
 * @author Eric O. Scott
 */
public class RandomInjectionMigrationPolicy<T extends Individual> extends MigrationPolicy<T> {
    public final static String P_RANDOM = "random";
    public final static String P_INTERVAL = "interval";
    public final static String P_NUM_INJECTIONS = "numInjections";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_REPLACEMENT_SELECTOR = "replacementSelector";
    public final static String P_ALWAYS_REPLACE = "alwaysReplace";
    public final static String P_COMPARATOR = "fitnessComparator";
    
    private final SRandom random;
    private final int interval;
    private final Option<Integer> numInjections;
    private final Initializer<T> initializer;
    private final Selector<T> replacementSelector;
    private final boolean alwaysReplace;
    private final Option<FitnessComparator<T>> fitnessComparator;
    
    public RandomInjectionMigrationPolicy(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        interval = parameters.getIntParameter(Parameters.push(base, P_INTERVAL));
        numInjections = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_INJECTIONS));
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
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
        
        final int numIndsToGenerate = numInjections.isDefined() ? numInjections.get() : topology.numIslands();
        // For each island
        for (int i = 0; i < numIndsToGenerate; i++) {
            // Randomly choose a destination from the set of all islands
            final int target = random.nextInt(topology.numIslands());
            if (islandConfigs.isDefined())
                migrate(target, population, new Option(islandConfigs.get().get(target)));
            else
                migrate(target, population, Option.NONE);
        }
        assert(repOK());
    }
    
    private void migrate(final int targetPopIndex, final Population<T> population, final Option<IslandConfiguration> targetIslandConfiguration) {
        assert(targetPopIndex >= 0);
        assert(targetPopIndex < population.numSuppopulations());
        
        final List<T> targetPop = population.getSubpopulation(targetPopIndex);
        final T sourceInd = targetIslandConfiguration.isDefined() ?
                (T) targetIslandConfiguration.get().getEvaluator().evaluate(initializer.generateIndividual())
                : initializer.generateIndividual();
        final int targetIndex = replacementSelector.selectIndividualIndex(targetPop);
        final T targetInd = targetPop.get(targetIndex);
        if (alwaysReplace || fitnessComparator.get().betterThan(sourceInd, targetInd))
            synchronized (this) { // XXX Ew.  Move this synchronization logic inside the Population class.
                targetPop.set(targetIndex, sourceInd);
            }
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
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_NUM_INJECTIONS != null
                && !P_NUM_INJECTIONS.isEmpty()
                && random != null
                && interval > 0
                && numInjections != null
                && !(numInjections.isDefined() && (numInjections.get() < 0))
                && initializer != null
                && replacementSelector != null
                && fitnessComparator != null
                && (alwaysReplace || fitnessComparator.isDefined());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RandomInjectionMigrationPolicy))
            return false;
        final RandomInjectionMigrationPolicy ref = (RandomInjectionMigrationPolicy)o;
        return interval == ref.interval
                && alwaysReplace == ref.alwaysReplace
                && numInjections.equals(ref.numInjections)
                && random.equals(ref.random)
                && initializer.equals(ref.initializer)
                && replacementSelector.equals(ref.replacementSelector)
                && fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.random);
        hash = 59 * hash + this.interval;
        hash = 59 * hash + Objects.hashCode(this.numInjections);
        hash = 59 * hash + Objects.hashCode(this.initializer);
        hash = 59 * hash + Objects.hashCode(this.replacementSelector);
        hash = 59 * hash + (this.alwaysReplace ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.fitnessComparator);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_ALWAYS_REPLACE, alwaysReplace,
                P_INTERVAL, interval,
                P_NUM_INJECTIONS, numInjections,
                P_COMPARATOR, fitnessComparator,
                P_RANDOM, random,
                P_INITIALIZER, initializer,
                P_REPLACEMENT_SELECTOR, replacementSelector);
    }
    // </editor-fold>
}
