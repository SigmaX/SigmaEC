package SigmaEC.util.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class VectorTest {
    
    public VectorTest() {
    }

    /** Test of euclideanDistance method, of class Misc. */
    @Test
    public void testEuclideanDistance2D() {
        System.out.println("euclideanDistance (2 dimensions)");
        double[] v1 = new double[] {1, 1};
        double[] v2 = new double[] {2, 2};
        double expResult = Math.sqrt(2);
        double result = Vector.euclideanDistance(v1, v2);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of euclideanDistance method, of class Misc. */
    @Test
    public void testEuclideanDistance3D() {
        System.out.println("euclideanDistance (3 dimensions)");
        double[] v1 = new double[] {1, 1, 1};
        double[] v2 = new double[] {2, 2, 2};
        double expResult = Math.sqrt(3);
        double result = Vector.euclideanDistance(v1, v2);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of dotProduct method, of class Misc. */
    @Test
    public void testDotProduct1() {
        System.out.println("dotProduct");
        double[] v1 = new double[] {4, .5};
        double[] v2 = new double[] {2, 8};
        double expResult = 12;
        double result = Vector.dotProduct(v1, v2);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of dotProduct method, of class Misc. */
    @Test
    public void testDotProduct2() {
        System.out.println("dotProduct (orthogonal)");
        double[] v1 = new double[] {1, 1};
        double[] v2 = new double[] {1, -1};
        double expResult = 0;
        double result = Vector.dotProduct(v1, v2);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of euclideanNorm method, of class Misc. */
    @Test
    public void testEuclideanNorm2D() {
        System.out.println("euclideanNorm (2 dimensions)");
        double[] v = new double[] {1, 1};
        double expResult = Math.sqrt(2);
        double result = Vector.euclideanNorm(v);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of euclideanNorm method, of class Misc. */
    @Test
    public void testEuclideanNorm3D() {
        System.out.println("euclideanNorm (3 dimensions)");
        double[] v = new double[] {2, 2, 2};
        double expResult = 2*Math.sqrt(3);
        double result = Vector.euclideanNorm(v);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of vectorMinus method, of class Misc. */
    @Test
    public void testVectorMinus2D() {
        System.out.println("vectorMinus (2 dimensions)");
        double[] v1 = new double[] {4, .5};
        double[] v2 = new double[] {2, 8};
        double[] expResult = new double[] {2, -7.5};
        double[] result = Vector.vectorMinus(v1, v2);
        assertArrayEquals(expResult, result, 0.0);
    }
    
    /** Test of vectorMinus method, of class Misc. */
    @Test
    public void testVectorMinus3D() {
        System.out.println("vectorMinus (3 dimensions)");
        double[] v1 = new double[] {4, .5, -2};
        double[] v2 = new double[] {2, 8, -2};
        double[] expResult = new double[] {2, -7.5, 0};
        double[] result = Vector.vectorMinus(v1, v2);
        assertArrayEquals(expResult, result, 0.0);
    }
    
    /** Test of scalarTimesVector method, of class Misc. */
    @Test
    public void testScalarTimesVector() {
        System.out.println("scalarTimesVector");
        double s = 4.5;
        double[] v = new double[] {2, 8};
        double[] expResult = new double[] {9, 36};
        double[] result = Vector.scalarTimesVector(s, v);
        assertArrayEquals(expResult, result, 0.0);
    }
    
    /** Test of pointToLineEuclideanDistance method, of class Misc. */
    @Test
    public void testPointToLineEuclideanDistance1() {
        System.out.println("pointToLineEuclideanDistance");
        double[] point = new double[] { 0, 2 };
        double[] slopeVector = new double[] {Math.sqrt(0.5), Math.sqrt(0.5)};
        double[] interceptVector = new double[] { 0, 0 };
        double expResult = Math.sqrt(2);
        double result = Vector.pointToLineEuclideanDistance(point, slopeVector, interceptVector);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of pointToLineEuclideanDistance method, of class Misc. */
    @Test
    public void testPointToLineEuclideanDistance2() {
        System.out.println("pointToLineEuclideanDistance");
        double[] point = new double[] { 0, 2 };
        double[] slopeVector = new double[] {1.0, 0};
        double[] interceptVector = new double[] { 0, 0 };
        double expResult = 2;
        double result = Vector.pointToLineEuclideanDistance(point, slopeVector, interceptVector);
        assertEquals(expResult, result, 0.0);
    }
    
    /** Test of pointToLineEuclideanDistance method, of class Misc. */
    @Test
    public void testPointToLineEuclideanDistance3() {
        System.out.println("pointToLineEuclideanDistance");
        double[] point = new double[] { 1, 2 };
        double[] slopeVector = new double[] {0, 1};
        double[] interceptVector = new double[] { 0, 0 };
        double expResult = 1;
        double result = Vector.pointToLineEuclideanDistance(point, slopeVector, interceptVector);
        assertEquals(expResult, result, 0.0);
    }
}