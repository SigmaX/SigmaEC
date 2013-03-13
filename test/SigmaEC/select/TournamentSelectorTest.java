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
public class TournamentSelectorTest
{
    private final Mockery context;
    private TournamentSelector SUT;
    private List<TestIndividual> population;
    private final static int[] randomSequence = {5, 0, 2, 7, 6, 7, 4, 6, 8, 1};
    private final static int POP_SIZE = 10;
    
    public TournamentSelectorTest()
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
        ObjectiveFunction obj = new TestObjective();
        SUT = new TournamentSelector(obj, mockRandom, 2);
        population = new ArrayList(10) {{
            for (int i = 0; i < POP_SIZE; i++)
                add(new TestIndividual(i));
        }};
    }

    /** Test of selectIndividual method, of class TournamentSelector. */
    @Test
    public void testSelectIndividual()
    {
        System.out.println("selectIndividual");
        assertTrue(SUT.repOK());
        for (int i = 0; i < POP_SIZE/2; i+=2)
        {
            TestIndividual result = (TestIndividual) SUT.selectIndividual(population);
            int expectedResult = Math.max(randomSequence[i], randomSequence[i+1]);
            assertEquals(expectedResult, result.getTrait(), 0);
        }
        assertTrue(SUT.repOK());
    }

    /** Test of selectIndividual method, of class TournamentSelector. */
    @Test(expected = NullPointerException.class)
    public void testSelectIndividualNPE()
    {
        System.out.println("selectIndividual NPE");
        SUT.selectIndividual(null);
    }

    /** Test of selectIndividual method, of class TournamentSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectIndividualIAE()
    {
        System.out.println("selectIndividual IAE");
        SUT.selectIndividual(new ArrayList());
    }
}
