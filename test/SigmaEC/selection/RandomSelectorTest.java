package SigmaEC.selection;

import SigmaEC.test.TestIndividual;
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
 * @author Eric 'Siggy' Scott
 */
public class RandomSelectorTest
{
    private final Mockery context;
    private RandomSelector SUT;
    private List<TestIndividual> population;
    private final static int[] randomSequence = {5, 0, 2, 7, 6, 7, 4, 6, 8, 1};
    private final static int POP_SIZE = 10;
    
    public RandomSelectorTest()
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
            allowing(mockRandom).nextInt(POP_SIZE);
            will(onConsecutiveCalls(
                    returnValue(randomSequence[0]),
                    returnValue(randomSequence[1]),
                    returnValue(randomSequence[2]),
                    returnValue(randomSequence[3]),
                    returnValue(randomSequence[4]),
                    returnValue(randomSequence[5]),
                    returnValue(randomSequence[6]),
                    returnValue(randomSequence[7]),
                    returnValue(randomSequence[8]),
                    returnValue(randomSequence[9])));
        }});
        SUT = new RandomSelector(mockRandom);
        population = new ArrayList(POP_SIZE) {{
            for (int i = 0; i < POP_SIZE; i++)
                add(new TestIndividual(i));
        }};
    }

    /** Test of selectIndividual method, of class RandomSelector. */
    @Test
    public void testSelectIndividual()
    {
        System.out.println("selectIndividual");
        for (int i = 0; i < POP_SIZE; i++)
        {
            TestIndividual result = (TestIndividual) SUT.selectIndividual(population);
            assertEquals(randomSequence[i], result.getTrait(), 0);
        }
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
}
