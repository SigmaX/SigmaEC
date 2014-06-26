package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
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
public class BoundedDoubleObjective extends ObjectiveFunction<DoubleVectorIndividual>
{
    private final IDoublePoint[] bounds;
    private final double outsideValue;
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    
    @Override
    public int getNumDimensions() {
        return bounds.length;
    }
    
    public BoundedDoubleObjective(final int dimensions, final IDoublePoint[] bounds, final ObjectiveFunction<DoubleVectorIndividual> objective, final double outsideValue) throws IllegalArgumentException {
        if (dimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": dimensions is < 1.");
        if (bounds == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds is null.");
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective is null.");
        if (bounds.length != dimensions)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": dimensions and length of bounds array must be equal.");
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds contains null elements.");
        if (!Misc.boundsOK(bounds))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": for each point p in bounds, p.x must be < p.y.");
        if (Double.isNaN(outsideValue))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": outsideValue is NaN.");
        this.bounds = Arrays.copyOf(bounds, dimensions);
        this.objective = objective;
        this.outsideValue = outsideValue;
        assert(repOK());
    }
    
    /** Creates an objective bounded by a hyper-cube bound in each dimension by
     * -bound and bound, i.e. with a width of 2*bound. */
    public BoundedDoubleObjective(final int dimensions, final double bound, final ObjectiveFunction<DoubleVectorIndividual> objective) throws IllegalArgumentException {
        this(dimensions, scalarBoundToArray(dimensions, bound), objective);
        assert(repOK());
    }
    
    /** Creates an objective bounded by a hyper-cube bound in each dimension by
     * -bound and bound, i.e. with a width of 2*bound. */
    public BoundedDoubleObjective(final int dimensions, final double bound, final ObjectiveFunction<DoubleVectorIndividual> objective, final double outsideValue) throws IllegalArgumentException {
        this(dimensions, scalarBoundToArray(dimensions, bound), objective, outsideValue);
        assert(repOK());
    }
    
    public BoundedDoubleObjective(int dimensions, IDoublePoint[] bounds, final ObjectiveFunction<DoubleVectorIndividual> objective) throws IllegalArgumentException {
        this(dimensions, bounds, objective, -1);
        assert(repOK());
    }
    
    private static IDoublePoint[] scalarBoundToArray(final int dimensions, final double bound) {
        final IDoublePoint[] bounds = new IDoublePoint[dimensions];
        for(int i = 0; i < dimensions; i++)
            bounds[i] = new IDoublePoint(-bound, bound);
        return bounds;
    }
    
    private boolean withinBounds(final DoubleVectorIndividual ind) {
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
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == bounds.length);
        if (!withinBounds(ind))
            return outsideValue;
        return objective.fitness(ind);
    }

    @Override
    public void setGeneration(final int i) {
        objective.setGeneration(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return bounds != null
                && objective != null
                && !Double.isNaN(outsideValue)
                && Misc.boundsOK(bounds);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: dimensions=%d, bounds=%s, objective=%s, outsideValue=%f]", this.getClass().getSimpleName(), bounds.length, Arrays.deepToString(bounds), objective.toString(), outsideValue);
    }
    
    @Override
    public boolean equals(final Object ref) {
        if (ref == this)
            return true;
        if (!(ref instanceof BoundedDoubleObjective))
            return false;
        final BoundedDoubleObjective cRef = (BoundedDoubleObjective) ref;
        return Misc.doubleEquals(outsideValue, cRef.outsideValue)
                && Arrays.deepEquals(bounds, cRef.bounds)
                && objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Arrays.deepHashCode(this.bounds);
        return hash;
    }
    //</editor-fold>
}
