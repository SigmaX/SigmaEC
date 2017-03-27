package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.SRandom;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.meta.CircleOfLife;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.Operator;
import SigmaEC.meta.Population;
import SigmaEC.meta.StoppingCondition;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric O. Scott
 * @param <T> The genotype of individuals.
 * @param <P> The phenotype of individuals.
 */
public class HeterogeneousIslandModelCircleOfLife<T extends Individual<F>, P, F extends Fitness> extends CircleOfLife<T, F> {
    public final static String P_RANDOM = "random";
    public final static String P_TOPOLOGY = "topology";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    public final static String P_NUM_THREADS = "numThreads";
    public final static String P_MIGRATION = "migrationPolicy";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_METRICS = "metrics";
    public final static String P_ISLAND_GENERATOR = "islandGenerator";
    
    // IslandConfiguration-specific parameter names
    
    private final SRandom random;
    private final Topology topology;
    private final MigrationPolicy<T, F> migrationPolicy;
    private final Initializer<T> initializer;
    private final StoppingCondition<T, F> stoppingCondition;
    private final Option<List<PopulationMetric<T, F>>> metrics;
    private final int numThreads;
    
    private final List<IslandConfiguration<T, P, F>> islands;
    
    public List<IslandConfiguration> getIslands() {
        final ArrayList<IslandConfiguration> result = new ArrayList<>();
        result.addAll(islands);
        assert(repOK());
        return result;
    }
    
    public HeterogeneousIslandModelCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        topology = parameters.getInstanceFromParameter(Parameters.push(base, P_TOPOLOGY), Topology.class);
        if (topology.numIslands() <= 1)
            throw new IllegalStateException(String.format("%s: must have at least two islands in '%s'.", this.getClass().getSimpleName(), Parameters.push(base, P_TOPOLOGY)));
        migrationPolicy = parameters.getInstanceFromParameter(Parameters.push(base, P_MIGRATION), MigrationPolicy.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        numThreads = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_THREADS), topology.numIslands());
        metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        final IslandGenerator<T, P, F> islandGenerator = parameters.getInstanceFromParameter(Parameters.push(base, P_ISLAND_GENERATOR), IslandGenerator.class);
        islands = islandGenerator.getIslands();
        if (islands.size() != topology.numIslands())
            throw new IllegalStateException(String.format("%s: '%s' produced %d island configurations, but '%s' requires %d islands.", this.getClass().getSimpleName(),
                    Parameters.push(base, P_ISLAND_GENERATOR), islands.size(), Parameters.push(base, P_TOPOLOGY), topology.numIslands()));
        assert(repOK());
    }
    
    @Override
    public EvolutionResult<T, F> evolve(final int run) {
        assert(run >= 0);
            reset();
            int step = 0;

            // Initialize subpopulations
            final Population<T, F> population = new Population<>(topology.numIslands(), initializer);
            // Evaluate fitness of initial subpopulations
            for (int i = 0; i < population.numSuppopulations(); i++)
                population.setSubpopulation(i, islands.get(i).getEvaluator().operate(run, step, population.getSubpopulation(i)));

            List<T> bestSoFarInds = getBestsOfStep(population);
            
            final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            while (!(stoppingCondition.stop(population, step))) {
                // Take measurements
                if (metrics.isDefined())
                    for (PopulationMetric<T, F> metric : metrics.get())
                        metric.measurePopulation(run, step, population);

                // Execute each island in parallel
                final Collection<Callable<Void>> tasks = new ArrayList<>(topology.numIslands());
                for (final IslandConfiguration isl : islands)
                    tasks.add(new HeterogeneousIslandModelCircleOfLife.IslandStepper(run, step, isl, population));
                try {
                    final List<Future<Void>> results = executor.invokeAll(tasks);
                    for (final Future<Void> f : results)
                        f.get(); // Throw exception if a thread dies
                } catch (final Exception ex) {
                    Logger.getLogger(HeterogeneousIslandModelCircleOfLife.class.getName()).log(Level.SEVERE, null, ex);
                }

                migrationPolicy.migrateAll(step, population, topology, new Option(getIslands()));

                // Update our local best-so-far variable
                final List<T> bestOfStepInds = getBestsOfStep(population);
                bestSoFarInds = updateBestSoFars(bestOfStepInds, bestSoFarInds);
                
                flushMetrics();
                step++;
            }
            executor.shutdown();

            // Measure final population
            if (metrics.isDefined())
                for (PopulationMetric<T, F> metric : metrics.get())
                    metric.measurePopulation(run, step, population);

            assert(repOK());
            return new EvolutionResult<T, F>(population, bestSoFarInds, getFitnesses(bestSoFarInds));
    }
    
    // <editor-fold defaultstate="collapsed" desc="CircleOfLife helpers">
    private List<T> getBestsOfStep(final Population<T, F> population) {
        final List<T> bests = new ArrayList<>(population.numSuppopulations());
        for (int i = 0; i < population.numSuppopulations(); i++)
            bests.add(population.getBest(i, islands.get(i).getFitnessComparator()));
        assert(!Misc.containsNulls(bests));
        return bests;
    }
    
    private List<T> updateBestSoFars(final List<T> bestOfStep, final List<T> previousBestSoFars) {
        assert(bestOfStep != null);
        assert(!Misc.containsNulls(bestOfStep));
        assert(previousBestSoFars != null);
        assert(!Misc.containsNulls(previousBestSoFars));
        assert(bestOfStep.size() == previousBestSoFars.size());
        final List<T> newBests = new ArrayList<>(bestOfStep.size());
        for (int i = 0; i < bestOfStep.size(); i++) {
            if (islands.get(i).getFitnessComparator().betterThan(bestOfStep.get(i), previousBestSoFars.get(i)))
                newBests.add(bestOfStep.get(i));
            else
                newBests.add(previousBestSoFars.get(i));
        }
        assert(!Misc.containsNulls(newBests));
        return newBests;
    }
    
    private List<F> getFitnesses(final List<T> inds) {
        assert(inds != null);
        assert(!Misc.containsNulls(inds));
        return new ArrayList<F>() {{
            for (final T ind : inds)
                add(ind.getFitness());
        }};
    }
    // </editor-fold>
    
    /** Flush I/O buffers. */
    private void flushMetrics() {
        if (metrics.isDefined())
            for (final PopulationMetric<T, F> metric: metrics.get())
                metric.flush();
    }
    
    private void reset() {
        stoppingCondition.reset();
        if (metrics.isDefined())
            for (final PopulationMetric<T, F> metric : metrics.get())
                    metric.reset();
    }
    
    private class IslandStepper extends ContractObject implements Callable<Void> {
        private final Population<T, F> population;
        private final IslandConfiguration<T, P, F> island;
        private final int run;
        private final int step;
        
        public IslandStepper(final int run, final int step, final IslandConfiguration<T, P, F> island, final Population<T, F> population) {
            assert(island != null);
            assert(population != null);
            this.island = island;
            this.population = population;
            this.run = run;
            this.step = step;
            assert(repOK());
        }

        @Override
        public Void call() throws Exception {
            assert(population != null);
            assert(population.numSuppopulations() == topology.numIslands());

            // Apply operators
            for (final Operator<T> gen : island.getOperators()) {
                final List<T> newSubpop = gen.operate(run, step, population.getSubpopulation(island.getIslandID()));
                    population.setSubpopulation(island.getIslandID(), newSubpop);
            }

            if (island.isDynamic())
                island.getObjective().setStep(step);
            assert(repOK());
            return null;
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return run >= 0
                    && step >= 0
                    && island != null
                    && population != null;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (!(o instanceof HeterogeneousIslandModelCircleOfLife.IslandStepper))
                return false;
            final IslandStepper ref = (IslandStepper)o;
            return step == ref.step
                    && run == ref.run
                    && population.equals(ref.population)
                    && island.equals(ref.island);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.population);
            hash = 71 * hash + Objects.hashCode(this.island);
            hash = 71 * hash + this.run;
            hash = 71 * hash + this.step;
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: run=%d, step=%d, population=%s, island=%s]", HeterogeneousIslandModelCircleOfLife.IslandStepper.class.getSimpleName(), run, step, population, island);
        }
        // </editor-fold>
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_TOPOLOGY != null
                && !P_TOPOLOGY.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && P_NUM_THREADS != null
                && !P_NUM_THREADS.isEmpty()
                && P_MIGRATION != null
                && !P_MIGRATION.isEmpty()
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_METRICS != null
                && !P_METRICS.isEmpty()
                && P_ISLAND_GENERATOR != null
                && !P_ISLAND_GENERATOR.isEmpty()
                && random != null
                && topology != null
                && topology.numIslands() > 1
                && migrationPolicy != null
                && initializer != null
                && stoppingCondition != null
                && metrics != null
                && numThreads > 0
                && islands != null
                && !islands.isEmpty()
                && !Misc.containsNulls(islands);
    }
            
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HeterogeneousIslandModelCircleOfLife))
            return false;
        final HeterogeneousIslandModelCircleOfLife ref = (HeterogeneousIslandModelCircleOfLife)o;
        return numThreads == ref.numThreads
                && random.equals(ref.random)
                && topology.equals(ref.topology)
                && migrationPolicy.equals(ref.migrationPolicy)
                && initializer.equals(ref.initializer)
                && stoppingCondition.equals(ref.stoppingCondition)
                && metrics.equals(ref.metrics)
                && islands.equals(ref.islands);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.random);
        hash = 89 * hash + Objects.hashCode(this.topology);
        hash = 89 * hash + Objects.hashCode(this.migrationPolicy);
        hash = 89 * hash + Objects.hashCode(this.initializer);
        hash = 89 * hash + Objects.hashCode(this.stoppingCondition);
        hash = 89 * hash + Objects.hashCode(this.metrics);
        hash = 89 * hash + this.numThreads;
        hash = 89 * hash + Objects.hashCode(this.islands);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, islands=%s]", this.getClass().getSimpleName(),
                P_NUM_THREADS, numThreads,
                P_RANDOM, random,
                P_TOPOLOGY, topology,
                P_MIGRATION, migrationPolicy,
                P_INITIALIZER, initializer,
                P_STOPPING_CONDITION, stoppingCondition,
                P_METRICS, metrics,
                islands);
    }
    // </editor-fold>
}
