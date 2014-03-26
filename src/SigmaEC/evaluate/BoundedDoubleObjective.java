package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 * A decorator that gives hyper-rectangular constraints to an ObjectiveFunction
 * in R^n: Any point outside the specified bounds is mapped to a fitness of -1,
 * and any point inside the bounds (inclusive) is evaluated with the wrapped
 * ObjectiveFunction.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BoundedDoubleObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final IDoublePoint[] bounds;
    private final double outsideValue;
    private ObjectiveFunction<DoubleVectorPhenotype> objective;
    
    @Override
    public int getNumDimensions()
    {
        return bounds.length;
    }
    
    public BoundedDoubleObjective(int dimensions, IDoublePoint[] bounds, final ObjectiveFunction<DoubleVectorPhenotype> objective, final double outsideValue) throws IllegalArgumentException
    {
        if (dimensions <= 0)
            throw new IllegalArgumentException("BoundedDoubleObjective: dimensions is < 1.");
        if (bounds == null)
            throw new IllegalArgumentException("BoundedDoubleObjective: bounds is null.");
        if (objective == null)
            throw new IllegalArgumentException("BoundedDoubleObjective: objective is null.");
        if (bounds.length != dimensions)
            throw new IllegalArgumentException("BoundedDoubleObjective: dimensions and length of bounds array must be equal.");
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException("BoundedDoubleObjective: bounds contains null elements.");
        if (!Misc.boundsOK(bounds))
            throw new IllegalArgumentException("BoundedDoubleObjective: for each point p in bounds, p.x must be < p.y.");
        if (Double.isNaN(outsideValue))
            throw new IllegalArgumentException("BoundedDoubleObjective: outsideValue is NaN.");
        this.bounds = Arrays.copyOf(bounds, dimensions);
        this.objective = objective;
        this.outsideValue = outsideValue;
        assert(repOK());
    }
    
    /** Creates an objective bounded by a hyper-cube bound in each dimension by
     * -bound and bound, i.e. with a width of 2*bound. */
    public BoundedDoubleObjective(final int dimensions, final double bound, final ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException
    {
        this(dimensions, scalarBoundToArray(dimensions, bound), objective);
        assert(repOK());
    }
    
    /** Creates an objective bounded by a hyper-cube bound in each dimension by
     * -bound and bound, i.e. with a width of 2*bound. */
    public BoundedDoubleObjective(final int dimensions, final double bound, final ObjectiveFunction<DoubleVectorPhenotype> objective, final double outsideValue) throws IllegalArgumentException
    {
        this(dimensions, scalarBoundToArray(dimensions, bound), objective, outsideValue);
        assert(repOK());
    }
    
    public BoundedDoubleObjective(int dimensions, IDoublePoint[] bounds, final ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException {
        this(dimensions, bounds, objective, -1);
        assert(repOK());
    }
    
    private static IDoublePoint[] scalarBoundToArray(final int dimensions, final double bound)
    {
        IDoublePoint[] bounds = new IDoublePoint[dimensions];
        for(int i = 0; i < dimensions; i++)
            bounds[i] = new IDoublePoint(-bound, bound);
        return bounds;
    }
    
    private boolean withinBounds(DoubleVectorPhenotype ind)
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
    public double fitness(DoubleVectorPhenotype ind)
    {
        assert(ind.size() == bounds.length);
        if (!withinBounds(ind))
            return outsideValue;
        return objective.fitness(ind);
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return bounds != null
                && objective != null
                && objective.repOK()
                && Misc.boundsOK(bounds);
    }
    
    @Override
    public String toString()
    {
        return String.format("[BoundedDoubleObjective: Dimensions=%d, Bounds=%s, Objective=%s]", bounds.length, Arrays.deepToString(bounds), objective.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (ref == this)
            return true;
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
