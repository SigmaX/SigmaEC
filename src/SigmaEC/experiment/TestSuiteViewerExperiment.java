package SigmaEC.experiment;

import SigmaEC.SRandom;
import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    final public static String P_GENERATIONS = "generations";
    
    final private List<ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness>> objectives;
    final private String prefix;
    final private IDoublePoint[] bounds;
    final private double granularity;
    final private Random random;
    // FIXME: generations not used
    final private Option<Integer> generations;

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
        generations = parameters.getOptionalIntParameter(Parameters.push(base, P_GENERATIONS));
        
        if (!allObjectivesLessThanThreeDimensions(objectives))
            throw new IllegalArgumentException(String.format("%s: all objectives must have at most 2 dimensions", this.getClass().getSimpleName()));
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
        for (int i = 0; i < objectives.size(); i++) {
            final ObjectiveFunction obj = objectives.get(i);
            final String fileName = (objectives.size() > 1) ?
                    String.format("%sFunction_%d.csv", prefix, i)
                    : String.format("%sFunction.csv", prefix);
            try {
                final Writer writer = new FileWriter(fileName);
                viewObjective(obj, writer);
            }
            catch (final IOException e) {
                throw new IllegalStateException(this.getClass().getSimpleName() +": could not open file " + fileName, e);
            }
        }
        assert(repOK());
    }

    @Override
    public Object getResult() {
        return null;
    }
    
    private static <T extends Individual> boolean allObjectivesLessThanThreeDimensions(final List<ObjectiveFunction<T, ScalarFitness>> objectives) {
        assert(objectives != null);
        for (final ObjectiveFunction obj : objectives)
            if (obj.getNumDimensions() > 2)
                return false;
        return true;
    }
    
    private void viewObjective(final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective, final Writer output) throws IOException {
        assert(objective != null);
        if (objective.getNumDimensions() == 1)
            viewObjective1D(objective, output);
        else if (objective.getNumDimensions() == 2)
            viewObjective2D(objective, output);
        else
            throw new IllegalStateException(String.format("%s: encountered an objective function with %d dimensions, but can only sample the landscape of 1- or 2-dimensional objectives.", this.getClass().getSimpleName(), objective.getNumDimensions()));
    }
    
    private void viewObjective1D(final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective, final Writer output) throws IOException {
        assert(objective != null);
        assert(objective.getNumDimensions() == 1);
        assert(output != null);
        
        for (double x = bounds[0].x; x <= bounds[0].y; x += granularity) {
            output.write(String.valueOf(x));
            final DoubleVectorIndividual ind = new DoubleVectorIndividual.Builder(new double[] { x }).build();
            final double fitness = objective.fitness(ind).asScalar();
            output.write(", ");
            output.write(String.valueOf(fitness));
            output.write("\n");
        }
        output.flush();
    }
    
    private void viewObjective2D(final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective, final Writer output) throws IOException {
        assert(objective != null);
        assert(objective.getNumDimensions() == 2);
        assert(output != null);
        output.write(String.valueOf(0));
        // Print the y grid values along the first row
        for (double y = bounds[1].x; y <= bounds[1].y; y += granularity) {
            output.write(", " + y);
        }
        output.write("\n");
        
        for (double x = bounds[0].x; x <= bounds[0].y; x += granularity) {
            output.write(String.valueOf(x));
            for (double y = bounds[1].x; y <= bounds[1].y; y += granularity) {
                final double[] point = new double[objective.getNumDimensions()];
                point[0] = x;
                point[1] = y;
                // The remaining dimensions are left set to zero
                final DoubleVectorIndividual ind = new DoubleVectorIndividual.Builder(point).build();
                final double fitness = objective.fitness(ind).asScalar();
                output.write(", " + fitness);
            }
            output.write("\n");
        }
        output.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_OBJECTIVES != null
                && !P_OBJECTIVES.isEmpty()
                && P_PREFIX != null
                && !P_PREFIX.isEmpty()
                && P_GENERATIONS != null
                && !P_GENERATIONS.isEmpty()
                && P_GRANULARITY != null
                && !P_GRANULARITY.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_X_MAX != null
                && !P_X_MAX.isEmpty()
                && P_X_MIN != null
                && !P_X_MIN.isEmpty()
                && P_Y_MAX != null
                && !P_Y_MIN.isEmpty()
                && objectives != null
                && prefix != null
                && !prefix.isEmpty()
                && !objectives.isEmpty()
                && !Misc.containsNulls(objectives)
                && allObjectivesLessThanThreeDimensions(objectives)
                && bounds != null
                && bounds.length == 2
                && Misc.boundsOK(bounds)
                && !Double.isNaN(granularity)
                && !Double.isInfinite(granularity)
                && granularity > 0
                && random != null
                && generations != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TestSuiteViewerExperiment))
            return false;
        final TestSuiteViewerExperiment ref = (TestSuiteViewerExperiment) o;
        return prefix.equals(ref.prefix)
                && objectives.equals(ref.objectives)
                && Arrays.equals(bounds, ref.bounds)
                && generations.equals(ref.generations)
                && Misc.doubleEquals(granularity, ref.granularity)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.objectives);
        hash = 79 * hash + Objects.hashCode(this.prefix);
        hash = 79 * hash + Arrays.deepHashCode(this.bounds);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.granularity) ^ (Double.doubleToLongBits(this.granularity) >>> 32));
        hash = 79 * hash + Objects.hashCode(this.random);
        hash = 79 * hash + Objects.hashCode(this.generations);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, bounds=%s, %s=%f, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_PREFIX, prefix,
                P_OBJECTIVES, objectives,
                Arrays.toString(bounds),
                P_GRANULARITY, granularity,
                P_RANDOM, random,
                P_GENERATIONS, generations);
    }
    // </editor-fold>
}
