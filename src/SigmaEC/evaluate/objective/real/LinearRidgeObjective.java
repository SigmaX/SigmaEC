package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.math.Vector;
import java.util.Arrays;

/**
 * An landscape containing a single, linear "ridge" of high-fitness space.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearRidgeObjective<T extends DoubleVectorIndividual> extends ObjectiveFunction<T>
{
    private final double width;
    private final double highFitness;
    private final double[] interceptVector;
    private final double[] slopeVector;
    private final int numDimensions;
    private final Option<Double> gradientXIntercept;

    //<editor-fold defaultstate="collapsed" desc="Accessors">
    public double getWidth() {
        return width;
    }

    public double getHighFitness() {
        return highFitness;
    }

    /** Returns a defensive copy of the interceptVector. */
    public double[] getInterceptVector()
    {
        return Arrays.copyOf(interceptVector, interceptVector.length);
    }

    /** Returns a defensive copy of the slope vector. */
    public double[] getSlopeVector() {
        return Arrays.copyOf(slopeVector, slopeVector.length);
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    //</editor-fold>
    
    
    /**
     * A LinearRidgeObjective built from a parametric representation of a line.
     * 
     * @param numDimensions Number of dimensions of the phenotype space.
     * @param width Width of the hyper-cylindrical region around the line which will map to high fitness.
     * @param highFitness Value of the high-fitness region.
     * @param interceptVector A point the line passes through.
     * @param slopeVector Slope vector of the line.
     * @throws IllegalArgumentException 
     */
    public LinearRidgeObjective(final int numDimensions, final double width, final double highFitness, final double[] interceptVector, final double[] slopeVector, final Option<Double> gradientXIntercept) throws IllegalArgumentException
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        if (highFitness < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": highFitness is < 1.");
        if (Double.isInfinite(highFitness) || Double.isNaN(highFitness))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": highFitness is infinite, must be finite.");
        if (width <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": width is <= 0.");
        if (Double.isInfinite(width) || Double.isNaN(width))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": width is infinite, must be finite.");
        if (interceptVector == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": interceptVector is null.");
        if (interceptVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("%s: interceptVector has %d elements, must have %d.", this.getClass().getSimpleName(), slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(interceptVector)))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": interceptVector contains non-finite values.");
        if (slopeVector == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector is null.");
        if (slopeVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("%s: slopeVector has %d elements, must have %d.", this.getClass().getSimpleName(), slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(slopeVector)))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector contains non-finite values.");
        if (!Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": slopeVector is not a unit vector.");
        if (gradientXIntercept == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": gradientXIntercept is null.");
            
        this.numDimensions = numDimensions;
        this.width = width;
        this.highFitness = highFitness;
        this.interceptVector = Arrays.copyOf(interceptVector, interceptVector.length);
        this.slopeVector = Arrays.copyOf(slopeVector, slopeVector.length);
        this.gradientXIntercept = gradientXIntercept;
        assert(repOK());
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }
    
    /** Returns this.highFitness if the vector of the given individual is
     * within distance this.width from the line, else returns 0.0.
     */
    @Override
    public double fitness(T ind)
    {
        assert(ind != null);
        final double distance = Vector.pointToLineEuclideanDistance(ind.getGenomeArray(), slopeVector, interceptVector);
        if (distance < width)
            return highFitness;
        else if (gradientXIntercept.isDefined())
            return Math.max(0.0, -highFitness/gradientXIntercept.get() * distance + highFitness);
        else
            return 0;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return width > 0
                && !Double.isInfinite(width)
                && !Double.isNaN(width)
                && highFitness > 0
                && !Double.isInfinite(highFitness)
                && !Double.isNaN(highFitness)
                && numDimensions > 0
                && interceptVector != null
                && interceptVector.length == numDimensions
                && gradientXIntercept != null
                && Misc.finiteValued(interceptVector)
                && slopeVector != null
                && slopeVector.length == numDimensions
                && Misc.finiteValued(slopeVector)
                && Misc.doubleEquals(Vector.euclideanNorm(slopeVector), 1.0);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: NumDimensions=%d, Width=%f, HighFitness=%f, PointVector=%s, SlopeVector=%s]", this.getClass().getSimpleName(), numDimensions, width, highFitness, Arrays.asList(interceptVector), Arrays.asList(slopeVector));
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if(!(o instanceof LinearRidgeObjective))
            return false;
        
        LinearRidgeObjective cRef = (LinearRidgeObjective) o;
        return numDimensions == cRef.numDimensions
                && gradientXIntercept.equals(cRef.gradientXIntercept)
                && Misc.doubleEquals(width, cRef.width)
                && Misc.doubleEquals(highFitness, cRef.highFitness)
                && Misc.doubleArrayEquals(interceptVector, cRef.interceptVector)
                && Misc.doubleArrayEquals(slopeVector, cRef.slopeVector);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.highFitness) ^ (Double.doubleToLongBits(this.highFitness) >>> 32));
        hash = 31 * hash + Arrays.hashCode(this.interceptVector);
        hash = 31 * hash + Arrays.hashCode(this.slopeVector);
        hash = 31 * hash + this.numDimensions;
        hash = 31 * hash + (this.gradientXIntercept != null ? this.gradientXIntercept.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
