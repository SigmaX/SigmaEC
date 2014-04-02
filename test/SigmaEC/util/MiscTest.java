package SigmaEC.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class MiscTest {
    
    public MiscTest()
    {
    }
    
    /** Test of containsOnlyClass method, of class Misc. */
    @Test
    public void testContainsOnlyClass() {
        System.out.println("containsOnlyClass");
        Collection intCollection = new ArrayList() {{
            add(1); add(2); add(1); add(8);
        }};
        Collection mixedCollection = new ArrayList() {{
            add(1); add(2.0); add(1); add(8.0);
        }};
        assertEquals(true, Misc.containsOnlyClass(intCollection, Integer.class));
        assertEquals(false, Misc.containsOnlyClass(mixedCollection, Integer.class));
    }

    /** Test of gaussianSample method, of class Misc. */
    /*
    @Test
    public void testGaussianSample() {
        System.out.println("gaussianSample");
        Random random = new Random();
        double expResult = 0.0;
        double result = Misc.gaussianSample(random);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }*/
    
}