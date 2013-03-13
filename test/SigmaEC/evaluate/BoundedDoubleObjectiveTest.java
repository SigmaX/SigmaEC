package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;
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
    private final VectorObjectiveFunction objective = new TestVectorObjective();
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
        // Using JMock to stub individuals.
        Mockery context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        final DoubleVectorIndividual gInd1 = new TestVectorIndividual(new double[] {0, 0, 5.0});
        final DoubleVectorIndividual gInd2 = new TestVectorIndividual(new double[] {-3.0, -8.0, 3.3});
        final DoubleVectorIndividual gInd3 = new TestVectorIndividual(new double[] {3.0, 4.0, 17.0});
        final DoubleVectorIndividual gInd4 = new TestVectorIndividual(new double[] {-2.6, 3.1, 8.8});
            
            // Points *outside* the bounds
        final DoubleVectorIndividual bInd1 = new TestVectorIndividual(new double[] {0, 0, 0});
        final DoubleVectorIndividual bInd2 = new TestVectorIndividual(new double[] {-3.1, 0, 3.3});
        final DoubleVectorIndividual bInd3 = new TestVectorIndividual(new double[] {3.1, 0, 5.0});
        final DoubleVectorIndividual bInd4 = new TestVectorIndividual(new double[] {0, 0, 18.0});
        
        assertEquals(5.0, SUT.fitness(gInd1), 0);
        assertEquals(-7.7, SUT.fitness(gInd2), 0);
        assertEquals(24.0, SUT.fitness(gInd3), 0);
        assertEquals(9.3, SUT.fitness(gInd4), 0);
        
        assertEquals(-1.0, SUT.fitness(bInd1), 0);
        assertEquals(-1.0, SUT.fitness(bInd2), 0);
        assertEquals(-1.0, SUT.fitness(bInd3), 0);
        assertEquals(-1.0, SUT.fitness(bInd4), 0);
        
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
    
    private class TestVectorObjective extends VectorObjectiveFunction<DoubleVectorIndividual>
    {
        @Override
        public double fitness(DoubleVectorIndividual ind)
        {
            double sum = 0;
            for(double d : ind.getVector())
                sum += d;
            return sum;
        }

        @Override
        public boolean repOK() { return true; }
        @Override
        public String toString() { return "[TestVectorObjective]"; } 
        @Override
        public int getNumDimensions() { return 3;}
    }
    
    private class TestVectorIndividual extends DoubleVectorIndividual
    {
        private final double[] vector;
        public TestVectorIndividual(final double[] vector)
        {
            this.vector = vector;
        }
        
        @Override
        public int size() { return vector.length; }

        @Override
        public double[] getVector() { return vector; }

        @Override
        public boolean repOK() { return true; }

        @Override
        public double getElement(int i) { return vector[i]; }
        
    }
}
