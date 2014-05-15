package SigmaEC.evaluate.objective;

import SigmaEC.evaluate.decorate.MaxObjective;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An objective made out of a 2-D lattice of ridges.  If the phenotype
 * space has greater than 2 dimensions, this forms a 2-D manifold of
 * intersecting cylinders.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LatticeObjective<T extends DoubleVectorPhenotype> extends ObjectiveFunction<T>
{
    private final int numDimensions;
    private final double ridgeWidth;
    private final double meshWidth;
    private final double highFitness;
    private final IDoublePoint[] bounds;
    private final ObjectiveFunction<T> objective;
    private final boolean useGradient;

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
    
    public LatticeObjective(final int numDimensions, final double ridgeWidth, final double meshWidth, final double highFitness, final IDoublePoint[] bounds, final boolean useGradient)
    {
        if (numDimensions < 2)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 2.");
        if (ridgeWidth <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": ridgeWidth is <= 0.");
        if (meshWidth < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": meshWidth is < 1.");
        if (Double.isInfinite(meshWidth) || Double.isNaN(meshWidth))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": meshWidth is infinite, must be finite.");
        if (highFitness <= 0)
            throw new IllegalArgumentException(String.format("%s: highFitness is %f, but must be > 0.", this.getClass().getSimpleName(), highFitness));
        if (Double.isInfinite(highFitness) || Double.isNaN(highFitness))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": highFitness is infinite, must be finite.");
        if (bounds == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds is null.");
        if (Misc.containsNulls(bounds))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds contains null elements.");
        if (!Misc.boundsOK(bounds))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": for each point p in bounds, p.x must be < p.y.");
        
        this.numDimensions = numDimensions;
        this.ridgeWidth = ridgeWidth;
        this.meshWidth = meshWidth;
        this.highFitness = highFitness;
        this.bounds = Arrays.copyOf(bounds, bounds.length); // Shallow copy okay because IDoublePoint is immutable
        this.useGradient = useGradient;
        
        objective = constructLattice();
        
        assert(repOK());
    }
    
    private ObjectiveFunction<T> constructLattice()
    {
        final List<ObjectiveFunction<T>> subObjectives = new ArrayList<ObjectiveFunction<T>>();
        final Option<Double> gradientXIntercept = (useGradient ? new Option<Double>(meshWidth/2.0) : Option.NONE);
        
        double[] verticalSlopeVector = new double[numDimensions];
        verticalSlopeVector[0] = 1;
        for (double x = bounds[0].x; x < bounds[0].y; x += meshWidth)
        {
            double[] intercept = new double[numDimensions];
            intercept[1] = x;
            ObjectiveFunction<T> verticalLine = new LinearRidgeObjective(numDimensions, ridgeWidth, highFitness, intercept, verticalSlopeVector, gradientXIntercept);
            subObjectives.add(verticalLine);
        }
        
        double[] horizontalSlopeVector = new double[numDimensions];
        horizontalSlopeVector[1] = 1;
        for (double y = bounds[1].x; y < bounds[1].y; y += meshWidth)
        {
            double[] intercept = new double[numDimensions];
            intercept[0] = y;
            ObjectiveFunction<T> horizontalLine = new LinearRidgeObjective(numDimensions, ridgeWidth, highFitness, intercept, horizontalSlopeVector, gradientXIntercept);
            subObjectives.add(horizontalLine);
        }
        
        return new MaxObjective<T>(subObjectives, numDimensions);
    }
    
    @Override
    public double fitness(T ind)
    {
        assert(ind != null);
        return objective.fitness(ind);
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return numDimensions > 1
                && ridgeWidth > 0
                && !Double.isNaN(meshWidth)
                && !Double.isInfinite(meshWidth)
                && meshWidth > 0
                && !Double.isInfinite(highFitness)
                && !Double.isNaN(highFitness)
                && highFitness > 0
                && bounds != null
                && !Misc.containsNulls(bounds)
                && Misc.boundsOK(bounds)
                && objective != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LatticeObjective))
            return false;
        final LatticeObjective ref = (LatticeObjective) o;
        return numDimensions == ref.numDimensions
                && useGradient == ref.useGradient
                && Misc.doubleEquals(ridgeWidth, ref.ridgeWidth)
                && Misc.doubleEquals(meshWidth, ref.meshWidth)
                && Misc.doubleEquals(highFitness, ref.highFitness)
                && Arrays.equals(bounds, ref.bounds)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.numDimensions;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.ridgeWidth) ^ (Double.doubleToLongBits(this.ridgeWidth) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.meshWidth) ^ (Double.doubleToLongBits(this.meshWidth) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.highFitness) ^ (Double.doubleToLongBits(this.highFitness) >>> 32));
        hash = 67 * hash + Arrays.deepHashCode(this.bounds);
        hash = 67 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 67 * hash + (this.useGradient ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, useGradient=%s, ridgeWidth=%f, meshWidth=%f, highFitness=%f, bounds=%s, objective=%s]", this.getClass().getSimpleName(), numDimensions, useGradient, ridgeWidth, meshWidth, highFitness, Arrays.toString(bounds), objective.toString());
    }
    // </editor-fold>
    
}
