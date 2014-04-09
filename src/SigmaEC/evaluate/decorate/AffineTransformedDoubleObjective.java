package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import SigmaEC.util.math.Matrix;
import java.util.Arrays;

/**
 * A decorator that applies an affine transformation to an ObjectiveFunction
 * over R^n.
 *
 * Translation is not implemented because that would require using homogeneous
 * coordinates (with one extra "fake" dimension).  I think it would be more
 * computationally expensive than its worth, especially since we're not using
 * dedicated hardware.  Use TranslateDoubleObjective instead (or in addition).
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class AffineTransformedDoubleObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final double[][] transformationMatrix;
    private final ObjectiveFunction<DoubleVectorPhenotype> objective;
    private final int numDimensions;
    
    /** Convenience constructor for creating affine transformation in R^2.
     * @param angles The angles by which the different axes will be rotated.
     * @param scale The value by which the function will be scaled.
     * @param objective The original objective function.
     */
    public AffineTransformedDoubleObjective(double[] angles, double scale, ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException
    {
        this(getTransformationMatrix(angles, scale, objective.getNumDimensions()), objective);
    }
    
    /**
     * @param transformationMatrix The transformation represented as an
     * augmented matrix.
     * @param objective The original objective function.
     */
    public AffineTransformedDoubleObjective(double[][] transformationMatrix, ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException
    {
        if (objective == null)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: objective is null.");
        if (transformationMatrix == null)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: transformationMatrix is null.");
        if (!Matrix.isSquare(transformationMatrix))
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: transformationMatrix is not square.");
        this.transformationMatrix = Misc.deepCopy2DArray(transformationMatrix);
        this.objective = objective;
        this.numDimensions = transformationMatrix.length;
    }
    
    /** @return A matrix representation of an affine transformation
     * in R^2 with the specified rotations and scale . */
    private static double[][] getTransformationMatrix(double[] angles, double scale, int numDimensions) throws IllegalArgumentException
    {
        if (angles == null)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: angle array is null.");
        // N choose 2 = N (N-1) / 2
        if (angles.length != numDimensions * (numDimensions - 1) / 2)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: incorrect number of angles.");

        double[][] transformationMatrix = getScaleMatrix(scale, numDimensions);

        int angleIndex = 0;
        for (int d1 = 0; d1 < numDimensions-1; d1++)
        {
            for (int d2 = d1 + 1; d2 < numDimensions; d2++)
            {
                double[][] rotMatrix = getAxisRotationMatrix(angles[angleIndex], d1, d2, numDimensions);
                transformationMatrix = Matrix.multiply(transformationMatrix, rotMatrix);
                angleIndex++;
            }
        }

        return transformationMatrix;
    }
    
    /** @return A rotation matrix for a single axis.  The two dimensions
     * define the axis of rotation. */
    private static double[][] getAxisRotationMatrix(double angle, int dimension1, int dimension2, int numDimensions) throws IllegalArgumentException
    {
        if (dimension1 >= numDimensions || dimension2 >= numDimensions)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: bad dimension.");
        if (dimension1 == dimension2)
            throw new IllegalArgumentException("AffineTransformedDoubleObjective: dimensions are equal.");
        double[][] rotationMatrix = getScaleMatrix(1.0, numDimensions);

        rotationMatrix[dimension1][dimension1] = Math.cos(angle);
        rotationMatrix[dimension1][dimension2] = -Math.sin(angle);
        rotationMatrix[dimension2][dimension1] = Math.sin(angle);
        rotationMatrix[dimension2][dimension2] = Math.cos(angle);

        return rotationMatrix;
    }
        
    /** @return A scale matrix. */
    private static double[][] getScaleMatrix(double scale, int numDimensions) //throws IllegalArgumentException
    {
        // Hmmm. A negative scale might be useful.
        //if (scale < 0)
        //    throw new IllegalArgumentException("AffineTransformedDoubleObjective: negative scale.");
        double[][] scaleMatrix = new double[numDimensions][numDimensions];

        for (int i = 0; i < scaleMatrix.length; i++)
        {
            scaleMatrix[i][i] = scale;
        }
        // All other values in the matrix are automatically 0.

        return scaleMatrix;
    }
        
    

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    public ObjectiveFunction<DoubleVectorPhenotype> getWrappedObjective() {
        return objective;
    }
    
    public double[][] getTransformationMatrix() {
        return Matrix.copy(transformationMatrix);
    }
    
    public DoubleVectorPhenotype transform(DoubleVectorPhenotype ind)
    {
        assert(ind != null);
        assert(ind.size() == getNumDimensions());
        double[] newPoint = new double[getNumDimensions()];
        for(int i = 0; i < getNumDimensions(); i++)
        {
            newPoint[i] = 0;
            for(int j = 0; j < getNumDimensions(); j++)
                newPoint[i] += ind.getElement(j) * transformationMatrix[i][j];
        }
        return new DoubleVectorPhenotype(newPoint);
    }
    
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        return objective.fitness(transform(ind));
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return objective != null
                && numDimensions > 0
                && Matrix.isSquare(transformationMatrix);
    }
    
    @Override
    public String toString()
    {
        return String.format("[%s: Dimensions=%d, Matrix=%s, Objective=%s]", this.getClass().getSimpleName(), getNumDimensions(), Arrays.deepToString(transformationMatrix), objective.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (ref == this)
            return true;
        if (!(ref instanceof AffineTransformedDoubleObjective))
            return false;
        final AffineTransformedDoubleObjective cRef = (AffineTransformedDoubleObjective) ref;
        return numDimensions == cRef.numDimensions
                && objective.equals(cRef.objective)
                && Arrays.deepEquals(transformationMatrix, cRef.transformationMatrix);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.deepHashCode(this.transformationMatrix);
        hash = 97 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    // </editor-fold>
}
