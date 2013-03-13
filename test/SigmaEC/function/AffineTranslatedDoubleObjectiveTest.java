/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SigmaEC.function;

import SigmaEC.representation.DoubleVectorIndividual;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eric
 */
public class AffineTranslatedDoubleObjectiveTest
{
    
    public AffineTranslatedDoubleObjectiveTest()
    {
    }
    
    @Before
    public void setUp()
    {
    }

    /**
     * Test of getNumDimensions method, of class AffineTranslatedDoubleObjective.
     */
    @Test
    public void testGetNumDimensions()
    {
        System.out.println("getNumDimensions");
        AffineTranslatedDoubleObjective instance = null;
        int expResult = 0;
        int result = instance.getNumDimensions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of transform method, of class AffineTranslatedDoubleObjective.
     */
    @Test
    public void testTransform()
    {
        System.out.println("transform");
        DoubleVectorIndividual ind = null;
        AffineTranslatedDoubleObjective instance = null;
        DoubleVectorIndividual expResult = null;
        DoubleVectorIndividual result = instance.transform(ind);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fitness method, of class AffineTranslatedDoubleObjective.
     */
    @Test
    public void testFitness()
    {
        System.out.println("fitness");
        DoubleVectorIndividual ind = null;
        AffineTranslatedDoubleObjective instance = null;
        double expResult = 0.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of repOK method, of class AffineTranslatedDoubleObjective.
     */
    @Test
    public void testRepOK()
    {
        System.out.println("repOK");
        AffineTranslatedDoubleObjective instance = null;
        boolean expResult = false;
        boolean result = instance.repOK();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
