package SigmaEC.evaluate.objective.function;

import SigmaEC.util.Parameters;
import java.util.Properties;
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
        assertEquals(expResult, result);
    }

    /** Test of numOutputs method, of class ConcatenatedBooleanFunction. */
    @Test
    public void testNumOutputs1() {
        System.out.println("numOutputs (shared inputs)");
        final ConcatenatedBooleanFunction instance = new ConcatenatedBooleanFunction(getParams().build(), BASE);
        int expResult = 2;
        int result = instance.numOutputs();
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
}
