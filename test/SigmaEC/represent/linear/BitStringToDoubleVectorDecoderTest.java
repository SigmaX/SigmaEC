package SigmaEC.represent.linear;

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
    
    final BitStringIndividual emptyInd = new BitStringIndividual.Builder(new ArrayList<BitGene>()).build();
    final BitStringIndividual goodInd = new BitStringIndividual.Builder(new ArrayList<BitGene>() {{
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(true)); // 1011, Gray: 1110
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(false)); // 1010, Gray: 1111
        add(new BitGene(false)); add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); // 0111, Gray: 0100
        add(new BitGene(true)); add(new BitGene(false)); add(new BitGene(false)); add(new BitGene(false)); // 1000, Gray: 1100
        add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); add(new BitGene(true)); // 1111, Gray: 1000
    }}).build();
        
    public BitStringToDoubleVectorDecoderTest() { }
    
    @Before
    public void setUp() {
        properties = new Properties();
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MIN), "0.0");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "15.0");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_GRAY_CODE), "false");
    }

    @Test
    public void testBuilder() {
        System.out.println("constructor");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        assertEquals(sut.getNumDimensions(), 5);
        assertEquals(sut.getNumBitsPerDimension(), 4);
        assertTrue(sut.repOK());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderIAE1() {
        System.out.println("constructor (dimensions <= 0)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "0");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderIAE2() {
        System.out.println("constructor (bits <= 0)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "0");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Endian tests">
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode() {
        System.out.println("decode (endian)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "1.875");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 1.375, 1.25, 0.875, 1.0, 1.875 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode1() {
        System.out.println("decode (endian)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "3.75");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 2.75, 2.5, 1.75, 2.0, 3.75 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode2() {
        System.out.println("decode (endian)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "31");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 23.0, 9.0, 28.0, 15.0}).build();
        //fail("Why does this not go to 31?");
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode3() {
        System.out.println("decode (endian)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MIN), "-15");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "16");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 8.0, -6.0, 13.0, 0.0}).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Endian tests">
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode4() {
        System.out.println("decode (gray)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "1.875");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_GRAY_CODE), "true");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 1.75, 1.875, 0.5, 1.5, 1.0 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode5() {
        System.out.println("decode (gray)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "3.75");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_GRAY_CODE), "true");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 3.5, 3.75, 1.0, 3.0, 2.0 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode6() {
        System.out.println("decode (gray)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "31");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_GRAY_CODE), "true");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 28.0, 13.0, 18.0, 8.0 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    
    /** Test of decode method, of class BitStringToDoubleVectorDecoder. */
    @Test
    public void testDecode7() {
        System.out.println("decode (gray)");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_DIMENSIONS), "4");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_NUM_BITS_PER_DIMENSION), "5");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MIN), "-15");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_MAX), "16");
        properties.setProperty(Parameters.push(BASE, BitStringToDoubleVectorDecoder.P_GRAY_CODE), "true");
        final BitStringToDoubleVectorDecoder sut = new BitStringToDoubleVectorDecoder(new Parameters(properties), BASE);
        final DoubleVectorIndividual expResult = new DoubleVectorIndividual.Builder(new double[] { 13.0, -2.0, 3.0, -7.0 }).build();
        final DoubleVectorIndividual result = sut.decode(goodInd);
        assertEquals(expResult.getGenome(), result.getGenome());
        assertTrue(sut.repOK());
    }
    // </editor-fold>
    
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
        final String expResult = String.format("[BitStringToDoubleVectorDecoder: numDimensions=%d, numBitsPerDimension=%d, min=%f, max=%f]", 5, 4, 0.0, 15.0);
        final String result = sut.toString();
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
}