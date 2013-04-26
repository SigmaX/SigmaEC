package SigmaEC.select;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.test.TestObjective;
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
public class FitnessProportionateSelectorTest
{
    private FitnessProportionateSelector<TestIndividual> SUT;
    private Random random;
    private List<TestIndividual> population;
    private final static int POP_SIZE = 10;
    private int totalFitness;
    
    
    public FitnessProportionateSelectorTest()
    {
        
    }
    
    @Before
    public void setUp()
    {
        random = new Random();
        ObjectiveFunction<TestIndividual> obj = new TestObjective();
        SUT = new FitnessProportionateSelector<TestIndividual>(obj, random);
        population = new ArrayList(POP_SIZE) {{
            totalFitness = 0;
            for (int i = 1; i <= POP_SIZE; i++)
            {
                add(new TestIndividual(i));
                totalFitness += i;
            }
        }};
    }

    /** Test of selectIndividual method, of class FitnessProportionateSelector. */
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
            TestIndividual ind = SUT.selectIndividual(population);
            count.put(ind, count.get(ind) + 1);
        }
        
        for (TestIndividual ind : population)
        {
            double frequency = count.get(ind)/(double)NUM_SAMPLES;
            double expectedFrequency = ind.getTrait()/totalFitness;
            assertEquals(expectedFrequency, frequency, EPSILON);
        }
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testToString()
    {
        assertEquals("[FitnessProportionateSelector: Random=" + random.toString() + "]", SUT.toString());
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        FitnessProportionateSelector SUT = new FitnessProportionateSelector(new TestObjective(), random);
        FitnessProportionateSelector gRef = new FitnessProportionateSelector(new TestObjective(), random);
        FitnessProportionateSelector bRef = new FitnessProportionateSelector(new TestObjective(), new Random());
        assertTrue(SUT.equals(gRef));
        assertEquals(SUT.hashCode(), gRef.hashCode());
        assertTrue(gRef.equals(SUT));
        assertTrue(SUT.equals(SUT));
        assertFalse(SUT.equals(null));
        assertFalse(SUT.equals(bRef));
        assertNotEquals(SUT.hashCode(), bRef.hashCode());
        assertFalse(bRef.equals(SUT));
        assertTrue(SUT.repOK());
    }
}
