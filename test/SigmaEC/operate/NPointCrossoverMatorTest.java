package SigmaEC.operate;

import SigmaEC.represent.Gene;
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
public class NPointCrossoverMatorTest
{
    private final Mockery context;
    private Random mockRandom;
    private List<TestGene> parent1, parent2, parent3;
    private final static int NUM_CUT_POINTS = 2;
    private final static int GENOME_LENGTH = 12;
    private final static int[] randomSequence = {3, 9, 2, 1, 0, 1, 1, 0, 0, 1, 0};
    
    public NPointCrossoverMatorTest()
    {
        context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
    }
    
    @Before
    public void setUp()
    {
        mockRandom = context.mock(Random.class);
        context.checking(new Expectations () {{
            allowing(mockRandom).nextInt(with(any(Integer.class)));
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
                    returnValue(randomSequence[9]),
                    returnValue(randomSequence[10])));
        }});
        
        parent1 = getParent(0);
        parent2 = getParent(12);
        parent3 = getParent(24);
        assert(parent1.size() == parent2.size());
        assert(parent2.size() == parent3.size());
    }
    
    private List<TestGene> getParent(final int startIndex)
    {
        return new ArrayList<TestGene>() {{
            for (int i = startIndex; i < startIndex + GENOME_LENGTH; i++)
                add(new TestGene(i));
        }};
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test
    public void testConstructor()
    {
        System.out.println("ctor");
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, mockRandom);
        assertEquals(NUM_PARENTS, SUT.getNumParents());
        assertEquals(NUM_PARENTS, SUT.getNumChildren());
        assertEquals(NUM_CUT_POINTS, SUT.getNumCutPoints());
        assertTrue(SUT.repOK());
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE1()
    {
        System.out.println("ctor (IAE1)");
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(0, NUM_PARENTS, mockRandom);
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE2()
    {
        System.out.println("ctor (IAE2)");
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, 1, mockRandom);
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE3()
    {
        System.out.println("ctor (IAE3)");
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, null);
    }

    /** Test of mate method, of class NPointCrossoverMator. This is a brittle
     * test, as it depends on the effects of the stubbed Random. */
    @Test
    public void testMate3()
    {
        System.out.println("mate (3 parents)");
        final int NUM_PARENTS = 3;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, mockRandom);
        List<List<TestGene>> parents = new ArrayList<List<TestGene>>() {{ add(parent1); add(parent2); add(parent3); }};
        List<List<TestGene>> children = SUT.mate(parents);
        
        List<TestGene> child0 = intsToGenome(new int[] { 24, 25, 26, 15, 16, 17, 18, 19, 20, 9, 10, 11  });
        List<TestGene> child1 = intsToGenome(new int[] { 12, 13, 14, 27, 28, 29, 30, 31, 32, 33, 34, 35 });
        List<TestGene> child2 = intsToGenome(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 21, 22, 23 });
        
        assertEquals(NUM_PARENTS, children.size());
        assertTrue(children.get(0).equals(child0));
        assertTrue(children.get(1).equals(child1));
        assertTrue(children.get(2).equals(child2));
        assertEquals(NUM_PARENTS, parents.size());
        assertTrue(parents.get(0).equals(getParent(0)));
        assertTrue(parents.get(1).equals(getParent(12)));
        assertTrue(parents.get(2).equals(getParent(24)));
        assertTrue(SUT.repOK());
    }
    
    private List<TestGene> intsToGenome(final int[] sequence)
    {
        return new ArrayList<TestGene>(sequence.length) {{
           for (int i = 0; i < sequence.length; i++)
               add(new TestGene(sequence[i]));
        }};
    }

    /** Test of toString method, of class NPointCrossoverMator. */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, mockRandom);
        String expResult = "[NPointCrossoverMator: CutPoints=2, Parents=2]";
        String result = SUT.toString();
        assertEquals(expResult, result);
        assertTrue(SUT.repOK());
    }

    /** Test of equals method, of class NPointCrossoverMator. */
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, mockRandom);
        NPointCrossoverMator gRef = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS, mockRandom);
        NPointCrossoverMator bRef = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, NUM_PARENTS + 1, mockRandom);
        assertTrue(SUT.equals(gRef));
        assertTrue(gRef.equals(SUT));
        assertTrue(SUT.equals(SUT));
        assertFalse(SUT.equals(null));
        assertFalse(SUT.equals(bRef));
        assertFalse(bRef.equals(SUT));
        assertTrue(SUT.repOK());
    }
    
    private class TestGene implements Gene
    {
        int val;
        TestGene(int val) { this.val = val; }
        
        @Override
        public boolean equals(Object ref)
        {
            if (!(ref instanceof TestGene))
                return false;
            return val == ((TestGene)ref).val;
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 13 * hash + this.val;
            return hash;
        }
        
        @Override
        public String toString()
        {
            return ((Integer)val).toString();
        }
    }
}
