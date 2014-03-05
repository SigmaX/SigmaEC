package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.SimpleCircleOfLife;
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
import SigmaEC.select.RandomSelector;
import SigmaEC.select.Selector;
import SigmaEC.select.TournamentSelector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    
    public GAExperiment(final GAParameters params, final GAProblem<DoubleVectorPhenotype> problem, final Random random) {
        assert(params != null);
        assert(problem != null);
        assert(random != null);
        this.params = params;
        this.problem = problem;
        this.random = random;
        assert(repOK());
    }
    
    private List<Generator<LinearGenomeIndividual<BitGene>>> generators() {
        // Crossover operator
        final Selector<LinearGenomeIndividual<BitGene>> parentSelector = new RandomSelector<LinearGenomeIndividual<BitGene>>(random);
        final Mator<LinearGenomeIndividual<BitGene>> mator = new NPointCrossoverMator<LinearGenomeIndividual<BitGene>, BitGene>(2, true, random);
        final Generator<LinearGenomeIndividual<BitGene>> matingGenerator = new LinearGenomeMatingGenerator<LinearGenomeIndividual<BitGene>, BitGene>(parentSelector, mator);
        
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
        final List<Generator<LinearGenomeIndividual<BitGene>>> generators = generators();
        final Selector<LinearGenomeIndividual<BitGene>> survivalSelector = new TournamentSelector<LinearGenomeIndividual<BitGene>, DoubleVectorPhenotype>(problem.getObjective(), problem.getDecoder(), random, params.tournamentSize);

        
        // Set up the evolutionary loop
        final CircleOfLife<LinearGenomeIndividual<BitGene>> loop = new SimpleCircleOfLife<LinearGenomeIndividual<BitGene>>(generators, survivalSelector, null, null, problem);
        
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
                && random != null;
    }
    
}
