package SigmaEC.util.math;

import SigmaEC.represent.distance.DistanceMeasure;
import SigmaEC.represent.distance.DoubleVectorEuclideanDistanceMeasure;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class StatisticsTest {
    
    public StatisticsTest() { }
    
    @Test(expected = AssertionError.class)
    public void testNoInstantiation() throws Exception {
        System.out.println("instantiation");
        final Object o = Statistics.class.newInstance();
    }
    
    /** Test of mean method, of class Misc. */
    @Test
    public void testMean() {
        System.out.println("mean");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = 5.05454545454;
        double result = Statistics.mean(values);
        assertEquals(expResult, result, 0.00001);
    }

    /** Test of std method, of class Misc. */
    @Test
    public void testStd() {
        System.out.println("std");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double mean = 5.05454545454;
        double expResult = 9.2698302069733565;
        double result = Statistics.std(values, mean);
        assertEquals(expResult, result, 0.00001);
    }

    /** Test of best method, of class Misc. */
    @Test
    public void testMax() {
        System.out.println("max (odd index)");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = 22;
        double result = Statistics.max(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(7, Statistics.maxIndex(values));
    }

    /** Test of best method, of class Misc. */
    @Test
    public void testMaxEvenIndex() {
        System.out.println("max (even index)");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, 22, -15, 7.5, 5, 0.1 };
        double expResult = 22;
        double result = Statistics.max(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(6, Statistics.maxIndex(values));
    }

    /** Test of best method, of class Misc. */
    @Test
    public void testMaxFirst() {
        System.out.println("max (first element)");
        double[] values = new double[] { 22, 1, 9, 16, -3, 8, -15, 5, 7.5, 5, 0.1 };
        double expResult = 22;
        double result = Statistics.max(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(0, Statistics.maxIndex(values));
    }

    /** Test of best method, of class Misc. */
    @Test
    public void testMaxLast() {
        System.out.println("max (last element)");
        double[] values = new double[] { 0.1, 1, 9, 16, -3, 8, -15, 5, 7.5, 5, 22 };
        double expResult = 22;
        double result = Statistics.max(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(10, Statistics.maxIndex(values));
    }

    /** Test of worst method, of class Misc. */
    @Test
    public void testMin() {
        System.out.println("min");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, -15, 22, 7.5, 5, 0.1 };
        double expResult = -15;
        double result = Statistics.min(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(6, Statistics.minIndex(values));
    }

    /** Test of worst method, of class Misc. */
    @Test
    public void testMinFirst() {
        System.out.println("min (first element)");
        double[] values = new double[] { -15, 1, 9, 16, -3, 8, 5, 22, 7.5, 5, 0.1 };
        double expResult = -15;
        double result = Statistics.min(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(0, Statistics.minIndex(values));
    }

    /** Test of worst method, of class Misc. */
    @Test
    public void testMinLast() {
        System.out.println("min (last element)");
        double[] values = new double[] { 5, 1, 9, 16, -3, 8, 0.1, 22, 7.5, 5, -15 };
        double expResult = -15;
        double result = Statistics.min(values);
        assertEquals(expResult, result, 0.0);
        assertEquals(10, Statistics.minIndex(values));
    }
    
    @Test
    public void testDistanceMatrix() {
        System.out.println("distanceMatrix");
        final DistanceMeasure<DoubleVectorIndividual> dist = new DoubleVectorEuclideanDistanceMeasure(new Parameters.Builder(new Properties()).build(), "");
        final List<DoubleVectorIndividual> points = new ArrayList<DoubleVectorIndividual>() {{
            add(new DoubleVectorIndividual.Builder(new double[] { 0, 0, 0 }).build());
            add(new DoubleVectorIndividual.Builder(new double[] { 0, 0, 1 }).build());
            add(new DoubleVectorIndividual.Builder(new double[] { 0, 1, 0 }).build());
            add(new DoubleVectorIndividual.Builder(new double[] { 1, 0, 0 }).build());
            add(new DoubleVectorIndividual.Builder(new double[] { 1, 1, 1 }).build());
        }};
        final double[][] expectedResult = new double[][] {
            { 0, 1, 1, 1, Math.sqrt(3) },
            { 1, 0, Math.sqrt(2), Math.sqrt(2), Math.sqrt(2) },
            { 1, Math.sqrt(2), 0, Math.sqrt(2), Math.sqrt(2) },
            { 1, Math.sqrt(2), Math.sqrt(2), 0, Math.sqrt(2) },
            { Math.sqrt(3), Math.sqrt(2), Math.sqrt(2), Math.sqrt(2), 0}
        };
        final double[][] result = Statistics.distanceMatrix(points, dist);
        assertTrue(Matrix.equals(result, expectedResult, 0.00000001));
    }
}