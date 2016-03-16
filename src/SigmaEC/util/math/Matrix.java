package SigmaEC.util.math;

import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Matrix {
    
    public Matrix() throws AssertionError {
        throw new AssertionError(Matrix.class.getSimpleName() + ": Attempted to instantiate static utility class.");
    }
    
    public static boolean isSquare(final double[][] matrix) {
        assert(matrix != null);
        int d = matrix.length;
        for (double[] c : matrix)
            if (c == null || c.length != d)
                return false;
        return true;
    }
    
    public static boolean allRowsEqualLength(final double[][] matrix) {
        assert(matrix != null);
        if (matrix.length == 0)
            return true;
        final int columns = matrix[0].length;
        for (int i = 1; i < matrix.length; i++)
            if (matrix[i].length != columns)
                return false;
        return true;
    }
    
    /** @return The cross product of two matrices M1 * M2 */
    public static double[][] multiply(final double[][] M1, final double[][] M2) throws IllegalArgumentException {
        assert(M1 != null);
        assert(M2 != null);
        assert(allRowsEqualLength(M1) && allRowsEqualLength(M2));
        assert(M1[0].length == M2.length);

        int numRows = M1.length;
        int numCols = M2[0].length;
        double[][] productMatrix = new double[numRows][numCols];
        double sumProduct = 0.0;
        
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < M2[0].length; j++)
            {
                sumProduct=0.0;
                for(int k = 0; k < numCols; k++)
                {
                    sumProduct += M1[i][k] * M2[k][j];  
                }              
                productMatrix[i][j] = sumProduct;
            }
        }
                
        return productMatrix;
    }
    
    public static String prettyPrint(final double[][] matrix) {
        assert(allRowsEqualLength(matrix));
        if (matrix.length == 0)
            return "";
        final StringBuilder sb = new StringBuilder();
        final int columns = matrix[0].length;
        if (columns == 0)
            return "";
        for (int i = 0; i < matrix.length; i++) {
            sb.append(matrix[i][0]);
            for (int j = 1; j < columns; j++) {
                sb.append(", ").append(matrix[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static double meanElementValue(final double[][] matrix) {
        assert(matrix != null);
        assert(allRowsEqualLength(matrix));
        final int columns = matrix[0].length;
        double sum = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < columns; j++) {
                sum += matrix[i][j];
            }
        }
        return sum / (matrix.length * columns);
    }
    
    /** Standard deviation of the values of all elements (with Bessel's correction). */
    public static double stdElementValue(final double[][] matrix) {
        assert(matrix != null);
        assert(allRowsEqualLength(matrix));
        final int columns = matrix[0].length;
        final double mean = meanElementValue(matrix);
        double sumSqrDiff = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < columns; j++) {
                sumSqrDiff += Math.pow(matrix[i][j] - mean, 2);
            }
        }
        return Math.sqrt(sumSqrDiff / (matrix.length * columns - 1));
    }
    
    public static double[][] copy(double[][] matrix) {
        assert(matrix != null);
        final double[][] newMatrix = new double[matrix.length][];
        for (int i = 0; i < matrix.length; i++)
            newMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        return newMatrix;
    }
    
    public static boolean equals(double[][] matrix1, double[][] matrix2, double precision) {
        assert(matrix1 != null);
        assert(matrix2 != null);
        if (matrix1.length != matrix2.length)
            return false;
        if (matrix1.length == 0)
            return true;
        if (matrix1[0].length != matrix2[0].length)
            return false;
        
        for (int i = 0; i < matrix1.length; i++)
            for (int j = 0; j < matrix1[i].length; j++)
                if (!Misc.doubleEquals(matrix1[i][j], matrix2[i][j], precision))
                    return false;
        return true;
    }
}
