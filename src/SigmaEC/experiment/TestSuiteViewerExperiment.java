package SigmaEC.experiment;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * An experiment that takes a suite of objective functions 
 * @author Eric 'Siggy' Scott
 */
public class TestSuiteViewerExperiment extends Experiment {
    final public static String P_OBJECTIVES = "objectives";
    final public static String P_PREFIX = "prefix";
    final public static String P_X_MIN = "xMin";
    final public static String P_X_MAX = "xMax";
    final public static String P_Y_MIN = "yMin";
    final public static String P_Y_MAX = "yMax";
    final public static String P_GRANULARITY = "granularity";
    final public static String P_RANDOM = "random";
    final public static String P_FILE = "file";
    
    final private List<ObjectiveFunction> objectives;
    final private String prefix;
    final private IDoublePoint[] bounds;
    final private double granularity;
    final private Random random;
    final private String file;

    public TestSuiteViewerExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        objectives = parameters.getInstancesFromParameter(Parameters.push(base, P_OBJECTIVES), ObjectiveFunction.class);
        prefix = parameters.getStringParameter(Parameters.push(base, P_PREFIX));
        final double xMin = parameters.getDoubleParameter(Parameters.push(base, P_X_MIN));
        final double xMax = parameters.getDoubleParameter(Parameters.push(base, P_X_MAX));
        final double yMin = parameters.getDoubleParameter(Parameters.push(base, P_Y_MIN));
        final double yMax = parameters.getDoubleParameter(Parameters.push(base, P_Y_MAX));
        bounds = new IDoublePoint[2];
        bounds[0] = new IDoublePoint(xMin, xMax);
        bounds[1] = new IDoublePoint(yMin, yMax);
        granularity = parameters.getDoubleParameter(Parameters.push(base, P_GRANULARITY));
        file = parameters.getStringParameter(Parameters.push(base, P_FILE));
        
        if (!allObjectivesTwoDimensions(objectives))
            throw new IllegalArgumentException(String.format("%s: all objectives must have at least 2 dimensions", this.getClass().getSimpleName()));
        if (granularity <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": granularity is not positive.");
        if (bounds == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds is null.");
        if (bounds.length != 2)
            throw new IllegalArgumentException(String.format("%s: dimensionality of bounds is %d, but must be 2.", this.getClass().getSimpleName(), bounds.length));
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds contains null values.");
        
        assert(repOK());
    }
    
    @Override
    public void run() {
        // The callbacks define what happens when R sends back messages or asks for user input.
        final RMainLoopCallbacks callbacks= new RLoggerCallbacks(Logger.getLogger(this.getClass().getSimpleName()));
        // The engine runs R commands.
        final Rengine re = new Rengine(new String[] {"--no-save"}, false, callbacks);
        final InputStream rScriptStream = this.getClass().getResourceAsStream("TestSuiteViewerExperiment.r");
        try {
            final String rScript = Misc.inputStreamToString(rScriptStream);
            re.eval(rScript);
            re.eval(String.format("pdf(\"%s\")", file));
            for (final ObjectiveFunction obj : objectives) {
                final StringWriter gridDataWriter = new StringWriter();
                viewObjective(obj, gridDataWriter);
                final String gridData = gridDataWriter.toString();
                re.eval(String.format("x <- \"%s\"", gridData));
                re.eval("d <- read.csv(text=x)");
                re.eval(String.format("plot_objective(x, \"%s\")", obj.getClass().getSimpleName()));
            }
            re.eval("dev.off()");
            re.eval("quit(save=\"no\")");
        }
        catch (final IOException e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.SEVERE, "Failed to load R script.", e);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getResult() {
        return null;
    }
    
    private static boolean allObjectivesTwoDimensions(final List<ObjectiveFunction> objectives) {
        assert(objectives != null);
        for (final ObjectiveFunction obj : objectives)
            if (obj.getNumDimensions() < 2)
                return false;
        return true;
    }
    
    private void viewObjective(final ObjectiveFunction<DoubleVectorIndividual> objective, final Writer outputDestination) throws IllegalArgumentException, IOException {
        
        outputDestination.write(String.valueOf(0));
        // Print the y grid values along the first row
        for (double y = bounds[1].x; y <= bounds[1].y; y += granularity) {
            outputDestination.write(", " + y);
        }
        outputDestination.write("\n");
        
        for (double x = bounds[0].x; x <= bounds[0].y; x += granularity) {
            outputDestination.write(String.valueOf(x));
            for (double y = bounds[1].x; y <= bounds[1].y; y += granularity) {
                final double[] point = new double[objective.getNumDimensions()];
                point[0] = x;
                point[1] = y;
                final DoubleVectorIndividual ind = new DoubleVectorIndividual(point);
                final double fitness = objective.fitness(ind);
                outputDestination.write(", " + fitness);
            }
            outputDestination.write("\n");
        }
        outputDestination.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_OBJECTIVES != null
                && P_PREFIX != null
                && objectives != null
                && prefix != null
                && !objectives.isEmpty()
                && !Misc.containsNulls(objectives)
                && allObjectivesTwoDimensions(objectives)
                && bounds != null
                && bounds.length == 2
                && Misc.boundsOK(bounds)
                && !Double.isNaN(granularity)
                && !Double.isInfinite(granularity)
                && granularity > 0
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TestSuiteViewerExperiment))
            return false;
        final TestSuiteViewerExperiment ref = (TestSuiteViewerExperiment) o;
        return prefix.equals(ref.prefix)
                && objectives.equals(ref.objectives);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.objectives != null ? this.objectives.hashCode() : 0);
        hash = 97 * hash + (this.prefix != null ? this.prefix.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: prefix=%s, objectives=%s]", this.getClass().getSimpleName(), prefix, objectives);
    }
    // </editor-fold>
}
