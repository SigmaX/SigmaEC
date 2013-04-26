package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric 'Siggy' Scott
 */
public class RandomSelectorTest
{
    private RandomSelector SUT;
    private Random random;
    private List<TestIndividual> population;
    private final static int POP_SIZE = 10;
    
    public RandomSelectorTest()
    {
    }
    
    @Before
    public void setUp()
    {
        random = new Random();
        SUT = new RandomSelector(random);
        population = new ArrayList(POP_SIZE) {{
            for (int i = 0; i < POP_SIZE; i++)
                add(new TestIndividual(i));
        }};
    }

    /** Test of selectIndividual method, of class RandomSelector. Clearly this
     test is quite brittle, since it's tied to a specific set of random numbers. */
    @Test
    public void testSelectIndividual()
    {
        System.out.println("selectIndividual");
        final int NUM_SAMPLES = 10000000;
        final double EPSILON = 0.001;
        
        Map<Individual, Integer> count = new HashMap<Individual, Integer>(POP_SIZE);
        for (Individual ind : population)
            count.put(ind, 0);
        for (int i = 0; i < NUM_SAMPLES; i++)
        {
            TestIndividual ind = (TestIndividual) SUT.selectIndividual(population);
            count.put(ind, count.get(ind) + 1);
        }
        
        for (Individual ind : population)
            assertEquals(1.0/POP_SIZE, count.get(ind)/(double)NUM_SAMPLES, EPSILON);
        
        assertTrue(SUT.repOK());
    }

    /** Test of selectIndividual method, of class RandomSelector. */
    @Test(expected = NullPointerException.class)
    public void testSelectIndividualNPE ()
    {
        System.out.println("selectIndividual NPE");
            SUT.selectIndividual(null);
    }

    /** Test of selectIndividual method, of class RandomSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectIndividualIAE ()
    {
        System.out.println("selectIndividual IAE");
            SUT.selectIndividual(new ArrayList());
    }
    
    @Test
    public void testToString()
    {
        System.out.println("toString");
        assertEquals("[RandomSelector: Random=" + random.toString() + "]", SUT.toString());
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        fail("Test case not implemented");
    }
}
