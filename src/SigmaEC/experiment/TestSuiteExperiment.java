package SigmaEC.experiment;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.problemclass.ProblemClass;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A meta-experiment that takes a list of ObjectiveFunctions and launches a
 * sub-experiment on each objective.  The sub-experiment parameters file can
 * reference the objective function in %meta.objective, its dimensionality in
 * %meta.numDimensions, and the PRNG in %meta.random.  A path prefix can
 * also be set to tell the sub-experiments where to output data to, accessible
 * in %meta.prefix.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestSuiteExperiment extends Experiment {
    final public static String P_OBJECTIVES = "objectives";
    final public static String P_PROBLEMCLASS = "problemclass";
    public final static String P_NUM_INSTANCES = "numInstances";
    final public static String P_NUM_RUNS_PER_OBJECTIVE = "numRunsPerObjective"; // XXX The sub-experiment will have its own numRuns parameter; don't need this here.
    final public static String P_PARAMETER_FILE = "parameterFile";
    final public static String P_RANDOM = "random";
    final public static String P_PREFIX = "prefix";
    
    final public static String META_BASE = "meta";
    final public static String P_OBJECTIVE = "objective";
    final public static String P_NUM_DIMENSIONS = "numDimensions";
    final public static String P_EXPERIMENT = "experiment";
    
    final private List<ObjectiveFunction> objectives;
    
    final private int numRunsPerObjective;
    final private String parameterFile;
    final private Random random;
    final private String prefix;
    final private Option<ProblemClass> problemClass;

    public TestSuiteExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        numRunsPerObjective = parameters.getIntParameter(Parameters.push(base, P_NUM_RUNS_PER_OBJECTIVE));
        parameterFile = parameters.getStringParameter(Parameters.push(base, P_PARAMETER_FILE));
        prefix = parameters.getStringParameter(Parameters.push(base, P_PREFIX));
        
        problemClass = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_PROBLEMCLASS), ProblemClass.class);
        final Option<List<ObjectiveFunction>> objectivesOpt = parameters.getOptionalInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        final Option<ObjectiveFunction> objectiveOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        if (!(problemClass.isDefined() ^ objectivesOpt.isDefined() ^ objectiveOpt.isDefined()))
            throw new IllegalStateException(String.format("%s: exactly one of %s, %s %s must be defined.", this.getClass().getSimpleName(), P_PROBLEMCLASS, P_OBJECTIVE, P_OBJECTIVES));
        if (problemClass.isDefined()) {
            final int numInstances = parameters.getIntParameter(Parameters.push(base, P_NUM_INSTANCES));
            objectives = new ArrayList<ObjectiveFunction>() {{
                for (int i = 0; i < numInstances; i++)
                    add(problemClass.get().getNewInstance());
            }};
        }
        else {
            objectives = objectivesOpt.isDefined() ? objectivesOpt.get() : new ArrayList<ObjectiveFunction>() {{ add(objectiveOpt.get()); }};
        }
        assert(repOK());
    }
    
    @Override
    public void run() {
        final Properties subExperimentProperties; 
        try {
            subExperimentProperties = new Properties();
            final FileInputStream pInput = new FileInputStream(parameterFile);
            subExperimentProperties.load(pInput);

        }
        catch (final Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to load properties file while attempting to launch sub-experiment.");
            return;
        }
            
        for (int k = 0; k < objectives.size(); k++) {
            final ObjectiveFunction obj = objectives.get(k);
            for (int i = 0; i < numRunsPerObjective; i++) {
                final String filePrefix = String.format("%sobj%d_%s_%ddimensions_run%d", prefix, k, obj.getClass().getSimpleName(), obj.getNumDimensions(), i);
                final Parameters parameters = new Parameters.Builder(subExperimentProperties)
                        .registerInstance(Parameters.push(META_BASE, P_RANDOM), random)
                        .registerInstance(Parameters.push(META_BASE, P_OBJECTIVE), obj)
                        .setParameter(Parameters.push(META_BASE, P_NUM_DIMENSIONS), String.valueOf(obj.getNumDimensions()))
                        .setParameter(Parameters.push(META_BASE, P_PREFIX), filePrefix)
                        .build();
                final Experiment<Double> experiment = parameters.getInstanceFromParameter(P_EXPERIMENT, Experiment.class);
                experiment.run();
            }
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
