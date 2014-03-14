package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.math.Vector;
import java.util.Arrays;

/**
 * An landscape containing a single, linear "ridge" of high-fitness space.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearRidgeObjective<T extends DoubleVectorPhenotype> implements ObjectiveFunction<T>
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
            throw new IllegalArgumentException("LinearRidgeObjective: numDimensions is < 1.");
        if (highFitness < 1)
            throw new IllegalArgumentException("LinearRidgeObjective: highFitness is < 1.");
        if (highFitness == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("LinearRidgeObjective: highFitness is infinite, must be finite.");
        if (width <= 0)
            throw new IllegalArgumentException("LinearRidgeObjective: width is <= 0.");
        if (width == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("LinearRidgeObjective: width is infinite, must be finite.");
        if (interceptVector == null)
            throw new IllegalArgumentException("LinearRidgeObjective: interceptVector is null.");
        if (interceptVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("LinearRidgeObjective: interceptVector has %d elements, must have %d.", slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(interceptVector)))
            throw new IllegalArgumentException("LinearRidgeObjective: interceptVector contains non-finite values.");
        if (slopeVector == null)
            throw new IllegalArgumentException("LinearRidgeObjective: slopeVector is null.");
        if (slopeVector.length != numDimensions)
            throw new IllegalArgumentException(String.format("LinearRidgeObjective: slopeVector has %d elements, must have %d.", slopeVector.length, numDimensions));
        if (!(Misc.finiteValued(slopeVector)))
            throw new IllegalArgumentException("LinearRidgeObjective: slopeVector contains non-finite values.");
        if ((Vector.euclideanNorm(slopeVector) - 1.0) > 0.0001)
            throw new IllegalArgumentException("LinearRidgeObjective: slopeVector is not a unit vector.");
        if (gradientXIntercept == null)
            throw new IllegalArgumentException("LinearRidgeObjective: gradientXIntercept is null.");
            
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
        final double distance = Vector.pointToLineEuclideanDistance(ind.getVector(), slopeVector, interceptVector);
        if (distance < width)
            return highFitness;
        else if (gradientXIntercept.isDefined())
            return Math.max(0.0, -highFitness/gradientXIntercept.get() * distance + highFitness);
        else
            return 0;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return width > 0
                && width != Double.POSITIVE_INFINITY
                && highFitness > 0
                && highFitness != Double.POSITIVE_INFINITY
                && numDimensions > 0
                && interceptVector != null
                && interceptVector.length == numDimensions
                && gradientXIntercept != null
                && Misc.finiteValued(interceptVector)
                && slopeVector != null
                && slopeVector.length == numDimensions
                && Misc.finiteValued(slopeVector)
                && (Vector.euclideanNorm(slopeVector) - 1.0) <= 0.0001;
    }
    
    @Override
    public String toString()
    {
        return String.format("[LinearRidgeObjective: NumDimensions=%d, Width=%f, HighFitness=%f, PointVector=%s, SlopeVector=%s]", numDimensions, width, highFitness, Arrays.asList(interceptVector), Arrays.asList(slopeVector));
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if(!(o instanceof LinearRidgeObjective))
            return false;
        
        LinearRidgeObjective cRef = (LinearRidgeObjective) o;
        return numDimensions == cRef.numDimensions
                && Misc.doubleEquals(width, cRef.width)
                && Misc.doubleEquals(highFitness, cRef.highFitness)
                && Misc.doubleArrayEquals(interceptVector, cRef.interceptVector)
                && Misc.doubleArrayEquals(slopeVector, cRef.slopeVector);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.highFitness) ^ (Double.doubleToLongBits(this.highFitness) >>> 32));
        hash = 97 * hash + Arrays.hashCode(this.interceptVector);
        hash = 97 * hash + Arrays.hashCode(this.slopeVector);
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
