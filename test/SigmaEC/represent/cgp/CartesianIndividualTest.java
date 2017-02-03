package SigmaEC.represent.cgp;

import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.NAND;
import SigmaEC.util.Parameters;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class CartesianIndividualTest {
    private final static String BASE = "base";
    
    private final static Parameters params = new Parameters.Builder(new Properties())
        .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_INPUTS), "2")
        .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_OUTPUTS), "1")
        .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_LAYERS), "2")
        .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_NODES_PER_LAYER), "2")
        .setParameter(Parameters.push(BASE, CGPParameters.P_MAX_ARITY), "2")
        .setParameter(Parameters.push(BASE, CGPParameters.P_LEVELS_BACK), "1")
        .setParameter(Parameters.push(BASE, CGPParameters.P_NUM_PRIMITIVES), "4")
        .build();
    private final static CGPParameters cgpParameters = new CGPParameters(params, BASE);
    
    public CartesianIndividualTest() {
    }
    
    public CartesianIndividual.Builder getInd() {
        return new CartesianIndividual.Builder(cgpParameters, new int[] { 5 })
                .setFunction(0, 0, new NAND(params, BASE), new int[] { 0, 1 })
                .setFunction(0, 1, new NAND(params, BASE), new int[] { 0, 1 })
                .setFunction(1, 0, new NAND(params, BASE), new int[] { 2, 3 })
                .setFunction(1, 1, new NAND(params, BASE), new int[] { 2, 3 });
    }

    /** Test of arity method, of class CartesianIndividual. */
    @Test
    public void testArity() {
        System.out.println("arity");
        final CartesianIndividual instance = getInd().build();
        final int expResult = 2;
        final int result = instance.arity();
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of numOutputs method, of class CartesianIndividual. */
    @Test
    public void testNumOutputs() {
        System.out.println("numOutputs");
        final CartesianIndividual instance = getInd().build();
        final int expResult = 1;
        final int result = instance.numOutputs();
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of getFunction method, of class CartesianIndividual. */
    @Test
    public void testGetFunction() {
        System.out.println("getFunction");
        final CartesianIndividual instance = getInd().build();
        final BooleanFunction expResult = new NAND(params, BASE);
        final BooleanFunction result = instance.getFunction(0, 0);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of getInputs method, of class CartesianIndividual. */
    @Test
    public void testGetInputs() {
        System.out.println("getInputs");
        final CartesianIndividual instance = getInd().build();
        assertArrayEquals(new int[] { 0, 1}, instance.getInputs(0, 0));
        assertArrayEquals(new int[] { 0, 1}, instance.getInputs(0, 1));
        assertArrayEquals(new int[] { 2, 3}, instance.getInputs(1, 0));
        assertArrayEquals(new int[] { 2, 3}, instance.getInputs(1, 1));
        assertTrue(instance.repOK());
    }

    /** Test of getOutputSources method, of class CartesianIndividual. */
    @Test
    public void testGetOutputSources() {
        System.out.println("getOutputSources");
        final CartesianIndividual instance = getInd().build();
        final int[] expResult = { 5 };
        final int[] result = instance.getOutputSources();
        assertArrayEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of getExecutionPaths method, of class CartesianIndividual. */
    @Test
    public void testGetExecutionPaths_int_int() {
        System.out.println("getExecutionPaths");
        final CartesianIndividual instance = getInd().build().computeExecutionPaths();
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { 0 })), instance.getExecutionPaths(0, 0));
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { 0 })), instance.getExecutionPaths(0, 1));
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { })), instance.getExecutionPaths(1, 0));
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { 0 })), instance.getExecutionPaths(1, 1));
        assertTrue(instance.repOK());
    }

    /** Test of getExecutionPaths method, of class CartesianIndividual. */
    @Test
    public void testGetExecutionPaths_int() {
        System.out.println("getExecutionPaths");
        final CartesianIndividual instance = getInd().build().computeExecutionPaths();
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { 0 })), instance.getExecutionPaths(0));
        assertEquals(new HashSet<>(Arrays.asList(new Integer[] { 0 })), instance.getExecutionPaths(1));
        assertTrue(instance.repOK());
    }

    /** Test of getFitness method, of class CartesianIndividual. */
    @Test
    public void testGetFitness() {
        System.out.println("getFitness");
        final double expResult = 1.0;
        final CartesianIndividual instance = getInd().build().setFitness(expResult);
        assertEquals(expResult, instance.getFitness(), 0.0);
        assertTrue(instance.repOK());
    }

    /** Test of isEvaluated method, of class CartesianIndividual. */
    @Test
    public void testIsEvaluated0() {
        System.out.println("isEvaluated");
        final CartesianIndividual instance = getInd().build();
        assertFalse(instance.isEvaluated());
        assertTrue(instance.repOK());
    }

    /** Test of isEvaluated method, of class CartesianIndividual. */
    @Test
    public void testIsEvaluated1() {
        System.out.println("isEvaluated");
        final CartesianIndividual instance = getInd().build().setFitness(1.0);
        assertTrue(instance.isEvaluated());
        assertTrue(instance.repOK());
    }

    /** Test of clearFitness method, of class CartesianIndividual. */
    @Test
    public void testClearFitness() {
        System.out.println("clearFitness");
        CartesianIndividual instance = getInd().build().setFitness(1.0);
        assertTrue(instance.isEvaluated());
        instance = instance.clearFitness();
        assertFalse(instance.isEvaluated());
        assertTrue(instance.repOK());
    }
    
    /** Test of execute method, of class CartesianIndividual. */
    @Test
    public void testExecute() {
        System.out.println("execute");
        boolean[][] inputs = {  new boolean[] { true, true },
                                new boolean[] { true, false },
                                new boolean[] { false, true},
                                new boolean[] { false, false}};
        final CartesianIndividual instance = getInd().build();
        assertArrayEquals(new boolean[] { true }, instance.execute(inputs[0]));
        assertArrayEquals(new boolean[] { false }, instance.execute(inputs[1]));
        assertArrayEquals(new boolean[] { false }, instance.execute(inputs[2]));
        assertArrayEquals(new boolean[] { false }, instance.execute(inputs[2]));
        assertTrue(instance.repOK());
    }
}
