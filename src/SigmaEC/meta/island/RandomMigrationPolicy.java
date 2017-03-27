package SigmaEC.meta.island;

import SigmaEC.SRandom;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A strategy for migration that moves one or more individuals from each island
 * to some other randomly chosen neighbor every time a fixed interval of generations
 * passes.
 * 
 * @author Eric O. Scott
 */
public class RandomMigrationPolicy<T extends Individual<F>, F extends Fitness> extends MigrationPolicy<T, F> {
    public final static String P_RANDOM = "random";
    public final static String P_INTERVAL = "interval";
    public final static String P_SOURCE_SELECTOR = "sourceSelector";
    public final static String P_REPLACEMENT_SELECTOR = "replacementSelector";
    public final static String P_ALWAYS_REPLACE = "alwaysReplace";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_PREFIX = "prefix";
    public final static String P_LOG_FILE = "logFile";
    public final static String P_NUM_THREADS = "numThreads";
    
    private final SRandom random;
    private final int interval;
    private final Selector<T> sourceSelector;
    private final Selector<T> replacementSelector;
    private final boolean alwaysReplace;
    private final FitnessComparator<T, F> fitnessComparator;
    private final Writer writer;
    private final int numThreads;
    
    public RandomMigrationPolicy(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        interval = parameters.getIntParameter(Parameters.push(base, P_INTERVAL));
        sourceSelector = parameters.getInstanceFromParameter(Parameters.push(base, P_SOURCE_SELECTOR), Selector.class);
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
            // For each island
            for (int source = 0; source < topology.numIslands(); source++) {
                final Set<Integer> neighbors = topology.getNeighbors(source);
                // Randomly choose a destination from its neighbors
                final int targetIndex = random.nextInt(neighbors.size());
                final int target = Misc.getIthSetElement(neighbors, targetIndex);
                assert(target >= 0);
                assert(target < population.numSuppopulations());
                if (islandConfigs.isDefined())
                    add(new MigrationThread(step, source, target, population, new Option(islandConfigs.get().get(target))));
                else
                    add(new MigrationThread(step, source, target, population, Option.NONE));
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
    
    private class MigrationThread implements Callable<Void> {
        private final int step;
        private final int source;
        private final int target;
        private final Population<T, F> population;
        private final Option<IslandConfiguration> targetIsland;
    
        public MigrationThread(final int step, final int sourcePopIndex, final int targetPopIndex, final Population<T, F> population, final Option<IslandConfiguration> targetIsland) {
            assert(step >= 0);
            assert(population != null);
            assert(targetIsland != null);
            assert(sourcePopIndex >= 0);
            assert(sourcePopIndex < population.numSuppopulations());
            assert(targetPopIndex >= 0);
            assert(targetPopIndex < population.numSuppopulations());
            assert(sourcePopIndex != targetPopIndex);
            this.step = step;
            this.source = sourcePopIndex;
            this.target = targetPopIndex;
            this.population = population;
            this.targetIsland = targetIsland;
        }
        
        @Override
        public Void call() {
            final List<T> sourcePop = population.getSubpopulation(source);
            final List<T> targetPop = population.getSubpopulation(target);
            // Select an individual from the source population
            final T oldSourceInd = sourceSelector.selectIndividual(sourcePop);
            
            // If the islands are heterogeneous, evaluate its fitness in the context of the new island
            final T sourceInd = targetIsland.isDefined() ?
                    (T) targetIsland.get().getEvaluator().evaluate(oldSourceInd) // Evaluate the source individual in the context of the new island's fitness function
                    : oldSourceInd;
            
            // Select an individual from the target population
            final int targetIndex = replacementSelector.selectIndividualIndex(targetPop);
            final T targetInd = targetPop.get(targetIndex);
            
            // See how the source individual's fitness comparest to the target deme's bsf
            final double bsfImprovement = sourceInd.getFitness().asScalar() - population.getBest(target, fitnessComparator).getFitness().asScalar();
            final StringBuilder logBuilder = new StringBuilder(String.format("%d, %d, %f, %d, %f, %f", step, source, oldSourceInd.getFitness().asScalar(), target, sourceInd.getFitness().asScalar(), bsfImprovement));
            
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
                && P_SOURCE_SELECTOR != null
                && !P_SOURCE_SELECTOR.isEmpty()
                && P_NUM_THREADS != null
                && !P_NUM_THREADS.isEmpty()
                && P_LOG_FILE != null
                && !P_LOG_FILE.isEmpty()
                && random != null
                && interval > 0
                && sourceSelector != null
                && replacementSelector != null
                && fitnessComparator != null
                && numThreads > 0
                && writer != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RandomMigrationPolicy))
            return false;
        final RandomMigrationPolicy ref = (RandomMigrationPolicy)o;
        return interval == ref.interval
                && numThreads == ref.numThreads
                && alwaysReplace == ref.alwaysReplace
                && writer.equals(ref.writer)
                && random.equals(ref.random)
                && sourceSelector.equals(ref.sourceSelector)
                && replacementSelector.equals(ref.replacementSelector)
                && fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.random);
        hash = 23 * hash + this.interval;
        hash = 23 * hash + Objects.hashCode(this.sourceSelector);
        hash = 23 * hash + Objects.hashCode(this.replacementSelector);
        hash = 23 * hash + (this.alwaysReplace ? 1 : 0);
        hash = 23 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 23 * hash + Objects.hashCode(this.writer);
        hash = 23 * hash + this.numThreads;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%B, %s=%s, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_NUM_THREADS, numThreads,
                P_ALWAYS_REPLACE, alwaysReplace,
                P_LOG_FILE, writer,
                P_INTERVAL, interval,
                P_COMPARATOR, fitnessComparator,
                P_RANDOM, random,
                P_SOURCE_SELECTOR, sourceSelector,
                P_REPLACEMENT_SELECTOR, replacementSelector);
    }
    // </editor-fold>
}
