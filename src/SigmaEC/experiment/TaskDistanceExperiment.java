package SigmaEC.experiment;

import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.represent.distance.DistanceMeasure;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Matrix;
import SigmaEC.util.math.Statistics;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class TaskDistanceExperiment extends Experiment<double[][]> {
    public final static String P_TASKS = "tasks";
    public final static String P_DISTANCE_MEASURE = "distanceMeasure";
    
    private final List<BooleanFunction> tasks;
    private final DistanceMeasure<BooleanFunction> distanceMeasure;
    
    private double[][] result;
    
    public TaskDistanceExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        tasks = parameters.getInstancesFromParameter(Parameters.push(base, P_TASKS), BooleanFunction.class);
        distanceMeasure = parameters.getInstanceFromParameter(Parameters.push(base, P_DISTANCE_MEASURE), DistanceMeasure.class);
        assert(repOK());
    }

    @Override
    public void run() {
        result = Statistics.distanceMatrix(tasks, distanceMeasure);
        System.out.println(Matrix.prettyPrint(result));
        assert(repOK());
    }

    @Override
    public double[][] getResult() {
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DISTANCE_MEASURE != null
                && !P_DISTANCE_MEASURE.isEmpty()
                && tasks != null
                && !tasks.isEmpty()
                && !Misc.containsNulls(tasks)
                && distanceMeasure != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskDistanceExperiment))
            return false;
        final TaskDistanceExperiment ref = (TaskDistanceExperiment)o;
        return distanceMeasure.equals(ref.distanceMeasure)
                && tasks.equals(ref.tasks);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.tasks);
        hash = 97 * hash + Objects.hashCode(this.distanceMeasure);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_DISTANCE_MEASURE, distanceMeasure,
                P_TASKS, tasks);
    }
    // </editor-fold>
}
