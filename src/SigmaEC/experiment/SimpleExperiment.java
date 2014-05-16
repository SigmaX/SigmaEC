package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SimpleExperiment<T extends Individual> extends Experiment {
    private final static String P_INITIALIZER = "initializer";
    private final static String P_CIRCLE_OF_LIFE = "circleOfLife";
    private final static String P_NUM_RUNS = "numRuns";
    private final static String P_RANDOM = "random";
        
    private final Random random;
    private final Initializer<T> initializer;
    private final CircleOfLife<T> circleOfLife;
    private final int numRuns;
    
    public SimpleExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        this.initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        this.circleOfLife = parameters.getInstanceFromParameter(Parameters.push(base, P_CIRCLE_OF_LIFE), CircleOfLife.class);
        this.numRuns = parameters.getIntParameter(Parameters.push(base, P_NUM_RUNS));
        assert(repOK());
    }
    
    @Override
    public void run() {
        final List<T> initialPopulation = initializer.generateInitialPopulation();
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, String.format("Beginning evolutionary run with the following configuration: %s.", circleOfLife.toString()));
        
        for (int i = 0; i < numRuns; i++) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, String.format("Run %d", i));
            circleOfLife.evolve(i, initialPopulation);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Finished");
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return initializer != null
                && circleOfLife != null
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
                && initializer.equals(ref.initializer)
                && circleOfLife.equals(ref.circleOfLife);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.initializer != null ? this.initializer.hashCode() : 0);
        hash = 43 * hash + (this.circleOfLife != null ? this.circleOfLife.hashCode() : 0);
        hash = 43 * hash + this.numRuns;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numRuns=%d, initializer=%s, circleOfLife=%s]", numRuns, initializer.toString(), circleOfLife.toString());
    }
    // </editor-fold>
}
