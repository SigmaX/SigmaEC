package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for FitnessComparator.
 * 
 * @author Eric O. Scott
 */
public class FitnessComparatorTest {
    private final static String BASE = "base";
    private Parameters.Builder parameters;
    
    public FitnessComparatorTest() { }
    
    @Before
    public void setUp() {
        parameters = new Parameters.Builder(new Properties());
    }

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    @Test
    public void testConstructor1() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters.build(), BASE);
        assertEquals(FitnessComparator.DEFAULT_EQUAL_IS_BETTER, sut.equalIsBetter());
        assertEquals(FitnessComparator.DEFAULT_MINIMIZE, sut.minimize());
        assertEquals(FitnessComparator.DEFAULT_DOUBLE_EQUALITY_DELTA, sut.getDoubleEqualityDelta(), 0.0000000000001);
        assertTrue(sut.repOK());
    }

    @Test
    public void testConstructor2() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), Boolean.toString(!FitnessComparator.DEFAULT_EQUAL_IS_BETTER))
                .build(), BASE);
        assertEquals(!FitnessComparator.DEFAULT_EQUAL_IS_BETTER, sut.equalIsBetter());
        assertEquals(FitnessComparator.DEFAULT_MINIMIZE, sut.minimize());
        assertEquals(FitnessComparator.DEFAULT_DOUBLE_EQUALITY_DELTA, sut.getDoubleEqualityDelta(), 0.0000000000001);
        assertTrue(sut.repOK());
    }

    @Test
    public void testConstructor3() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), Boolean.toString(!FitnessComparator.DEFAULT_MINIMIZE))
                .build(), BASE);
        assertEquals(FitnessComparator.DEFAULT_EQUAL_IS_BETTER, sut.equalIsBetter());
        assertEquals(!FitnessComparator.DEFAULT_MINIMIZE, sut.minimize());
        assertEquals(FitnessComparator.DEFAULT_DOUBLE_EQUALITY_DELTA, sut.getDoubleEqualityDelta(), 0.0000000000001);
        assertTrue(sut.repOK());
    }

    @Test
    public void testConstructor4() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "0.1")
                .build(), BASE);
        assertEquals(FitnessComparator.DEFAULT_EQUAL_IS_BETTER, sut.equalIsBetter());
        assertEquals(FitnessComparator.DEFAULT_MINIMIZE, sut.minimize());
        assertEquals(0.1, sut.getDoubleEqualityDelta(), 0.0000000000001);
        assertTrue(sut.repOK());
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructor5() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "-0.1")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor6() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "NaN")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor7() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "-Infinity")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor8() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "Infinity")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor9() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "Hello")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor10() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "Hello")
                .build(), BASE);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testConstructor11() {
        System.out.println("constructor");
        final FitnessComparator sut = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "Hello")
                .build(), BASE);
    }
    
    @Test
    public void testCopyConstructor1() {
        System.out.println("copy constructor");
        final FitnessComparator ref = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator copy = new FitnessComparator(ref, ref.minimize());
        assertEquals(ref, copy);
        assertTrue(ref.repOK());
        assertTrue(copy.repOK());
    }
    
    @Test
    public void testCopyConstructor2() {
        System.out.println("copy constructor");
        final FitnessComparator ref = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator copy = new FitnessComparator(ref, !ref.minimize());
        assertNotEquals(ref, copy);
        assertEquals(!ref.minimize(), copy.minimize());
        assertTrue(ref.repOK());
        assertTrue(copy.repOK());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Compare">
    /** Test of compare method, of class FitnessComparator. */
    @Test
    public void testCompare1() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(2.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare2() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare3() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare4() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(2.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare5() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare6() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare7() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare8() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare9() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    /** Test of compare method, of class FitnessComparator. */
    @Test
    public void testCompare10() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(2.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare11() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare12() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare13() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(2.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = -1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare14() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare15() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare16() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare17() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare18() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare19() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare20() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare21() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare22() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare23() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare24() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 0;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare25() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NaN);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare26() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NaN);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testCompare27() {
        System.out.println("compare");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NaN);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        int expResult = 1;
        int result = instance.compare(ind, ind1);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BetterThan">
    /** Test of betterThan method, of class FitnessComparator. */
    @Test
    public void testBetterThan1() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(2.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertFalse(result);
        assertTrue(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan2() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(2.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan3() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(2.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan4() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertFalse(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan5() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(1.0);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan6() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertFalse(result);
        assertTrue(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan7() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(1.0);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan8() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertFalse(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan9() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(Double.POSITIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.POSITIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertTrue(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan10() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters.build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertFalse(result);
        assertFalse(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    @Test
    public void testBetterThan11() {
        System.out.println("betterThan");
        final TestIndividual ind = new TestIndividual(Double.NEGATIVE_INFINITY);
        final TestIndividual ind1 = new TestIndividual(Double.NEGATIVE_INFINITY);
        final FitnessComparator<TestIndividual> instance = new FitnessComparator<>(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        boolean result = instance.betterThan(ind, ind1);
        assertTrue(result);
        assertTrue(instance.betterThan(ind1, ind));
        assertTrue(instance.repOK());
    }
    
    // </editor-fold>
    
    /** Test of invert method, of class FitnessComparator. */
    @Test
    public void testInvert() {
        System.out.println("invert");
        final FitnessComparator instance = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator expResult = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        final FitnessComparator inverted = instance.invert();
        assertEquals(expResult, inverted);
        assertTrue(instance.repOK());
        assertTrue(inverted.repOK());
        
        final Individual ind0 = new TestIndividual(1.0);
        final Individual ind1 = new TestIndividual(2.0);
        final Individual ind2 = new TestIndividual(Double.POSITIVE_INFINITY);
        final Individual ind3 = new TestIndividual(Double.NEGATIVE_INFINITY);
        assertEquals(instance.betterThan(ind0, ind1), !inverted.betterThan(ind0, ind1));
        assertEquals(instance.betterThan(ind0, ind2), !inverted.betterThan(ind0, ind2));
        assertEquals(instance.betterThan(ind0, ind3), !inverted.betterThan(ind0, ind3));
        assertEquals(instance.betterThan(ind0, ind0), inverted.betterThan(ind0, ind0));
        assertEquals(instance.betterThan(ind1, ind0), !inverted.betterThan(ind1, ind0));
        assertEquals(instance.betterThan(ind1, ind2), !inverted.betterThan(ind1, ind2));
        assertEquals(instance.betterThan(ind1, ind3), !inverted.betterThan(ind1, ind3));
        assertEquals(instance.betterThan(ind2, ind0), !inverted.betterThan(ind2, ind0));
        assertEquals(instance.betterThan(ind2, ind1), !inverted.betterThan(ind2, ind1));
        assertEquals(instance.betterThan(ind2, ind3), !inverted.betterThan(ind2, ind3));
    }

    /** Test of equals method, of class FitnessComparator. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        final FitnessComparator a = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator b = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator c = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_MINIMIZE), "true")
                .build(), BASE);
        final FitnessComparator d = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator e = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_EQUAL_IS_BETTER), "true")
                .build(), BASE);
        final FitnessComparator f = new FitnessComparator(parameters.build(), BASE);
        final FitnessComparator g = new FitnessComparator(parameters
                .setParameter(Parameters.push(BASE, FitnessComparator.P_DOUBLE_EQUALITY_DELTA), "0.1")
                .build(), BASE);
        final FitnessComparator h = new FitnessComparator(parameters.build(), BASE);
        
        assertEquals(a, a);
        assertEquals(b, b);
        assertEquals(c, c);
        assertEquals(d, d);
        assertEquals(e, e);
        assertEquals(f, f);
        assertEquals(g, g);
        assertEquals(h, h);
        
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(c, d);
        assertEquals(d, c);
        assertEquals(c.hashCode(), d.hashCode());
        assertEquals(e, f);
        assertEquals(f, e);
        assertEquals(e.hashCode(), f.hashCode());
        assertEquals(g, h);
        assertEquals(h, g);
        assertEquals(g.hashCode(), h.hashCode());
        
        assertNotEquals(a, c);
        assertNotEquals(a, d);
        assertNotEquals(a, e);
        assertNotEquals(a, f);
        assertNotEquals(a, g);
        assertNotEquals(a, h);
        assertNotEquals(b, c);
        assertNotEquals(b, d);
        assertNotEquals(b, e);
        assertNotEquals(b, f);
        assertNotEquals(b, g);
        assertNotEquals(b, h);
        assertNotEquals(c, a);
        assertNotEquals(c, b);
        assertNotEquals(c, e);
        assertNotEquals(c, f);
        assertNotEquals(c, g);
        assertNotEquals(c, h);
        assertNotEquals(d, a);
        assertNotEquals(d, b);
        assertNotEquals(d, e);
        assertNotEquals(d, f);
        assertNotEquals(d, g);
        assertNotEquals(d, h);
        assertNotEquals(e, a);
        assertNotEquals(e, b);
        assertNotEquals(e, c);
        assertNotEquals(e, d);
        assertNotEquals(e, g);
        assertNotEquals(e, h);
        assertNotEquals(f, a);
        assertNotEquals(f, b);
        assertNotEquals(f, c);
        assertNotEquals(f, d);
        assertNotEquals(f, g);
        assertNotEquals(f, h);
        assertNotEquals(g, a);
        assertNotEquals(g, b);
        assertNotEquals(g, c);
        assertNotEquals(g, d);
        assertNotEquals(g, e);
        assertNotEquals(g, f);
        assertNotEquals(h, a);
        assertNotEquals(h, b);
        assertNotEquals(h, c);
        assertNotEquals(h, d);
        assertNotEquals(h, e);
        assertNotEquals(h, f);
        
        assertTrue(a.repOK());
        assertTrue(b.repOK());
        assertTrue(c.repOK());
        assertTrue(d.repOK());
        assertTrue(e.repOK());
        assertTrue(f.repOK());
        assertTrue(g.repOK());
        assertTrue(h.repOK());
    }
}
