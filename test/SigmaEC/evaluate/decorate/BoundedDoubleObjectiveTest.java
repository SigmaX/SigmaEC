package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.represent.Individual;
import SigmaEC.test.TestVectorDecoder;
import SigmaEC.test.TestVectorIndividual;
import SigmaEC.test.TestVectorObjective;
import SigmaEC.util.IDoublePoint;
import java.util.Arrays;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric 'Siggy' Scott
 */
public class BoundedDoubleObjectiveTest
{
    private BoundedDoubleObjective SUT;
    private final int numDimensions = 3;
    private final double singleBound = 5;
    private final ObjectiveFunction<DoubleVectorPhenotype> objective = new TestVectorObjective();
    private final IDoublePoint[] bounds = new IDoublePoint[] {
        new IDoublePoint(-3, 3),
        new IDoublePoint(-8, 4),
        new IDoublePoint(3.3, 17) };
    
    public BoundedDoubleObjectiveTest()
    {
    }
    
    @Before
    public void setUp()
    {
        assert(bounds.length == numDimensions);
        SUT = new BoundedDoubleObjective(numDimensions, bounds, objective);
    }

    @Test
    public void testConstructor1()
    {
        System.out.println("ctor1");
        assertEquals(numDimensions, SUT.getNumDimensions());
        assertTrue(SUT.repOK());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1IAE1()
    {
        System.out.println("ctor1 IAE1");
        SUT = new BoundedDoubleObjective(-1, bounds, objective);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1IAE2()
    {
        System.out.println("ctor1 IAE2");
        SUT = new BoundedDoubleObjective(numDimensions, null, objective);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1IAE3()
    {
        System.out.println("ctor1 IAE3");
        SUT = new BoundedDoubleObjective(numDimensions, bounds, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1IAE4()
    {
        System.out.println("ctor1 IAE4");
        SUT = new BoundedDoubleObjective(numDimensions+1, bounds, objective);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1IAE5()
    {
        System.out.println("ctor1 IAE5");
        bounds[0] = new IDoublePoint(5, 4);
        SUT = new BoundedDoubleObjective(numDimensions, bounds, objective);
    }

    @Test
    public void testConstructor2()
    {
        System.out.println("ctor2");
        SUT = new BoundedDoubleObjective(numDimensions, singleBound, objective);
        assertEquals(numDimensions, SUT.getNumDimensions());
        assertTrue(SUT.repOK());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2IAE1()
    {
        System.out.println("ctor2 IAE1");
        SUT = new BoundedDoubleObjective(0, singleBound, objective);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2IAE2()
    {
        System.out.println("ctor2 IAE2");
        SUT = new BoundedDoubleObjective(numDimensions, singleBound, null);
    }

    /** Test of fitness method, of class BoundedDoubleObjective. */
    @Test
    public void testFitness()
    {
        System.out.println("fitness");
        
        final TestVectorIndividual gInd1 = new TestVectorIndividual(new double[] {0, 0, 5.0});
        final TestVectorIndividual gInd2 = new TestVectorIndividual(new double[] {-3.0, -8.0, 3.3});
        final TestVectorIndividual gInd3 = new TestVectorIndividual(new double[] {3.0, 4.0, 17.0});
        final TestVectorIndividual gInd4 = new TestVectorIndividual(new double[] {-2.6, 3.1, 8.8});
            
            // Points *outside* the bounds
        final TestVectorIndividual bInd1 = new TestVectorIndividual(new double[] {0, 0, 0});
        final TestVectorIndividual bInd2 = new TestVectorIndividual(new double[] {-3.1, 0, 3.3});
        final TestVectorIndividual bInd3 = new TestVectorIndividual(new double[] {3.1, 0, 5.0});
        final TestVectorIndividual bInd4 = new TestVectorIndividual(new double[] {0, 0, 18.0});
        
        final Decoder<TestVectorIndividual, DoubleVectorPhenotype> d = new TestVectorDecoder();
        
        assertEquals(5.0, SUT.fitness(d.decode(gInd1)), 0);
        assertEquals(-7.7, SUT.fitness(d.decode(gInd2)), 0);
        assertEquals(24.0, SUT.fitness(d.decode(gInd3)), 0);
        assertEquals(9.3, SUT.fitness(d.decode(gInd4)), 0);
        
        assertEquals(-1.0, SUT.fitness(d.decode(bInd1)), 0);
        assertEquals(-1.0, SUT.fitness(d.decode(bInd2)), 0);
        assertEquals(-1.0, SUT.fitness(d.decode(bInd3)), 0);
        assertEquals(-1.0, SUT.fitness(d.decode(bInd4)), 0);
        
        assertTrue(SUT.repOK());
    }

    /**
     * Test of toString method, of class BoundedDoubleObjective.
     */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        String expResult = "[BoundedDoubleObjective: Dimensions=3, Bounds=["
                + "[IDoublePoint: x=-3.0, y=3.0], "
                + "[IDoublePoint: x=-8.0, y=4.0], "
                + "[IDoublePoint: x=3.3, y=17.0]], "
                + "Objective=[TestVectorObjective]]";
        String result = SUT.toString();
        assertEquals(expResult, result);
        assertTrue(SUT.repOK());
    }

    /**
     * Test of equals method, of class BoundedDoubleObjective.
     */
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        BoundedDoubleObjective ref = new BoundedDoubleObjective(numDimensions, Arrays.copyOf(bounds, numDimensions), new TestVectorObjective());
        assertTrue(SUT.equals(ref));
        assertTrue(ref.equals(SUT));
        assertTrue(SUT.equals(SUT));
        assertEquals(ref.toString(), SUT.toString());
        assertTrue(SUT.repOK());
        assertTrue(ref.repOK());
        
        BoundedDoubleObjective bRef = new BoundedDoubleObjective(1, new IDoublePoint[] { new IDoublePoint(-3, 3) }, new TestVectorObjective());
        assertFalse(SUT.equals(bRef));
        assertFalse(bRef.equals(SUT));
        assertTrue(SUT.repOK());
        assertTrue(bRef.repOK());
    }
}
