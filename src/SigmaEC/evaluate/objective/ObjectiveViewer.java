package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import java.io.IOException;
import java.io.Writer;

/**
 * Takes an objective function over R^2 and samples it along a grid to produce
 * a matrix suitable for visualization.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ObjectiveViewer
{
    public static <T extends ObjectiveFunction<DoubleVectorPhenotype>>
            void viewObjective(T objective, double granularity, IDoublePoint[] bounds, Writer outputDestination) throws IllegalArgumentException, IOException
    {
        if (objective == null)
            throw new IllegalArgumentException("ObjectiveViewer.viewObjective: objective is null.");
        if (objective.getNumDimensions() != 2)
            throw new IllegalArgumentException(String.format("ObjectiveViewer.viewObjective: dimensionality of objective is %d, but must be 2.", objective.getNumDimensions()));
        if (granularity <= 0)
            throw new IllegalArgumentException("ObjectiveViewer.viewObjective: granularity is not positive.");
        if (bounds == null)
            throw new IllegalArgumentException("ObjectiveViewer.viewObjective: bounds is null.");
        if (bounds.length != 2)
            throw new IllegalArgumentException(String.format("ObjectiveViewer.viewObjective: dimensionality of bounds is %d, but must be 2.", bounds.length));
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException("ObjectiveViewer.viewObjective: bounds contains null values.");
        if (outputDestination == null)
            throw new IllegalArgumentException("ObjectiveViewer.viewObjective: outputDestination is null.");
        
        outputDestination.write(String.valueOf(0));
        // Print the y grid values along the first row
        for (double y = bounds[1].x; y <= bounds[1].y; y += granularity)
        {
            outputDestination.write(", " + y);
        }
        outputDestination.write("\n");
        
        for (double x = bounds[0].x; x <= bounds[0].y; x += granularity)
        {
            outputDestination.write(String.valueOf(x));
            for (double y = bounds[1].x; y <= bounds[1].y; y += granularity)
            {
                final DoubleVectorPhenotype ind = new DoubleVectorPhenotype(new double[] { x, y });
                final double fitness = objective.fitness(ind);
                outputDestination.write(", " + fitness);
            }
            outputDestination.write("\n");
        }
        outputDestination.flush();
    }
}