package SigmaEC.represent.cgp;

import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class CGPParametersTest {
    private final static String BASE = "base";
    
    public CGPParametersTest() {
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_INPUTS), "2")
                .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_OUTPUTS), "1")
                .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_LAYERS), "2")
                .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_NODES_PER_LAYER), "2")
                .setParameter(Parameters.push(BASE, CGPParameters.P_MAX_ARITY), "2")
                .setParameter(Parameters.push(BASE, CGPParameters.P_LEVELS_BACK), "1")
                .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_PRIMITIVES), "4");
    }

    /** Test of getNumDimensions method, of class CGPParameters. */
    @Test
    public void testGetNumDimensions() {
        System.out.println("getNumDimensions");
        final CGPParameters instance = new CGPParameters(getParams().build(), BASE);
        final int expResult = 13;
        final int result = instance.getNumDimensions();
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of getFunctionGeneForNode method, of class CGPParameters. */
    @Test
    public void testGetFunctionGeneForNode() {
        System.out.println("getFunctionGeneForNode");
        final CGPParameters instance = new CGPParameters(getParams().build(), BASE);
        assertEquals(0, instance.getFunctionGeneForNode(0, 0));
        assertEquals(3, instance.getFunctionGeneForNode(0, 1));
        assertEquals(6, instance.getFunctionGeneForNode(1, 0));
        assertEquals(9, instance.getFunctionGeneForNode(1, 1));
        assertTrue(instance.repOK());
    }

    /** Test of getInputGeneForNode method, of class CGPParameters. */
    @Test
    public void testGetInputGeneForNode() {
        System.out.println("getInputGeneForNode");
        final CGPParameters instance = new CGPParameters(getParams().build(), BASE);
        assertEquals(1, instance.getInputGeneForNode(0, 0, 0));
        assertEquals(2, instance.getInputGeneForNode(0, 0, 1));
        assertEquals(4, instance.getInputGeneForNode(0, 1, 0));
        assertEquals(5, instance.getInputGeneForNode(0, 1, 1));
        assertEquals(7, instance.getInputGeneForNode(1, 0, 0));
        assertEquals(8, instance.getInputGeneForNode(1, 0, 1));
        assertEquals(10, instance.getInputGeneForNode(1, 1, 0));
        assertEquals(11, instance.getInputGeneForNode(1, 1, 1));
        assertTrue(instance.repOK());
    }

    /** Test of getLayerForGene method, of class CGPParameters. */
    @Test
    public void testGetLayerForGene() {
        System.out.println("getLayerForGene");
        final CGPParameters instance = new CGPParameters(getParams().build(), BASE);
        assertEquals(0, instance.getLayerForGene(0));
        assertEquals(0, instance.getLayerForGene(1));
        assertEquals(0, instance.getLayerForGene(2));
        assertEquals(0, instance.getLayerForGene(3));
        assertEquals(0, instance.getLayerForGene(4));
        assertEquals(0, instance.getLayerForGene(5));
        assertEquals(1, instance.getLayerForGene(6));
        assertEquals(1, instance.getLayerForGene(7));
        assertEquals(1, instance.getLayerForGene(8));
        assertEquals(1, instance.getLayerForGene(9));
        assertEquals(1, instance.getLayerForGene(10));
        assertEquals(1, instance.getLayerForGene(11));
        assertTrue(instance.repOK());
    }

    /** Test of getNodeForGene method, of class CGPParameters. */
    @Test
    public void testGetNodeForGene() {
        System.out.println("getNodeForGene");
        final CGPParameters instance = new CGPParameters(getParams().build(), BASE);
        assertEquals(0, instance.getNodeForGene(0));
        assertEquals(0, instance.getNodeForGene(1));
        assertEquals(0, instance.getNodeForGene(2));
        assertEquals(1, instance.getNodeForGene(3));
        assertEquals(1, instance.getNodeForGene(4));
        assertEquals(1, instance.getNodeForGene(5));
        assertEquals(0, instance.getNodeForGene(6));
        assertEquals(0, instance.getNodeForGene(7));
        assertEquals(0, instance.getNodeForGene(8));
        assertEquals(1, instance.getNodeForGene(9));
        assertEquals(1, instance.getNodeForGene(10));
        assertEquals(1, instance.getNodeForGene(11));
        assertTrue(instance.repOK());
    }

    /** Test of getMinBounds method, of class CGPParameters. */
    @Test
    public void testGetMinBounds() {
        System.out.println("getMinBounds");
        CGPParameters instance = null;
        int[] expResult = null;
        int[] result = instance.getMinBounds();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of getMaxBounds method, of class CGPParameters. */
    @Test
    public void testGetMaxBounds() {
        System.out.println("getMaxBounds");
        CGPParameters instance = null;
        int[] expResult = null;
        int[] result = instance.getMaxBounds();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    
}
