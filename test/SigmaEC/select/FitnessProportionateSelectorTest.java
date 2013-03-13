package SigmaEC.select;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.test.TestIndividual;
import SigmaEC.test.TestObjective;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class FitnessProportionateSelectorTest
{
    private FitnessProportionateSelector SUT;
    private final Mockery context;
    private List<TestIndividual> population;
    private final static double[] randomSequence = {.5, 0.0, .2, .7, .6};
    private final static int POP_SIZE = 10;
    
    
    public FitnessProportionateSelectorTest()
    {
        context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
    }
    
    @Before
    public void setUp()
    {
        final Random mockRandom = context.mock(Random.class);
        context.checking(new Expectations () {{
            allowing(mockRandom).nextDouble();
            will(onConsecutiveCalls(
                    returnValue(randomSequence[0]),
                    returnValue(randomSequence[1]),
                    returnValue(randomSequence[2]),
                    returnValue(randomSequence[3]),
                    returnValue(randomSequence[4])));
        }});
        ObjectiveFunction obj = new TestObjective();
        SUT = new FitnessProportionateSelector(obj, mockRandom, POP_SIZE);
        population = new ArrayList(POP_SIZE) {{
            for (int i = 1; i <= POP_SIZE; i++)
                add(new TestIndividual(i));
        }};
    }

    /**
     * Test of selectIndividual method, of class FitnessProportionateSelector.
     */
    @Test
    public void testSelectIndividual()
    {
        System.out.println("selectIndividual");

        // Dart = 0.5
        int expResult = 4;
        TestIndividual result = (TestIndividual) SUT.selectIndividual(population);
        assertEquals(expResult, result.getTrait(), 0);
        
        // Dart = 0.0
        expResult = 1;
        result = (TestIndividual) SUT.selectIndividual(population);
        assertEquals(expResult, result.getTrait(), 0);
        
        // Dart = 0.2
        expResult = 2;
        result = (TestIndividual) SUT.selectIndividual(population);
        assertEquals(expResult, result.getTrait(), 0);
        
        // Dart = 0.7
        expResult = 5;
        result = (TestIndividual) SUT.selectIndividual(population);
        assertEquals(expResult, result.getTrait(), 0);
        
        // Dart = 0.6
        expResult = 4;
        result = (TestIndividual) SUT.selectIndividual(population);
        assertEquals(expResult, result.getTrait(), 0);
        
        assertTrue(SUT.repOK());
    }
}
