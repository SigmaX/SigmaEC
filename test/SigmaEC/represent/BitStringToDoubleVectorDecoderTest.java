package SigmaEC.represent;

import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringToDoubleVectorDecoderTest {
    final static String BASE = "base";
    Properties properties;
    
    final BitStringIndividual emptyInd = new BitStringIndividual(new ArrayList<BitGene>());
    final BitStringIndividual goodInd = new BitStringIndividual(new ArrayList<BitGene>() {{
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(true)); // 1011
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(false)); // 1010
        add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); // 0111
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(false)); add(new BitGene(false)); // 1000
        add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); // 1111
    }});
        
    public BitStringToDoubleVectorDecoderTest() { }
    
    @Before
    public void setUp() {
        properties = new Properties();
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_LOWEST_SIGNIFICANCE), "-3");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MIN), "0.0");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "15.0");
    }

    @Test
    public void testBuilder() {
        System.out.println("builder");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        assertEquals(sut.getNumDimensions(), 5);
        assertEquals(sut.getNumBitsPerDimension(), 4);
        assertEquals(sut.getLowestSignificance(), -3);
        assertTrue(sut.repOK());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderIAE1() {
        System.out.println("builder (dimensions <= 0)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "0");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderIAE2() {
        System.out.println("builder (bits <= 0)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "0");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode() {
        System.out.println("decode");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual(new double[] { 1.625, 0.625, 1.75, 0.125, 1.875 });
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode1() {
        System.out.println("decode");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_LOWEST_SIGNIFICANCE), "-2");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual(new double[] { 3.25, 1.25, 3.5, 0.25, 3.75 });
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode2() {
        System.out.println("decode");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_LOWEST_SIGNIFICANCE), "0");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "31");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual(new double[] { 29.0, 18.0, 7.0, 30.0});
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode3() {
        System.out.println("decode");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_LOWEST_SIGNIFICANCE), "0");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MIN), "-15");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "16");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual(new double[] { 14.0, 3.0, -8.0, 15.0});
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of equals method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        final BitStringToDoubleVectorDecoder sut1 = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final BitStringToDoubleVectorDecoder sut2 = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "1");
        final BitStringToDoubleVectorDecoder sut3 = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "1");
        final BitStringToDoubleVectorDecoder sut4 = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_LOWEST_SIGNIFICANCE), "1");
        final BitStringToDoubleVectorDecoder sut5 = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        
        assertTrue(sut1.equals(sut2));
        assertTrue(sut2.equals(sut1));
        assertFalse(sut3.equals(sut1));
        assertFalse(sut3.equals(sut2));
        assertFalse(sut1.equals(sut3));
        assertFalse(sut2.equals(sut3));
        
        assertFalse(sut4.equals(sut1));
        assertFalse(sut4.equals(sut2));
        assertFalse(sut4.equals(sut3));
        assertFalse(sut1.equals(sut4));
        assertFalse(sut2.equals(sut4));
        assertFalse(sut3.equals(sut4));
        
        assertFalse(sut5.equals(sut1));
        assertFalse(sut5.equals(sut2));
        assertFalse(sut5.equals(sut3));
        assertFalse(sut5.equals(sut4));
        assertFalse(sut1.equals(sut5));
        assertFalse(sut2.equals(sut5));
        assertFalse(sut3.equals(sut5));
        assertFalse(sut4.equals(sut5));
        
        assertTrue(sut1.repOK());
        assertTrue(sut2.repOK());
        assertTrue(sut3.repOK());
        assertTrue(sut4.repOK());
        assertTrue(sut5.repOK());
    }

    /** Test of toString method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testToString() {
        System.out.println("toString");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final String expResult = String.format("[BitStringToDoubleVectorDecoder: numDimensions=%d, numBitsPerDimension=%d, lowestSignificance=%d, min=%f, max=%f]", 5, 4, -3, 0.0, 15.0);
        final String result = sut.toString();
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
}