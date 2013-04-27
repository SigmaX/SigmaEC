package SigmaEC.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class MiscTest {
    
    public MiscTest()
    {
    }
    
    /** Test of containsOnlyClass method, of class Misc. */
    @Test
    public void testContainsOnlyClass() {
        System.out.println("containsOnlyClass");
        Collection intCollection = new ArrayList() {{
            add(1); add(2); add(1); add(8);
        }};
        Collection mixedCollection = new ArrayList() {{
            add(1); add(2.0); add(1); add(8.0);
        }};
        assertEquals(true, Misc.containsOnlyClass(intCollection, Integer.class));
        assertEquals(false, Misc.containsOnlyClass(mixedCollection, Integer.class));
    }

    /** Test of gaussianSample method, of class Misc. */
    @Test
    public void testGaussianSample() {
        System.out.println("gaussianSample");
        Random random = null;
        double expResult = 0.0;
        double result = Misc.gaussianSample(random);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of mean method, of class Misc. */
    @Test
    public void testMean() {
        System.out.println("mean");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = 5.05454545454;
        double result = Misc.mean(values);
        assertEquals(expResult, result, 0.00001);
    }

    /** Test of std method, of class Misc. */
    @Test
    public void testStd() {
        System.out.println("std");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double mean = 5.05454545454;
        double expResult = 9.2698302069733565;
        double result = Misc.std(values, mean);
        assertEquals(expResult, result, 0.00001);
    }

    /** Test of max method, of class Misc. */
    @Test
    public void testMax() {
        System.out.println("max");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = 22;
        double result = Misc.max(values);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of min method, of class Misc. */
    @Test
    public void testMin() {
        System.out.println("min");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = -15;
        double result = Misc.min(values);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of euclideanDistance method, of class Misc. */
    @Test
    public void testEuclideanDistance2D() {
        System.out.println("euclideanDistance (2 dimensions)");
        double[] v1 = new double[] {1, 1};
        double[] v2 = new double[] {2, 2};
        double expResult = Math.sqrt(2);
        double result = Misc.euclideanDistance(v1, v2);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of euclideanDistance method, of class Misc. */
    @Test
    public void testEuclideanDistance3D() {
        System.out.println("euclideanDistance (3 dimensions)");
        double[] v1 = new double[] {1, 1, 1};
        double[] v2 = new double[] {2, 2, 2};
        double expResult = Math.sqrt(3);
        double result = Misc.euclideanDistance(v1, v2);
        assertEquals(expResult, result, 0.0);
    }
}