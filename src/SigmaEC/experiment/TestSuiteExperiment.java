package SigmaEC.experiment;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A meta-experiment that takes a list of ObjectiveFunctions and launches a
 * sub-experiment on each objective.  The sub-experiment properties file can
 * should reference the objective function in %meta.objective, and the PRNG
 * in %meta.random
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestSuiteExperiment extends Experiment {
    final private static String P_OBJECTIVES = "objectives";
    final private static String P_PARAMETER_FILE = "parameterFile";
    private final static String P_RANDOM = "random";
    
    final private static String META_BASE = "meta";
    final private static String P_OBJECTIVE = "objective";
    final private static String P_EXPERIMENT = "experiment";
    
    final private List<ObjectiveFunction> objectives;
    final private String parameterFile;
    final private Random random;

    public TestSuiteExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        parameterFile = parameters.getStringParameter(Parameters.push(base, P_PARAMETER_FILE));
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        assert(repOK());
    }
    
    @Override
    public void run() {
        for (final ObjectiveFunction obj : objectives) {
            final Properties properties; 
            try {
                properties = new Properties();
                final FileInputStream pInput = new FileInputStream(parameterFile);
                properties.load(pInput);

            }
            catch (final Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to load properties file while attempting to launch sub-experiment.");
                return;
            }
            final Parameters parameters = new Parameters.Builder(properties)
                    .registerInstance(Parameters.push(META_BASE, P_OBJECTIVE), obj)
                    .registerInstance(Parameters.push(META_BASE, P_RANDOM), random)
                    .build();
            final Experiment<Double> experiment = parameters.getInstanceFromParameter(P_EXPERIMENT, Experiment.class);
            experiment.run();
        }
    }

    @Override
    public Object getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return objectives != null
                && !objectives.isEmpty()
                && parameterFile != null
                && !parameterFile.isEmpty()
                && !Misc.containsNulls(objectives);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TestSuiteExperiment))
            return false;
        final TestSuiteExperiment ref = (TestSuiteExperiment) o;
        return parameterFile.equals(ref.parameterFile)
                && objectives.equals(ref.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        hash = 83 * hash + (this.parameterFile != null ? this.parameterFile.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: parameterFile=%s, objectives=%s]", this.getClass().getSimpleName(), parameterFile, objectives);
    }
    // </editor-fold>
}
