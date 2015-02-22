package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.CircleOfLife.EvolutionResult;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SimpleExperiment<T extends Individual> extends Experiment<Double> {
    private final static String P_CIRCLE_OF_LIFE = "circleOfLife";
    private final static String P_NUM_RUNS = "numRuns";
        
    private final CircleOfLife<T> circleOfLife;
    private final int numRuns;
    private final List<EvolutionResult<T>> results;
    
    public SimpleExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        this.circleOfLife = parameters.getInstanceFromParameter(Parameters.push(base, P_CIRCLE_OF_LIFE), CircleOfLife.class);
        this.numRuns = parameters.getIntParameter(Parameters.push(base, P_NUM_RUNS));
        this.results = new ArrayList<EvolutionResult<T>>(numRuns);
        assert(repOK());
    }
    
    @Override
    public void run() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, String.format("Beginning evolutionary run with the following configuration: %s.", circleOfLife.toString()));
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < numRuns; i++) {
            Logger.getLogger(SimpleExperiment.class.getName()).log(Level.INFO, String.format("Run %d", i));
            results.add(circleOfLife.evolve(i));
        }
        final double time = (System.currentTimeMillis() - startTime)/1000.0;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Finished (" + time + "s)");
    }

    @Override
    public Double getResult() {
        double fitnessSum = 0.0;
        for (final EvolutionResult<T> result : results)
            fitnessSum += result.getBestFitness();
        return fitnessSum/results.size();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return circleOfLife != null
                && numRuns > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SimpleExperiment))
            return false;
        final SimpleExperiment ref = (SimpleExperiment) o;
        return numRuns == ref.numRuns
                && circleOfLife.equals(ref.circleOfLife);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.circleOfLife != null ? this.circleOfLife.hashCode() : 0);
        hash = 43 * hash + this.numRuns;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numRuns=%d, circleOfLife=%s]", numRuns, circleOfLife);
    }
    // </editor-fold>
}
