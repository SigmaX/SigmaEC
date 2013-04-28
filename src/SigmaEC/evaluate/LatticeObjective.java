package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * An objective made out of a 2-D lattice of ridges.  If the phenotype
 * space has greater than 2 dimensions, this forms a 2-D manifold of
 * intersecting cylinders.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LatticeObjective<T extends DoubleVectorIndividual> implements ObjectiveFunction<T>, Dimensioned
{
    private final int numDimensions;
    private final double ridgeWidth;
    private final double meshWidth;
    private final double highFitness;
    private final IDoublePoint[] bounds;
    private final ObjectiveFunction<T> objective;

    //<editor-fold defaultstate="collapsed" desc="Accessors">
    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    public double getRidgeWidth() {
        return ridgeWidth;
    }

    public double getMeshWidth() {
        return meshWidth;
    }

    public double getHighFitness() {
        return highFitness;
    }
    //</editor-fold>
    
    public LatticeObjective(int numDimensions, int ridgeWidth, int meshWidth, int highFitness, IDoublePoint[] bounds)
    {
        if (numDimensions < 2)
            throw new IllegalArgumentException("LatticeObjective: numDimensions is < 2.");
        if (ridgeWidth < 1)
            throw new IllegalArgumentException("LatticeObjective: ridgeWidth is < 1.");
        if (meshWidth < 1)
            throw new IllegalArgumentException("LatticeObjective: meshWidth is < 1.");
        if (highFitness <= 0)
            throw new IllegalArgumentException(String.format("LatticeObjective: highFitness is %f, but must be > 0.", highFitness));
        if (bounds == null)
            throw new IllegalArgumentException("LatticeObjective: bounds is null.");
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException("LatticeObjective: bounds contains null elements.");
        if (!Misc.boundsOK(bounds))
            throw new IllegalArgumentException("LatticeObjective: for each point p in bounds, p.x must be < p.y.");
        
        this.numDimensions = numDimensions;
        this.ridgeWidth = ridgeWidth;
        this.meshWidth = meshWidth;
        this.highFitness = highFitness;
        this.bounds = bounds;
        
        objective = constructLattice();
        
        assert(repOK());
    }
    
    private ObjectiveFunction<T> constructLattice()
    {
        List<ObjectiveFunction<? super T>> subObjectives = new ArrayList<ObjectiveFunction<? super T>>();
        
        double[] verticalSlopeVector = new double[numDimensions];
        verticalSlopeVector[0] = 1;
        for (double x = bounds[0].x; x < bounds[0].y; x += meshWidth)
        {
            double[] intercept = new double[numDimensions];
            intercept[1] = x;
            ObjectiveFunction<T> verticalLine = new LinearRidgeObjective(numDimensions, ridgeWidth, highFitness, intercept, verticalSlopeVector);
            subObjectives.add(verticalLine);
        }
        
        double[] horizontalSlopeVector = new double[numDimensions];
        horizontalSlopeVector[1] = 1;
        for (double y = bounds[1].x; y < bounds[1].y; y += meshWidth)
        {
            double[] intercept = new double[numDimensions];
            intercept[0] = y;
            ObjectiveFunction<T> horizontalLine = new LinearRidgeObjective(numDimensions, ridgeWidth, highFitness, intercept, horizontalSlopeVector);
            subObjectives.add(horizontalLine);
        }
        
        return new AdditiveObjective<T>(subObjectives, numDimensions);
    }
    
    @Override
    public double fitness(T ind)
    {
        assert(ind != null);
        return (objective.fitness(ind) > 0) ? highFitness : 0;
    }

    @Override
    final public boolean repOK()
    {
        return numDimensions > 1
                && ridgeWidth > 0
                && meshWidth > 0
                && highFitness > 0
                && bounds != null
                && !Misc.containsNulls(bounds)
                && Misc.boundsOK(bounds)
                && objective != null;
    }
    
}
