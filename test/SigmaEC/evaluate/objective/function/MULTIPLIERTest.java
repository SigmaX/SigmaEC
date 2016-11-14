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

/**
 *
 * @author Eric O. Scott
 */
@RunWith(Parameterized.class)
public class MULTIPLIERTest {
    
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {   
            { 4, 4, new boolean[] {false, false, false, false}, 0 }, // 0 * 0 = 0
            { 4, 4, new boolean[] {true, false, false, true}, 2}, //2 * 1 = 2
            { 4, 4, new boolean[] {true, true, true, true}, 9}, // 3 * 3 = 9
            { 5, 6, new boolean[] {false, false, false, false, false}, 0 }, // 0 * 0 = 0
            { 5, 6, new boolean[] {false, true, false, false, true}, 2}, //2 * 1 = 2
            { 5, 6, new boolean[] {false, true, true, true, true}, 9}, // 3 * 3 = 9
            { 6, 6, new boolean[] {false, false, false, false, false, false}, 0}, // 0 * 0 = 0
            { 6, 6, new boolean[] {false, true, false, false, false, true}, 2}, //2 * 1 = 2
            { 6, 6, new boolean[] {true, true, true, true, true, true}, 49} //7 * 7 = 49
           });
    }
    
    @Parameterized.Parameter
    public int arity;
    
    @Parameterized.Parameter(value = 1)
    public int expectedNumOutputs;
    
    @Parameterized.Parameter(value = 2)
    public boolean[] input;
    
    @Parameterized.Parameter(value = 3)
    public int expectedResult;
    
    private MULTIPLIER sut;
    
    @Before
    public void setUp() {
        sut = new MULTIPLIER((new SigmaEC.util.Parameters.Builder(new Properties()))
                .setParameter(SigmaEC.util.Parameters.push("base", MULTIPLIER.P_ARITY), String.valueOf(arity))
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
