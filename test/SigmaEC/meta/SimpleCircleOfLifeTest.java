package SigmaEC.meta;

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
public class SimpleCircleOfLifeTest {
    
    public SimpleCircleOfLifeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /** Test of evolve method, of class SimpleCircleOfLife. */
    @Test
    public void testEvolve() {
        System.out.println("evolve");
        int run = 0;
        SimpleCircleOfLife instance = null;
        CircleOfLife.EvolutionResult expResult = null;
        CircleOfLife.EvolutionResult result = instance.evolve(run);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of equals method, of class SimpleCircleOfLife. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        SimpleCircleOfLife instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
