package SigmaEC.experiment;

import SigmaEC.CircleOfLife;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SimpleExperiment<T extends Individual> extends Experiment {
    private final Initializer<T> initializer;
    private final CircleOfLife<T> circleOfLife;
    private final int numRuns;
    
    public static <T extends Individual> SimpleExperiment<T> create(final Properties properties, final String base) {
        assert(properties != null);
        assert(base != null);
        return new Builder<T>(properties, base).build();
    }
    
    private SimpleExperiment(final Builder builder) {
        assert(builder != null);
        this.initializer = builder.initializer;
        this.circleOfLife = builder.circleOfLife;
        this.numRuns = builder.numRuns;
        assert(repOK());
    }
    
    public static class Builder<T extends Individual> {
        private final static String P_INITIALIZER = "initializer";
        private final static String P_CIRCLE_OF_LIFE = "circleOfLife";
        private final static String P_NUM_RUNS = "numRuns";
        
        private Initializer<T> initializer;
        private CircleOfLife<T> circleOfLife;
        private int numRuns = 1;
        
        public Builder(final Initializer<T> initializer, final CircleOfLife<T> circleOfLife) {
            assert(initializer != null);
            assert(circleOfLife != null);
            this.initializer = initializer;
            this.circleOfLife = circleOfLife;
        }
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.initializer = Parameters.getInstanceFromParameter(properties, Parameters.push(base, P_INITIALIZER), Initializer.class);
            this.circleOfLife = Parameters.getInstanceFromParameter(properties, Parameters.push(base, P_CIRCLE_OF_LIFE), CircleOfLife.class);
            this.numRuns = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_RUNS));
        }
        
        public SimpleExperiment<T> build() {
            return new SimpleExperiment<T>(this);
        }

        public Builder<T> initializer(final Initializer<T> initializer) {
            assert(initializer != null);
            this.initializer = initializer;
            return this;
        }

        public Builder<T> circleOfLife(final CircleOfLife<T> circleOfLife) {
            assert(circleOfLife != null);
            this.circleOfLife = circleOfLife;
            return this;
        }

        public Builder<T> numRuns(final int numRuns) {
            assert(numRuns > 0);
            this.numRuns = numRuns;
            return this;
        }
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
