package SigmaEC.meta.island;

import SigmaEC.SRandom;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.Selector;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A strategy for migration that generates and injects random individuals into
 * randomly selected islands.  This method ignores the connection topology
 * among islands.
 * 
 * @author Eric O. Scott
 */
public class RandomInjectionMigrationPolicy<T extends Individual<F>, F extends Fitness> extends MigrationPolicy<T, F> {
    public final static String P_RANDOM = "random";
    public final static String P_INTERVAL = "interval";
    public final static String P_NUM_INJECTIONS = "numInjections";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_REPLACEMENT_SELECTOR = "replacementSelector";
    public final static String P_ALWAYS_REPLACE = "alwaysReplace";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_PREFIX = "prefix";
    public final static String P_LOG_FILE = "logFile";
    public final static String P_NUM_THREADS = "numThreads";
    
    private final SRandom random;
    private final int interval;
    private final Option<Integer> numInjections;
    private final Initializer<T> initializer;
    private final Selector<T> replacementSelector;
    private final boolean alwaysReplace;
    private final FitnessComparator<T, F> fitnessComparator;
    private final Writer writer;
    private final int numThreads;
    
    public RandomInjectionMigrationPolicy(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        interval = parameters.getIntParameter(Parameters.push(base, P_INTERVAL));
        numInjections = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_INJECTIONS));
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        replacementSelector = parameters.getInstanceFromParameter(Parameters.push(base, P_REPLACEMENT_SELECTOR), Selector.class);
        numThreads = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_THREADS), Runtime.getRuntime().availableProcessors());
        alwaysReplace = parameters.getBooleanParameter(Parameters.push(base, P_ALWAYS_REPLACE));
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        
        final Option<String> file = parameters.getOptionalStringParameter(Parameters.push(base, P_LOG_FILE));
        if (file.isDefined()) {
            final String prefix = parameters.getOptionalStringParameter(Parameters.push(base, P_PREFIX), "");
            final String fileName = prefix + file.get();
            try {
                writer = new FileWriter(fileName);
            }
            catch (final IOException e) {
                throw new IllegalArgumentException(this.getClass().getSimpleName() + ": could not open file " + fileName, e);
            }
        }
        else
            writer = new OutputStreamWriter(System.out);
        assert(repOK());
    }
    
    @Override
    public void migrateAll(final int step, final Population<T, F> population, final Topology topology) {
        migrateAll(step, population, topology, Option.NONE);
    }
            
    @Override
    public void migrateAll(final int step, final Population<T, F> population, final Topology topology, final Option<List<IslandConfiguration>> islandConfigs) {
        assert(step >= 0);
        assert(population != null);
        assert(topology != null);
        assert(population.numSuppopulations() == topology.numIslands());
        assert(!(islandConfigs.isDefined() && (islandConfigs.get().size() != topology.numIslands())));
        assert(!(islandConfigs.isDefined() && islandConfigs.get().isEmpty()));
        
        if ((step % interval) != 0)
            return;
        
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        final Collection<Callable<Void>> tasks = new ArrayList<Callable<Void>>(topology.numIslands()) {{
            final int numIndsToGenerate = numInjections.isDefined() ? numInjections.get() : topology.numIslands();
            // For each island
            for (int i = 0; i < numIndsToGenerate; i++) {
                // Randomly choose a destination from the set of all islands
                final int target = random.nextInt(topology.numIslands());
                if (islandConfigs.isDefined())
                    add(new InjectionThread(step, target, population, new Option(islandConfigs.get().get(target))));
                else
                    add(new InjectionThread(step, target, population, Option.NONE));
            }
        }};
        try {
            executor.invokeAll(tasks);
        } catch (final InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        }
        finally {
            executor.shutdown();
        }
        assert(repOK());
    }
    
    private class InjectionThread implements Callable<Void> {
        private final int step;
        private final int target;
        private final Population<T, F> population;
        private final Option<IslandConfiguration> targetIsland;
    
        public InjectionThread(final int step, final int targetPopIndex, final Population<T, F> population, final Option<IslandConfiguration> targetIsland) {
            assert(step >= 0);
            assert(population != null);
            assert(targetIsland != null);
            assert(targetPopIndex >= 0);
            assert(targetPopIndex < population.numSuppopulations());
            this.step = step;
            this.target = targetPopIndex;
            this.population = population;
            this.targetIsland = targetIsland;
        }
        
        @Override
        public Void call() {
            final List<T> targetPop = population.getSubpopulation(target);
            
            final T sourceInd = targetIsland.isDefined() ?
                    (T) targetIsland.get().getEvaluator().evaluate(initializer.generateIndividual())
                    : initializer.generateIndividual();
            
            // Select an individual from the target population
            final int targetIndex = replacementSelector.selectIndividualIndex(targetPop);
            final T targetInd = targetPop.get(targetIndex);
            
            // See how the source individual's fitness comparest to the target deme's best individual
            final double improvementOverBest = sourceInd.getFitness().asScalar() - population.getBest(target, fitnessComparator).getFitness().asScalar();
            final StringBuilder logBuilder = new StringBuilder(String.format("%d, %d, %f, %f, ", step, target, sourceInd.getFitness().asScalar(), improvementOverBest));
            
            // Have the source and target individuals compete
            if (alwaysReplace || fitnessComparator.betterThan(sourceInd, targetInd)) {
                population.set(target, targetIndex, sourceInd); // This method is threadsafe
                logBuilder.append("invaded\n");
            }
            else
                logBuilder.append("repelled\n");

            try {
                synchronized (writer) {
                    writer.write(logBuilder.toString());
                    writer.flush();
                }
            } catch (final IOException ex) {
                Logger.getLogger(RandomMigrationPolicy.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
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
                && P_PREFIX != null
                && !P_PREFIX.isEmpty()
                && P_LOG_FILE != null
                && !P_LOG_FILE.isEmpty()
                && P_NUM_THREADS != null
                && !P_NUM_THREADS.isEmpty()
                && random != null
                && interval > 0
                && numInjections != null
                && !(numInjections.isDefined() && (numInjections.get() < 0))
                && initializer != null
                && replacementSelector != null
                && fitnessComparator != null
                && numThreads > 0
                && writer != null;
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
                && numThreads == ref.numThreads
                && numInjections.equals(ref.numInjections)
                && random.equals(ref.random)
                && writer.equals(ref.writer)
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
