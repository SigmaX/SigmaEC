package SigmaEC.meta;

import SigmaEC.SRandom;
import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.represent.linear.IntGene;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple ant colony optimization.
 * 
 * @author Eric O. Scott
 */
public class ACOCircleOfLife extends CircleOfLife {
    public final static String P_NUM_NODES = "numNodes";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    public final static String P_RANDOM = "random";
    public final static String P_NUM_ANTS = "numAnts";
    public final static String P_EVALUATOR = "evaluator";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_METRICS = "metrics";
    
    private final int numNodes;
    private final int numAnts;
    private final StoppingCondition<IntVectorIndividual> stoppingCondition;
    private final Operator<IntVectorIndividual> evaluator;
    private final Option<List<PopulationMetric<IntVectorIndividual>>> metrics;
    private final FitnessComparator<IntVectorIndividual> fitnessComparator;
    private final SRandom random;
    
    public ACOCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numNodes = parameters.getIntParameter(Parameters.push(base, P_NUM_NODES));
        if (numNodes < 1)
            throw new IllegalStateException(String.format("%s: '%s' is %d, but must be > 1.", this.getClass().getSimpleName(), P_NUM_NODES, numNodes));
        numAnts = parameters.getIntParameter(Parameters.push(base, P_NUM_ANTS));
        if (numAnts < 1)
            throw new IllegalStateException(String.format("%s: '%s' is %d, but must be > 1.", this.getClass().getSimpleName(), P_NUM_ANTS, numNodes));
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        evaluator = parameters.getInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationOperator.class);
        metrics = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_METRICS), PopulationMetric.class);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }
    @Override
    public EvolutionResult evolve(final int run) {
        assert(run >= 0);
        reset();
        
        // Initialize pheromones
        final double[][] pheromones = new double[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++)
            for (int j = 0; j < numNodes; j++)
                pheromones[i][j] = 0;
        
        // Run initial generation of ants
        final Population<IntVectorIndividual> ants = new Population<>(1);
        ants.setSubpopulation(0, executeAnts(pheromones));
        
        IntVectorIndividual bestSoFarInd = null;
        int i = 0;
        while (!stoppingCondition.stop(ants, i)) {
            // Take measurements
            if (metrics.isDefined())
                for (PopulationMetric<IntVectorIndividual> metric : metrics.get())
                    metric.measurePopulation(run, i, ants);
            
            updatePheromones(ants.getSubpopulation(0), pheromones);
            
            // Update our local best-so-far variable
            final IntVectorIndividual bestOfGen = Statistics.best(ants.getSubpopulation(0), fitnessComparator);
            if (fitnessComparator.betterThan(bestOfGen, bestSoFarInd)) 
                bestSoFarInd = bestOfGen;
            
            ants.setSubpopulation(0, executeAnts(pheromones));
            
            flushMetrics();
            i++;
        }
        
        // Measure final population
        if (metrics.isDefined())
            for (PopulationMetric<IntVectorIndividual> metric : metrics.get())
                metric.measurePopulation(run, i, ants);
        
        return new EvolutionResult(ants, bestSoFarInd, bestSoFarInd.getFitness());
    }
    
    final List<IntVectorIndividual> executeAnts(final double[][] pheromones) {
        List<IntVectorIndividual> ants = new ArrayList<IntVectorIndividual>() {{
           for (int i = 0; i < numAnts; i++)
               add(forage(pheromones));
        }};
        return evaluator.operate(0, 0, ants); // Evaluate fitness
    }
    
    private void updatePheromones(final List<IntVectorIndividual> ants, final double[][] pheromones) {
        assert(ants != null);
        assert(pheromones.length == numNodes);
        assert(pheromones[0].length == numNodes);
        
        for (final IntVectorIndividual ant : ants) {
            final int[] tour = ant.getGenomeArray();
            for (int i = 0; i < tour.length - 1; i++) {
                final int start = tour[i];
                final int end = tour[i+1];
                pheromones[start][end] += 1/ant.getFitness();
            }
        }
    }
    
    private IntVectorIndividual forage(final double[][] pheromones) {
        final List<IntGene> path = new ArrayList<IntGene>(numNodes);
        final List<IntGene> unVisitedNodes = new ArrayList<IntGene>(numNodes) {{
           for (int i = 0; i < numNodes; i++)
               add(new IntGene(i));
        }};
        
        int currentNode = 0;
        path.add(new IntGene(0));
        unVisitedNodes.remove(new IntGene(0));
        for (int i = 1; i < numNodes; i++) {
            final IntGene next = chooseNext(unVisitedNodes, pheromones, currentNode);
            path.add(next);
            final boolean r = unVisitedNodes.remove(next);
            assert(r);
            currentNode = next.value;
        }
        assert(path.size() == numNodes);
        return new IntVectorIndividual.Builder(path).build();
    }
    
    private IntGene chooseNext(final List<IntGene> unVisitedNodes, final double[][] pheromones, final int currentNode) {
        assert(unVisitedNodes != null);
        IntGene best = null;
        double bestPheromone = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < 2; i++) {
            final IntGene candidate = unVisitedNodes.get(random.nextInt(unVisitedNodes.size()));
            final double p = pheromones[currentNode][candidate.value];
            if (p > bestPheromone) {
                bestPheromone = p;
                best = candidate;
            }
        }
        return best;
    }

    /** Flush I/O buffers. */
    private void flushMetrics() {
        if (metrics.isDefined())
            for (final PopulationMetric<IntVectorIndividual> metric: metrics.get())
                metric.flush();
    }
    
    private void reset() {
        stoppingCondition.reset();
        if (metrics.isDefined())
            for (final PopulationMetric<IntVectorIndividual> metric : metrics.get())
                    metric.reset();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_NODES != null
                && !P_NUM_NODES.isEmpty()
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
                && P_METRICS != null
                && !P_METRICS.isEmpty()
                && P_NUM_ANTS != null
                && !P_NUM_ANTS.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && numNodes > 1
                && evaluator != null
                && fitnessComparator != null
                && metrics != null
                && !(metrics.isDefined() && Misc.containsNulls(metrics.get()))
                && numAnts > 0
                && random != null
                && stoppingCondition != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ACOCircleOfLife))
            return false;
        final ACOCircleOfLife ref = (ACOCircleOfLife)o;
        return numNodes == ref.numNodes
                && numAnts == ref.numAnts
                && fitnessComparator.equals(ref.fitnessComparator)
                && metrics.equals(ref.metrics)
                && evaluator.equals(ref.evaluator)
                && random.equals(ref.random)
                && stoppingCondition.equals(ref.stoppingCondition);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.numNodes;
        hash = 59 * hash + this.numAnts;
        hash = 59 * hash + (this.stoppingCondition != null ? this.stoppingCondition.hashCode() : 0);
        hash = 59 * hash + (this.evaluator != null ? this.evaluator.hashCode() : 0);
        hash = 59 * hash + (this.metrics != null ? this.metrics.hashCode() : 0);
        hash = 59 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 59 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_NUM_NODES, numNodes,
                P_NUM_ANTS, numAnts,
                P_COMPARATOR, fitnessComparator,
                P_METRICS, metrics,
                P_EVALUATOR, evaluator,
                P_RANDOM, random,
                P_STOPPING_CONDITION, stoppingCondition);
    }
    // </editor-fold>
}
