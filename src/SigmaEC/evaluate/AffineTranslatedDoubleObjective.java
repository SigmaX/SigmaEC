package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 * A decorator that applies an affine transformation to an ObjectiveFunction
 * over R^n.
 * 
 * @author Eric 'Siggy' Scott
 */
public class AffineTranslatedDoubleObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final double[][] transformationMatrix;
    private final ObjectiveFunction<DoubleVectorPhenotype> objective;
    
    /** Convenience constructor for creating affine transformation in R^2.
     * @param angle The angle by which the function will be rotated.
     * @param offset The vector by which the function will be offset.
     * @param objective The original objective function.
     */
    public AffineTranslatedDoubleObjective(double angle, IDoublePoint offset, ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException
    {
        this(get2DTransformationMatrix(angle, offset), objective);
    }
    
    /**
     * @param transformationMatrix The transformation represented as an
     * augmented matrix.
     * @param objective The original objective function.
     */
    public AffineTranslatedDoubleObjective(double[][] transformationMatrix, ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException
    {
        if (objective == null)
            throw new IllegalArgumentException("AffineTranslatedDoubleObjective: objective is null.");
        if (transformationMatrix == null)
            throw new IllegalArgumentException("AffineTranslatedDoubleObjective: transformationMatrix is null.");
        if (!isMatrixSquare(transformationMatrix))
            throw new IllegalArgumentException("AffineTranslatedDoubleObjective: transformationMatrix is not square.");
        this.transformationMatrix = Misc.deepCopy2DArray(transformationMatrix);
        this.objective = objective;
    }
    
    /** @return An augmented matrix representation of an affine transformation
     * in R^2 with the specified rotation and translation. */
    private static double[][] get2DTransformationMatrix(double angle, IDoublePoint offset) throws IllegalArgumentException
    {
        if (offset == null)
            throw new IllegalArgumentException("AffineTranslatedDoubleObjective: offset is null.");
        double[][] transformationMatrix = new double[3][3];
        // Rotation
        transformationMatrix[0][0] = Math.cos(angle);
        transformationMatrix[1][0] = -Math.sin(angle);
        transformationMatrix[0][1] = Math.sin(angle);
        transformationMatrix[1][1] = Math.cos(angle);
        transformationMatrix[2][0] = 0;
        transformationMatrix[2][1] = 0;
        
        // Translation
        transformationMatrix[0][2] = offset.x;
        transformationMatrix[1][2] = offset.y;
        
        // Scaling
        transformationMatrix[2][2] = 1;
        assert(isMatrixSquare(transformationMatrix));
        return transformationMatrix;
    }
    
    @Override
    public int getNumDimensions()
    {
        return transformationMatrix.length - 1;
    }
    
    public DoubleVectorPhenotype transform(DoubleVectorPhenotype ind)
    {
        assert(ind != null);
        assert(ind.size() == getNumDimensions());
        double[] newPoint = new double[getNumDimensions()];
        for(int i = 0; i < getNumDimensions(); i++)
        {
            newPoint[i] = 0;
            for(int j = 0; j < getNumDimensions() + 1; j++)
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
                && isMatrixSquare(transformationMatrix);
    }
    
    private static boolean isMatrixSquare(double[][] matrix)
    {
        int d = matrix.length;
        for (double[] c : matrix)
            if (c == null || c.length != d)
                return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return String.format("[AffineTranslatedDoubleObjective: Dimensions=%d, Matrix=%s, Objective=%s]", getNumDimensions(), Arrays.deepToString(transformationMatrix), objective.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (ref == this)
            return true;
        if (!(ref instanceof AffineTranslatedDoubleObjective))
            return false;
        AffineTranslatedDoubleObjective cRef = (AffineTranslatedDoubleObjective) ref;
        return objective.equals(cRef.objective)
                && Arrays.deepEquals(transformationMatrix, cRef.transformationMatrix);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 67 * hash + Arrays.deepHashCode(this.transformationMatrix);
        hash = 67 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
}
