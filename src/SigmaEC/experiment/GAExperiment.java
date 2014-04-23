package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.SimpleCircleOfLife;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.operate.BitGeneMutator;
import SigmaEC.operate.Generator;
import SigmaEC.operate.LinearGenomeMatingGenerator;
import SigmaEC.operate.LinearGenomeMutationGenerator;
import SigmaEC.operate.Mator;
import SigmaEC.operate.NPointCrossoverMator;
import SigmaEC.represent.BitGene;
import SigmaEC.represent.BitStringIndividual;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.Selector;
import SigmaEC.select.TournamentSelector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class GAExperiment implements Experiment {
    final private GAParameters params;
    final private GAProblem<DoubleVectorPhenotype> problem;
    final private Random random;
    final private Option<List<PopulationMetric<LinearGenomeIndividual<BitGene>>>> preOperatorMetrics;
    final private Option<List<PopulationMetric<LinearGenomeIndividual<BitGene>>>> postOperatorMetrics;
    
    public GAExperiment(final GAParameters params,
            final GAProblem<DoubleVectorPhenotype> problem,
            final Option<List<PopulationMetric<LinearGenomeIndividual<BitGene>>>> preOperatorMetrics,
            final Option<List<PopulationMetric<LinearGenomeIndividual<BitGene>>>> postOperatorMetrics,
            final Random random) {
        assert(params != null);
        assert(problem != null);
        assert(preOperatorMetrics != null);
        assert(!(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get())));
        assert(postOperatorMetrics != null);
        assert(!(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get())));
        assert(random != null);
        this.params = params;
        this.problem = problem;
        this.preOperatorMetrics = preOperatorMetrics;
        this.postOperatorMetrics = postOperatorMetrics;
        this.random = random;
        assert(repOK());
    }
    
    /** The Builder pattern allows us to concisely create an immutable
     * PleiotropicParameters and set some non-default parameter values at
     * the same time.  For example, if we want to use  myObjective for
     * 10 runs of 500 generations each and a tournament size of 4, we'd run
     * 
     * (new GAParameters.Builder(myObjective))
     *          .numGenerations(500)
     *          .numRuns(10)
     *          .tournamentSize(4)
     *          .build();
     * 
     * The remaining parameters will assume their default values.
     */
    public static class Builder {
        // Parameters with default values
        private int numBits = 3;
        private int populationSize = 50;
        private int numGenerations = 1000;
        private double bitMutationRate = 0.03;
        private int numRuns = 30;
        private int tournamentSize = 2;
        private boolean isDynamic = false;
        
        public Builder() { }
        
        public Builder(final Properties properties, final String base) {
            if (properties == null)
                throw new IllegalArgumentException(String.format("%s: properties is null.", GAExperiment.class.getSimpleName()));
            if (base == null)
                throw new IllegalArgumentException(String.format("%s: base is null.", GAExperiment.class.getSimpleName()));
            
        }
        
        /** Create a modifiable copy of another PleiotropicParameters. */
        /*public Builder(final GAExperiment ref) {
            this.numBits = ref.numBits;
            this.populationSize = ref.populationSize;
            this.numGenerations = ref.numGenerations;
            this.bitMutationRate = ref.bitMutationRate;
            this.numRuns = ref.numRuns;
            this.tournamentSize = ref.tournamentSize;
            this.isDynamic = ref.isDynamic;
        }
        
        public GAExperiment build() {
            return new GAExperiment(this);
        }*/
        
        public Builder numBits(final int numBits) {
            assert(numBits > 0);
            this.numBits = numBits;
            return this;
        }
        
        public Builder populationSize(final int populationSize) {
            assert(populationSize > 1);
            this.populationSize = populationSize;
            return this;
        }
        
        public Builder numGenerations(final int numGenerations) {
            assert(numGenerations > 0);
            this.numGenerations = numGenerations;
            return this;
        }
        
        public Builder bitMutationRate(final double bitMutationRate) {
            assert(bitMutationRate >= 0.0);
            assert(bitMutationRate <= 1.0);
            this.bitMutationRate = bitMutationRate;
            return this;
        }
        
        public Builder numRuns(final int numRuns) {
            assert(numRuns >= 0);
            this.numRuns = numRuns;
            return this;
        }
        
        public Builder tournamentSize(final int tournamentSize) {
            assert(tournamentSize > 1);
            this.tournamentSize = tournamentSize;
            return this;
        }
        
        public Builder isDynamic(final boolean isDynamic) {
            this.isDynamic = isDynamic;
            return this;
        }
    }
    
    private List<Generator<LinearGenomeIndividual<BitGene>>> generators() {
        // Crossover operator
        final Mator<LinearGenomeIndividual<BitGene>> mator = new NPointCrossoverMator<LinearGenomeIndividual<BitGene>, BitGene>(2, true, random);
        final Generator<LinearGenomeIndividual<BitGene>> matingGenerator = new LinearGenomeMatingGenerator<LinearGenomeIndividual<BitGene>, BitGene>(mator);
        
        // Mutation operator
        final BitGeneMutator bitMutator = new BitGeneMutator();
        final Generator<LinearGenomeIndividual<BitGene>> bitMutationGenerator = new LinearGenomeMutationGenerator<LinearGenomeIndividual<BitGene>, BitGene>(params.bitMutationRate, bitMutator, random);
        
        return new ArrayList<Generator<LinearGenomeIndividual<BitGene>>>() {{
            add(matingGenerator); add(bitMutationGenerator);
        }};
    }
    
    /** Set up initial population. */
    private List<LinearGenomeIndividual<BitGene>> initialPopulation() {
        return new ArrayList<LinearGenomeIndividual<BitGene>>(params.populationSize) {{
            for (int i = 0; i < params.populationSize; i++)
                add(new BitStringIndividual(random, params.numBits));
        }};
    }
    
    @Override
    public void run() {
        final Selector<LinearGenomeIndividual<BitGene>> parentSelector = new TournamentSelector<LinearGenomeIndividual<BitGene>, DoubleVectorPhenotype>(problem.getObjective(), problem.getDecoder(), random, params.tournamentSize);
        final List<Generator<LinearGenomeIndividual<BitGene>>> generators = generators();

        // Set up the evolutionary loop
        final CircleOfLife<LinearGenomeIndividual<BitGene>> loop = new SimpleCircleOfLife.Builder<LinearGenomeIndividual<BitGene>>(generators, problem)
                        .parentSelector(parentSelector)
                        .preOperatorMetrics(preOperatorMetrics.get())
                        .postOperatorMetrics(postOperatorMetrics.get())
                        .build();
    
        // Set up the initial population
        final List<LinearGenomeIndividual<BitGene>> population = initialPopulation();
        
        System.out.println("Setup of evolutionary run: ");
        System.out.println(loop.toString());
        
        // GO!
        try
        {
            for (int i = 0; i < params.numRuns; i++)
            {
                System.out.println("Run " + i);
                loop.evolve(i, population, params.numGenerations);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

    @Override
    public final boolean repOK() {
        return params != null
                && problem != null
                && preOperatorMetrics != null
                && postOperatorMetrics != null
                && random != null
                && !(preOperatorMetrics.isDefined() && Misc.containsNulls(preOperatorMetrics.get()))
                && !(postOperatorMetrics.isDefined() && Misc.containsNulls(postOperatorMetrics.get()));
    }
    
}
