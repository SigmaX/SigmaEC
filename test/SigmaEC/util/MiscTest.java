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
    public void testContainsOnlyClass1() {
        System.out.println("containsOnlyClass");
        final Collection intCollection = new ArrayList() {{
            add(1); add(2); add(1); add(8);
        }};
        assertTrue(Misc.containsOnlyClass(intCollection, Integer.class));
        assertFalse(Misc.containsOnlyClass(intCollection, Double.class));
    }
    
    /** Test of containsOnlyClass method, of class Misc. */
    @Test
    public void testContainsOnlyClass2() {
        System.out.println("containsOnlyClass");
        final Collection mixedCollection = new ArrayList() {{
            add(1); add(2.0); add(1); add(8.0);
        }};
        assertFalse(Misc.containsOnlyClass(mixedCollection, Integer.class));
        assertFalse(Misc.containsOnlyClass(mixedCollection, Double.class));
    }
    
    /** Test of containsOnlyClass method, of class Misc. */
    @Test
    public void testContainsOnlyClass3() {
        System.out.println("containsOnlyClass");
        final Collection collection = new ArrayList() {{ }};
        assertTrue(Misc.containsOnlyClass(collection, Integer.class));
        assertTrue(Misc.containsOnlyClass(collection, Double.class));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls1() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{ }};
        assertFalse(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls2() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{
            add(1); add(2.0); add(1); add(8.0);
        }};
        assertFalse(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls3() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{
            add(1); add(2.0); add(null); add(8.0);
        }};
        assertTrue(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls4() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{
            add(null); add(2.0); add(1); add(8.0);
        }};
        assertTrue(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls5() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{
            add(1); add(2.0); add(1); add(null);
        }};
        assertTrue(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNulls6() {
        System.out.println("containsNulls");
        final Collection collection = new ArrayList() {{
            add(null);
        }};
        assertTrue(Misc.containsNulls(collection));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNullsA1() {
        System.out.println("containsNulls (array)");
        final IDoublePoint[] array = new IDoublePoint[] { };
        assertFalse(Misc.containsNulls(array));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNullsA2() {
        System.out.println("containsNulls (array)");
        final IDoublePoint[] array = new IDoublePoint[] { null };
        assertTrue(Misc.containsNulls(array));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNullsA3() {
        System.out.println("containsNulls (array)");
        final IDoublePoint[] array = new IDoublePoint[] { new IDoublePoint(0.0, 0.0)};
        assertFalse(Misc.containsNulls(array));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNullsA4() {
        System.out.println("containsNulls (array)");
        final IDoublePoint[] array = new IDoublePoint[] { new IDoublePoint(0.0, 0.0), null, new IDoublePoint(1.0, -100.0) };
        assertTrue(Misc.containsNulls(array));
    }
    
    /** Test of containsNulls method, of class Misc. */
    @Test
    public void testContainsNullsA5() {
        System.out.println("containsNulls (array)");
        final IDoublePoint[] array = new IDoublePoint[] { new IDoublePoint(0.0, 0.0), new IDoublePoint(1.0, -100.0), null };
        assertTrue(Misc.containsNulls(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs1() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { };
        assertFalse(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs2() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { 0.8, 100.0, 34523453245.0, -3452345.123 };
        assertFalse(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs3() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { 0.8, Double.POSITIVE_INFINITY, 34523453245.0, -3452345.123 };
        assertFalse(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs4() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { 0.8, Double.NEGATIVE_INFINITY, 34523453245.0, -3452345.123 };
        assertFalse(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs5() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { 0.8, Double.NaN, 34523453245.0, -3452345.123 };
        assertTrue(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs6() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { Double.NaN, 0.8, 34523453245.0, -3452345.123 };
        assertTrue(Misc.containsNaNs(array));
    }
    
    /** Test of containsNaNs method, of class Misc. */
    @Test
    public void testContainsNaNs7() {
        System.out.println("containsNaNs");
        final double[] array = new double[] { 0.8, 34523453245.0, -3452345.123, Double.NaN };
        assertTrue(Misc.containsNaNs(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued1() {
        System.out.println("finiteValued");
        final double[] array = new double[] { };
        assertTrue(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued2() {
        System.out.println("finiteValued");
        final double[] array = new double[] { 0.8, 100.0, 34523453245.0, -3452345.123 };
        assertTrue(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued3() {
        System.out.println("finiteValued");
        final double[] array = new double[] { 0.8, Double.POSITIVE_INFINITY, 34523453245.0, -3452345.123 };
        assertFalse(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued4() {
        System.out.println("finiteValued");
        final double[] array = new double[] { 0.8, Double.NEGATIVE_INFINITY, 34523453245.0, -3452345.123 };
        assertFalse(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued5() {
        System.out.println("finiteValued");
        final double[] array = new double[] { 0.8, Double.NaN, 34523453245.0, -3452345.123 };
        assertFalse(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued6() {
        System.out.println("finiteValued");
        final double[] array = new double[] { Double.NaN, 0.8, 34523453245.0, -3452345.123 };
        assertFalse(Misc.finiteValued(array));
    }
    
    /** Test of finiteValued method, of class Misc. */
    @Test
    public void testFiniteValued7() {
        System.out.println("finiteValued");
        final double[] array = new double[] { 0.8, 34523453245.0, -3452345.123, Double.NaN };
        assertFalse(Misc.finiteValued(array));
    }
    
    /** Test of deepCopy2DArray method, of class Misc. */
    @Test
    public void testDeepCopy2DArray1() {
        System.out.println("deepCopy2DArray");
        final double[][] array = new double[][] { 
            new double[] { 0.8, 34523453245.0 },
            new double[] {-3452345.123, Double.NaN }
        };
        final double[][] copy = Misc.deepCopy2DArray(array);
        assertFalse(copy == null);
        assertEquals(array.length, copy.length);
        for (int i = 0; i < array.length; i++)
            assertArrayEquals(array[i], copy[i], 0.000001);
        array[0][0] = Double.NEGATIVE_INFINITY;
        assertEquals(0.8, copy[0][0], 0.000001);
    }
    
    /** Test of deepCopy2DArray method, of class Misc. */
    @Test
    public void testDeepCopy2DArray2() {
        System.out.println("deepCopy2DArray");
        final double[][] array = new double[][] { 
            new double[] { 0.8, 34523453245.0 }
        };
        final double[][] copy = Misc.deepCopy2DArray(array);
        assertFalse(copy == null);
        assertEquals(array.length, copy.length);
        for (int i = 0; i < array.length; i++)
            assertArrayEquals(array[i], copy[i], 0.000001);
        array[0][0] = Double.NEGATIVE_INFINITY;
        assertEquals(0.8, copy[0][0], 0.000001);
    }
    
    /** Test of deepCopy2DArray method, of class Misc. */
    @Test
    public void testDeepCopy2DArray3() {
        System.out.println("deepCopy2DArray");
        final double[][] array = new double[][] {};
        final double[][] copy = Misc.deepCopy2DArray(array);
        assertFalse(copy == null);
        assertEquals(0, copy.length);
    }
    
    /** Test of boundsOK method, of class Misc. */
    @Test
    public void testBoundsOK1() {
        System.out.println("boundsOK");
        final IDoublePoint[] bounds = new IDoublePoint[] {
            new IDoublePoint(0, 1),
            new IDoublePoint(-15.0, 12.0),
            new IDoublePoint(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        };
        assertTrue(Misc.boundsOK(bounds));
    }
    
    /** Test of boundsOK method, of class Misc. */
    @Test
    public void testBoundsOK2() {
        System.out.println("boundsOK");
        final IDoublePoint[] bounds = new IDoublePoint[] { };
        assertTrue(Misc.boundsOK(bounds));
    }
    
    /** Test of boundsOK method, of class Misc. */
    @Test
    public void testBoundsOK3() {
        System.out.println("boundsOK");
        final IDoublePoint[] bounds = new IDoublePoint[] {
            new IDoublePoint(1, 0),
            new IDoublePoint(-15.0, 12.0),
            new IDoublePoint(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        };
        assertFalse(Misc.boundsOK(bounds));
    }
    
    /** Test of boundsOK method, of class Misc. */
    @Test
    public void testBoundsOK4() {
        System.out.println("boundsOK");
        final IDoublePoint[] bounds = new IDoublePoint[] {
            new IDoublePoint(1, 0),
            new IDoublePoint(12.0, -15.0),
            new IDoublePoint(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
        };
        assertFalse(Misc.boundsOK(bounds));
    }
    
    /** Test of boundsOK method, of class Misc. */
    @Test
    public void testBoundsOK5() {
        System.out.println("boundsOK");
        final IDoublePoint[] bounds = new IDoublePoint[] {
            new IDoublePoint(1, 0),
            new IDoublePoint(12.0, -15.0),
            new IDoublePoint(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
        };
        assertFalse(Misc.boundsOK(bounds));
    }
    
    @Test
    public void testGaussianSample() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testDoubleEquals1() {
        System.out.println("doubleEquals");
        final double a = Math.sqrt(2);
        final double b = Math.sqrt(3)*Math.sqrt(2)/Math.sqrt(3);
        assert(a != b);
        assertTrue(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals2() {
        System.out.println("doubleEquals");
        final double a = 1;
        final double b = 2;
        assertFalse(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals3() {
        System.out.println("doubleEquals");
        final double a = 0.00001;
        final double b = 0.00001;
        assertTrue(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals4() {
        System.out.println("doubleEquals");
        final double a = 0.000000000001;
        final double b = 0.000000000002;
        assertTrue(Misc.doubleEquals(a, b));
        assertFalse(Misc.doubleEquals(a, b, 0.0000000000001));
    }
    
    @Test
    public void testDoubleEquals5() {
        System.out.println("doubleEquals");
        final double a = Double.POSITIVE_INFINITY;
        final double b = 10.0;
        assertFalse(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals6() {
        System.out.println("doubleEquals");
        final double a = Double.POSITIVE_INFINITY;
        final double b = Double.POSITIVE_INFINITY;
        assert(a == b);
        assertTrue(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals7() {
        System.out.println("doubleEquals");
        final double a = Double.NEGATIVE_INFINITY;
        final double b = Double.NEGATIVE_INFINITY;
        assert(a == b);
        assertTrue(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleEquals8() {
        System.out.println("doubleEquals");
        final double a = Double.NEGATIVE_INFINITY;
        final double b = Double.POSITIVE_INFINITY;
        assert(a != b);
        assertFalse(Misc.doubleEquals(a, b));
    }
    
    @Test
    public void testDoubleArrayEquals() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testListOfDoubleArrayEquals() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testAllFinite() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testOpenFile() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testPrepend() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testPrependA() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testAllElementsHaveDimension() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testIsNumber() {
        fail("This test case is a prototype.");
    }
    
    @Test
    public void testIn() {
        fail("This test case is a prototype.");
    }
    
}