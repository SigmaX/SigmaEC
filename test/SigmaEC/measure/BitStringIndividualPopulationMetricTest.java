package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.linear.BitStringIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.test.TestInitializer;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class BitStringIndividualPopulationMetricTest {
    private final static String BASE = "base";
    private Parameters.Builder parameters;
    
    public BitStringIndividualPopulationMetricTest() {
    }
    
    @Before
    public void setUp() {
        parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY), "true")
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BITS), "5")
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_FITNESS_COMPARATOR), "SigmaEC.select.FitnessComparator");
    }

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    @Test
    public void testConstructor1() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        assertTrue(sut.bestOnly());
        assertEquals(5, sut.numBits());
        assertTrue(sut.getFitnessComparator().isDefined());
        final FitnessComparator comparator = new FitnessComparator(parameters.build(), Parameters.push(BASE, BitStringIndividualPopulationMetric.P_FITNESS_COMPARATOR));
        assertEquals(comparator, sut.getFitnessComparator().get());
        assertTrue(sut.repOK());
    }

    @Test
    public void testConstructor2() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY), "false")
                .build(), BASE);
        assertFalse(sut.bestOnly());
        assertEquals(5, sut.numBits());
        assertFalse(sut.getFitnessComparator().isDefined());
        assertTrue(sut.repOK());
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructor3() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY))
                .build(), BASE);
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructor4() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BITS))
                .build(), BASE);
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructor5() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_FITNESS_COMPARATOR))
                .clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY))
                .build(), BASE);
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructor6() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_FITNESS_COMPARATOR))
                .build(), BASE);
    }

    @Test (expected = IllegalStateException.class)
    public void testConstructor7() {
        System.out.println("constructor");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BITS), "-1")
                .build(), BASE);
    }
    // </editor-fold>
    
    /** Test of measurePopulation method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testMeasurePopulation1() {
        System.out.println("measurePopulation");
        final Population<BitStringIndividual> population = new Population<>(2, new TestInitializer(0));
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        final int run = 3;
        final int generation = 8;
        
        assertTrue(sut.repOK());
        final MultipleStringMeasurement result = sut.measurePopulation(run, generation, population);
        final StringBuilder expectedResult = new StringBuilder()
                .append(String.format("3, 8, 0, 2, %f, 0, 1, 1, 1, 1\n", 1.0))
                .append(String.format("3, 8, 1, 14, %f, 0, 1, 1, 1, 1", 1.0));
        
        assertEquals(expectedResult.toString(), result.toString());
        assertTrue(sut.repOK());
    }
    
    /** Test of measurePopulation method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testMeasurePopulation2() {
        System.out.println("measurePopulation");
        final Population<BitStringIndividual> population = new Population<>(2, new TestInitializer(0));
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY), "false")
                .build(), BASE);
        final int run = 3;
        final int generation = 8;
        final long firstID = population.getSubpopulation(0).get(0).getID();
        
        assertTrue(sut.repOK());
        final MultipleStringMeasurement result = sut.measurePopulation(run, generation, population);
        final StringBuilder expectedResult = new StringBuilder()
                .append(String.format("3, 8, 0, %d, %f, 0, 1, 1, 0, 0\n", firstID, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 0, 0, 1, 1\n", firstID + 1, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 1, 1, 1, 1\n", firstID + 2, 1.0))
                .append(String.format("3, 8, 0, %d, %f, 1, 1, 0, 1, 0\n", firstID + 3, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 0, 1, 0, 0\n", firstID + 4, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 1, 1, 0, 1, 1\n", firstID + 5, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 1, 1, 1, 0, 0\n", firstID + 6, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 1, 1, 0, 0\n", firstID + 7, 0.9))
                .append(String.format("3, 8, 0, %d, %f, 0, 0, 1, 0, 0\n", firstID + 8, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 1, 0, 1, 1, 0\n", firstID + 9, 0.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 0, 1, 1, 0\n", firstID + 10, -1.0))
                .append(String.format("3, 8, 0, %d, %f, 0, 0, 1, 0, 1\n", firstID + 11, 0.0))
                
                .append(String.format("3, 8, 1, %d, %f, 0, 1, 1, 0, 0\n", firstID + 12, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 0, 0, 1, 1\n", firstID + 13, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 1, 1, 1, 1\n", firstID + 14, 1.0))
                .append(String.format("3, 8, 1, %d, %f, 1, 1, 0, 1, 0\n", firstID + 15, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 0, 1, 0, 0\n", firstID + 16, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 1, 1, 0, 1, 1\n", firstID + 17, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 1, 1, 1, 0, 0\n", firstID + 18, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 1, 1, 0, 0\n", firstID + 19, 0.9))
                .append(String.format("3, 8, 1, %d, %f, 0, 0, 1, 0, 0\n", firstID + 20, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 1, 0, 1, 1, 0\n", firstID + 21, 0.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 0, 1, 1, 0\n", firstID + 22, -1.0))
                .append(String.format("3, 8, 1, %d, %f, 0, 0, 1, 0, 1", firstID + 23, 0.0));
        
        assertEquals(expectedResult.toString(), result.toString());
        assertTrue(sut.repOK());
    }

    /** Test of csvHeader method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testCsvHeader1() {
        System.out.println("csvHeader");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        String expResult = "run, generation, subpopulation, individualID, fitness, V0, V1, V2, V3, V4";
        String result = sut.csvHeader();
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of csvHeader method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testCsvHeader2() {
        System.out.println("csvHeader");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters
                .setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BITS), "1")
                .build(), BASE);
        String expResult = "run, generation, subpopulation, individualID, fitness, V0";
        String result = sut.csvHeader();
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of reset method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testReset() {
        System.out.println("reset");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        sut.reset();
        assertTrue(sut.repOK());
    }

    /** Test of flush method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testFlush() {
        System.out.println("flush");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        sut.flush();
        assertTrue(sut.repOK());
    }

    /** Test of close method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testClose() {
        System.out.println("close");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        sut.close();
        assertTrue(sut.repOK());
    }

    /** Test of equals method, of class BitStringIndividualPopulationMetric. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut1 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut2 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        parameters = parameters.setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BITS), "6");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut3 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut4 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        parameters = parameters.setParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_BEST_ONLY), "false");
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut5 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        parameters = parameters.clearParameter(Parameters.push(BASE, BitStringIndividualPopulationMetric.P_FITNESS_COMPARATOR));
        final BitStringIndividualPopulationMetric<BitStringIndividual> sut6 = new BitStringIndividualPopulationMetric<>(parameters.build(), BASE);
        
        assertEquals(sut1, sut1);
        assertEquals(sut2, sut2);
        assertEquals(sut3, sut3);
        assertEquals(sut4, sut4);
        assertEquals(sut5, sut5);
        assertEquals(sut6, sut6);
        
        assertEquals(sut1, sut2);
        assertEquals(sut2, sut1);
        assertEquals(sut1.hashCode(), sut2.hashCode());
        assertEquals(sut3, sut4);
        assertEquals(sut4, sut3);
        assertEquals(sut3.hashCode(), sut4.hashCode());
        assertEquals(sut5, sut6);
        assertEquals(sut6, sut5);
        assertEquals(sut5.hashCode(), sut6.hashCode());
        
        assertNotEquals(sut1, sut3);
        assertNotEquals(sut1, sut4);
        assertNotEquals(sut1, sut5);
        assertNotEquals(sut1, sut6);
        assertNotEquals(sut2, sut3);
        assertNotEquals(sut2, sut4);
        assertNotEquals(sut2, sut5);
        assertNotEquals(sut2, sut6);
        assertNotEquals(sut3, sut1);
        assertNotEquals(sut3, sut2);
        assertNotEquals(sut3, sut5);
        assertNotEquals(sut3, sut6);
        assertNotEquals(sut4, sut1);
        assertNotEquals(sut4, sut2);
        assertNotEquals(sut4, sut5);
        assertNotEquals(sut4, sut6);
        assertNotEquals(sut5, sut1);
        assertNotEquals(sut5, sut2);
        assertNotEquals(sut5, sut3);
        assertNotEquals(sut5, sut4);
        assertNotEquals(sut6, sut1);
        assertNotEquals(sut6, sut2);
        assertNotEquals(sut6, sut3);
        assertNotEquals(sut6, sut4);
        
        assertNotEquals(sut1, new BitStringIndividual.Builder(new boolean[] { true }));
        
        assertTrue(sut1.repOK());
        assertTrue(sut2.repOK());
        assertTrue(sut3.repOK());
        assertTrue(sut4.repOK());
        assertTrue(sut5.repOK());
        assertTrue(sut6.repOK());
    }
    
}
