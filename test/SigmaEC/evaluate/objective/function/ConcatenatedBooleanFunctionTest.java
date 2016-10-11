package SigmaEC.evaluate.objective.function;

import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class ConcatenatedBooleanFunctionTest {
    private final static String BASE = "base";
    
    public ConcatenatedBooleanFunctionTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, ConcatenatedBooleanFunction.P_FUNCTIONS), "SigmaEC.evaluate.objective.function.XOR,SigmaEC.evaluate.objective.function.OR");
                //.setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, ConcatenatedBooleanFunction.P_FUNCTIONS), "0"), XOR.P_ARITY), "2")
                //.setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, ConcatenatedBooleanFunction.P_FUNCTIONS), "1"), OR.P_ARITY), "2");
    }

    /** Test of arity method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testArity1() {
        System.out.println("arity (shared inputs)");
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams().build(), BASE);
        int expResult = 2;
        int result = instance.arity();
        assertTrue(instance.isSharedInputs());
        assertEquals(expResult, result);
    }

    /** Test of arity method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testArity2() {
        System.out.println("arity (non-shared inputs)");
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams()
                .setParameter(Parameters.push(BASE, ConcatenatedBooleanFunction.P_SHARED_INPUTS), "false")
                .build(), BASE);
        int expResult = 4;
        int result = instance.arity();
        assertFalse(instance.isSharedInputs());
        assertEquals(expResult, result);
    }

    /** Test of numOutputs method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testNumOutputs1() {
        System.out.println("numOutputs (shared inputs)");
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams().build(), BASE);
        int expResult = 2;
        int result = instance.numOutputs();
        assertTrue(instance.isSharedInputs());
        assertEquals(expResult, result);
    }

    /** Test of numOutputs method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testNumOutputs2() {
        System.out.println("numOutputs (non-shared inputs)");
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams()
                .setParameter(Parameters.push(BASE, ConcatenatedBooleanFunction.P_SHARED_INPUTS), "false")
                .build(), BASE);
        int expResult = 2;
        int result = instance.numOutputs();
        assertFalse(instance.isSharedInputs());
        assertEquals(expResult, result);
    }

    /** Test of execute method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testExecute1() {
        System.out.println("execute (shared inputs)");
        boolean[][] inputs = new boolean[][] {
            { true, true },
            { true, false },
            { false, true },
            { false, false }
        };
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams().build(), BASE);
        assertTrue(instance.isSharedInputs());
        boolean[][] expResults = new boolean[][] {
            { false, true },
            { true, true },
            { true, true },
            { false, false }
        };
        for (int i = 0; i < inputs.length; i++) {
            boolean[] result = instance.execute(inputs[i]);
            assertArrayEquals(expResults[i], result);
        }
    }
    
    /** Test of execute method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testExecute2() {
        System.out.println("execute (non-shared inputs)");
        final List<boolean[]> inputs = TruthTableObjective.bitStringPermutations(4);
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams()
                .setParameter(Parameters.push(BASE, ConcatenatedBooleanFunction.P_SHARED_INPUTS), "false")
                .build(), BASE);
        assertFalse(instance.isSharedInputs());
        
        final List<boolean[]> expResults = new ArrayList() {{
            for (final boolean[] in : inputs)
                add(new boolean[] { in[0] ^ in[1], in[2] || in[3] });
        }};
        
        for (int i = 0; i < inputs.size(); i++) {
            boolean[] result = instance.execute(inputs.get(i));
            assertArrayEquals(expResults.get(i), result);
        }
    }
}
