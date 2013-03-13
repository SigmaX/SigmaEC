package SigmaEC.function;

import SigmaEC.representation.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;
import java.util.Arrays;

/**
 * A decorator that gives hyper-rectangular constraints to an ObjectiveFunction
 * in R^n: Any point outside the specified bounds is mapped to a fitness of -1,
 * and any point inside the bounds (inclusive) is evaluated with the wrapped
 * ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BoundedDoubleObjective extends VectorObjectiveFunction<DoubleVectorIndividual>
{
    private final IDoublePoint[] bounds;
    private VectorObjectiveFunction<DoubleVectorIndividual> objective;
    
    @Override
    public int getNumDimensions()
    {
        return bounds.length;
    }
    
    public BoundedDoubleObjective(int dimensions, IDoublePoint[] bounds, final VectorObjectiveFunction<DoubleVectorIndividual> objective) throws IllegalArgumentException
    {
        if (dimensions <= 0)
            throw new IllegalArgumentException("BoundedDoubleObjective: dimensions was < 1.");
        if (bounds == null)
            throw new IllegalArgumentException("BoundedDoubleObjective: bounds was null.");
        if (objective == null)
            throw new IllegalArgumentException("BoundedDoubleObjective: objective was null.");
        if (bounds.length != dimensions)
            throw new IllegalArgumentException("BoundedDoubleObjective: dimensions and length of bounds array must be equal.");
        if (!boundsOK(bounds))
            throw new IllegalArgumentException("BoundedDoubleObjective: for each point p in bounds, p.x must be < p.y.");
        this.bounds = Arrays.copyOf(bounds, dimensions);
        this.objective = objective;
        assert(repOK());
    }
    
    public BoundedDoubleObjective(final int dimensions, final double bound, final VectorObjectiveFunction<DoubleVectorIndividual> objective) throws IllegalArgumentException
    {
        this(dimensions, scalarBoundToArray(dimensions, bound), objective);
        assert(repOK());
    }
    
    private static IDoublePoint[] scalarBoundToArray(final int dimensions, final double bound)
    {
        IDoublePoint[] bounds = new IDoublePoint[dimensions];
        for(int i = 0; i < dimensions; i++)
            bounds[i] = new IDoublePoint(-bound, bound);
        return bounds;
    }
    
    private boolean withinBounds(DoubleVectorIndividual ind)
    {
        assert(ind.size() == bounds.length);
        for (int i = 0; i < bounds.length; i++)
        {
            double min = bounds[i].x;
            double max = bounds[i].y;
            double val = ind.getElement(i);
            if (val < min || val > max)
                return false;
        }
        return true;
    }
    
    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == bounds.length);
        assert(ind.repOK());
        if (!withinBounds(ind))
            return -1;
        return objective.fitness(ind);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return bounds != null
                && objective != null
                && objective.repOK()
                && boundsOK(bounds);
    }
    
    private static boolean boundsOK(IDoublePoint[] bounds)
    {
        for (IDoublePoint p : bounds)
            if (p.x >= p.y)
                return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return String.format("[BoundedDoubleObjective: Dimensions=%d, Bounds=%s, Objective=%s]", bounds.length, Arrays.deepToString(bounds), objective.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof BoundedDoubleObjective))
            return false;
        BoundedDoubleObjective cRef = (BoundedDoubleObjective) ref;
        return Arrays.deepEquals(bounds, cRef.bounds);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 71 * hash + Arrays.deepHashCode(this.bounds);
        return hash;
    }
    //</editor-fold>
}
