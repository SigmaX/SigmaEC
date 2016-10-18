package SigmaEC.evaluate.objective.function;

import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class IMPLYTest {    
    public IMPLYTest() {
    }

    @Test
    public void testArity() {
        System.out.println("arity");
        final IMPLY instance = new IMPLY(new Parameters.Builder(new Properties()).build(), "");
        final int expResult = 2;
        final int result = instance.arity();
        assertTrue(instance.repOK());
        assertEquals(expResult, result);
    }

    @Test
    public void testNumOutputs() {
        System.out.println("numOutputs");
        final IMPLY instance = new IMPLY(new Parameters.Builder(new Properties()).build(), "");
        final int expResult = 1;
        final int result = instance.numOutputs();
        assertTrue(instance.repOK());
        assertEquals(expResult, result);
    }

    @Test
    public void testExecute() {
        System.out.println("execute");
        final boolean[][] inputs = new boolean[][] {
            { true, true },
            { true, false },
            { false, true },
            { false, false}
        };
        final IMPLY instance = new IMPLY(new Parameters.Builder(new Properties()).build(), "");
        final boolean[][] expResult = new boolean[][] { {true}, {false}, {true}, {true} };
        final boolean[][] result = new boolean[4][];
        for (int i = 0; i < 4; i++)
            result[i] = instance.execute(inputs[i]);
        assertTrue(instance.repOK());
        for (int i = 0; i < expResult.length; i++)
            assertArrayEquals(expResult[i], result[i]);
    }
    
}
