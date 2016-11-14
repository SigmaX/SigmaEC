package SigmaEC.evaluate.objective.function;

import SigmaEC.util.Misc;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Eric O. Scott
 */
@RunWith(Parameterized.class)
public class ADDERTest {
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {   
            { 4, 3, new boolean[] {false, false, false, false}, 0 }, // 0 + 0 = 0
            { 4, 3, new boolean[] {true, false, false, true}, 3}, //2 + 1 = 3
            { 4, 3, new boolean[] {true, true, true, true}, 6}, // 3 + 3 = 6
            { 6, 4, new boolean[] {false, false, false, false, false, false}, 0}, // 0 + 0 = 0
            { 6, 4, new boolean[] {false, true, false, false, false, true}, 3}, //2 + 1 = 3
            { 6, 4, new boolean[] {true, true, true, true, true, true}, 14} //7 + 7 = 14
           });
    }
    
    @Parameter
    public int arity;
    
    @Parameter(value = 1)
    public int expectedNumOutputs;
    
    @Parameter(value = 2)
    public boolean[] input;
    
    @Parameter(value = 3)
    public int expectedResult;
    
    private ADDER sut;
    
    @Before
    public void setUp() {
        sut = new ADDER((new SigmaEC.util.Parameters.Builder(new Properties()))
                .setParameter(SigmaEC.util.Parameters.push("base", ADDER.P_ARITY), String.valueOf(arity))
                .build(), "base");
    }

    @Test
    public void testArity() {
        System.out.println("arity");
        assertEquals(arity, sut.arity());
        assertTrue(sut.repOK());
    }

    @Test
    public void testNumOutputs() {
        System.out.println("numOutputs");
        assertEquals(expectedNumOutputs, sut.numOutputs());
        assertTrue(sut.repOK());
    }

    @Test
    public void testExecute() {
        System.out.println("execute");
        final boolean[] result = sut.execute(input);
        final int resultInt = Misc.bitStringToInt(result);
        assertEquals(expectedResult, resultInt);
        assertTrue(sut.repOK());
    }
}
