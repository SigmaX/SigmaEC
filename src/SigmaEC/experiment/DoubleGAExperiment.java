package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.Problem;
import SigmaEC.SimpleCircleOfLife;
import SigmaEC.measure.FitnessStatisticsPopulationMetric;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.measure.WriterPopulationMetric;
import SigmaEC.operate.DoubleGeneMutator;
import SigmaEC.operate.Generator;
import SigmaEC.operate.LinearGenomeMatingGenerator;
import SigmaEC.operate.LinearGenomeMutationGenerator;
import SigmaEC.operate.Mator;
import SigmaEC.operate.NPointCrossoverMator;
import SigmaEC.represent.DoubleGene;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.RandomSelector;
import SigmaEC.select.Selector;
import SigmaEC.select.TournamentSelector;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class DoubleGAExperiment implements Experiment {
    final private DoubleGAParameters params;
    final private Problem<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype> problem;
    final private Random random;
    final private Option<String> phenotypeFile;
    final private Option<String> genotypeFile;
    final private Option<String> fitnessFile;
    final private boolean writeFitnessToStandardOut;
    
    public DoubleGAExperiment(final DoubleGAParameters params,
            final Problem<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype> problem,
            final Option<String> phenotypeFile,
            final Option<String> genotypeFile,
            final Option<String> fitnessFile,
            final Random random,
            final boolean writeFitnessToStandardOut) {
        assert(params != null);
        assert(problem != null);
        assert(random != null);
        this.params = params;
        this.problem = problem;
        this.phenotypeFile = phenotypeFile;
        this.genotypeFile = genotypeFile;
        this.fitnessFile = fitnessFile;
        this.random = random;
        this.writeFitnessToStandardOut = writeFitnessToStandardOut;
        assert(repOK());
    }
    
    private List<Generator<LinearGenomeIndividual<DoubleGene>>> generators() {
        // Crossover operator
        final Selector<LinearGenomeIndividual<DoubleGene>> parentSelector = new TournamentSelector<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype>(problem.getObjective(), problem.getDecoder(), random, params.tournamentSize);
        final Mator<LinearGenomeIndividual<DoubleGene>> mator = new NPointCrossoverMator<LinearGenomeIndividual<DoubleGene>, DoubleGene>(2, true, random);
        final Generator<LinearGenomeIndividual<DoubleGene>> matingGenerator = new LinearGenomeMatingGenerator<LinearGenomeIndividual<DoubleGene>, DoubleGene>(parentSelector, mator);
        
        // Mutation operator
        final DoubleGeneMutator mutator = new DoubleGeneMutator(params.mutationStd, random);
        final Generator<LinearGenomeIndividual<DoubleGene>> bitMutationGenerator = new LinearGenomeMutationGenerator<LinearGenomeIndividual<DoubleGene>, DoubleGene>(params.mutationRate, mutator, random);
        
        return new ArrayList<Generator<LinearGenomeIndividual<DoubleGene>>>() {{
            add(matingGenerator); add(bitMutationGenerator);
        }};
    }
    
    /** Set up metrics/output to print the population and fitness stats to the screen. */
    private List<PopulationMetric<LinearGenomeIndividual<DoubleGene>>> postMetrics() {
        final List<PopulationMetric<LinearGenomeIndividual<DoubleGene>>> metrics = new ArrayList<PopulationMetric<LinearGenomeIndividual<DoubleGene>>>();
        
        /*
        if (genotypeFile.isDefined()) {
            final Writer genFileWriter = Misc.openFile(genotypeFile.get());
            final PopulationMetric<LinearGenomeIndividual<DoubleGene>> genotypeFileMetric = new WriterPopulationMetric<LinearGenomeIndividual<DoubleGene>>(genFileWriter, new PrintPleiotropicGenotypePopulationMetric());
            metrics.add(genotypeFileMetric);
        }*/
        
        PopulationMetric<LinearGenomeIndividual<DoubleGene>> fitnessMetric = new FitnessStatisticsPopulationMetric<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype>(problem.getObjective(), problem.getDecoder());
        if (writeFitnessToStandardOut) {
            final Writer standardOut = new BufferedWriter(new OutputStreamWriter(System.out));
            fitnessMetric = new WriterPopulationMetric<LinearGenomeIndividual<DoubleGene>>(standardOut, fitnessMetric);
        }
        if (fitnessFile.isDefined()) {
            final Writer fitnessFileWriter = Misc.openFile(fitnessFile.get());
            fitnessMetric = new WriterPopulationMetric<LinearGenomeIndividual<DoubleGene>>(fitnessFileWriter, fitnessMetric);
        }
        metrics.add(fitnessMetric);
        
        return metrics;
    }
    
    /** Set up initial population. */
    private List<LinearGenomeIndividual<DoubleGene>> initialPopulation() {
        return new ArrayList<LinearGenomeIndividual<DoubleGene>>(params.populationSize) {{
            for (int i = 0; i < params.populationSize; i++)
                add(new DoubleVectorIndividual(random, params.numDimensions, params.initialBound));
        }};
    }
    
    @Override
    public void run() {
        final List<PopulationMetric<LinearGenomeIndividual<DoubleGene>>> postMetrics = postMetrics();
        final List<Generator<LinearGenomeIndividual<DoubleGene>>> generators = generators();
        final Selector<LinearGenomeIndividual<DoubleGene>> survivalSelector = new IterativeSelector<LinearGenomeIndividual<DoubleGene>>();

        // Set up the evolutionary loop
        final CircleOfLife<LinearGenomeIndividual<DoubleGene>> loop = new SimpleCircleOfLife<LinearGenomeIndividual<DoubleGene>>(generators, survivalSelector, Option.NONE, new Option(postMetrics), problem);
        
        // Set up the initial population
        final List<LinearGenomeIndividual<DoubleGene>> population = initialPopulation();
        
        System.out.println("Setup of evolutionary run: ");
        System.out.println(loop.toString());
        
        // GO!
        try
        {
            final int numGenerations = (params.numEvaluations.isDefined()) ?
                    Math.min(params.numGenerations, (int) Math.floor(params.numEvaluations.get()/params.populationSize)) :
                    params.numGenerations;
            for (int i = 0; i < params.numRuns; i++)
            {
                System.out.println("Run " + i);
                loop.evolve(i, population, numGenerations);
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
