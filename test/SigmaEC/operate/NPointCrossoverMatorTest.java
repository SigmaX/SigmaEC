package SigmaEC.operate;

import SigmaEC.represent.Gene;
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
public class NPointCrossoverMatorTest
{
    private Random random;
    private List<TestGene> parent1, parent2;
    private final static int GENOME_LENGTH = 10;
    
    public NPointCrossoverMatorTest()
    {
    }
    
    @Before
    public void setUp()
    {
        random = new Random();
        parent1 = makeUniformParent(0, GENOME_LENGTH);
        parent2 = makeUniformParent(1, GENOME_LENGTH);
        assert(parent1.size() == parent2.size());
    }
    
    private List<TestGene> makeUniformParent(final int value, final int length)
    {
        return new ArrayList<TestGene>() {{
            for (int i = 0; i < length; i++)
                add(new TestGene(value));
        }};
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test
    public void testConstructor()
    {
        System.out.println("ctor");
        final int NUM_CUT_POINTS = 2;
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, false, random);
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
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(0, false, random);
    }

    /** Test of constructor method, of class NPointCrossoverMator. */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorIAE2()
    {
        System.out.println("ctor (IAE2)");
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(2, false, null);
    }
    
    

    /** Test of mate method, of class NPointCrossoverMator. */
    @Test
    public void testMate2()
    {
        System.out.println("mate (2 cut points)");
        testMate(2);
    }
    
    private void testMate(int numCutPoints)
    {
        assert(numCutPoints > 0);
        final int NUM_TRIALS = 1000000;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(numCutPoints, true, random);
        List<List<TestGene>> parents = new ArrayList<List<TestGene>>() {{ add(parent1); add(parent2); }};
        
        Map<List<TestGene>, Integer> count = testChildSequenceDistribution(NUM_TRIALS, SUT, parents, numCutPoints);
        testGenePlacement(count, NUM_TRIALS);
        
        assertTrue(SUT.repOK());
    }
    
    /** @return the probability that a gene at location g in parent 1 ends up
     * in child 1 after crossover.
     * @param g Index of the gene in parent 1
     * @param n Length of the genome
     */
    private double chanceGeneInFirstChild(int g, int n)
    {
        g = g+1; // using 1-based indexing
        return Math.pow((n - g + 1)/(double)(n + 1), 2) + Math.pow(g/(double)(n+1), 2);
    }
    
    private int factorial(int n)
    {
        assert(n>=0);
        if (n == 0 || n == 1)
            return 1;
        else
            return n*factorial(n-1);
    }

    /** Test of mate method, of class NPointCrossoverMator. */
    //@Test
    public void testMate3()
    {
        System.out.println("mate (3 cut points)");
        
        // TODO
    }

    /** Test of toString method, of class NPointCrossoverMator. */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        final int NUM_CUT_POINTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, false, random);
        String expResult = "[NPointCrossoverMator: CutPoints=" + NUM_CUT_POINTS + "]";
        String result = SUT.toString();
        assertEquals(expResult, result);
        assertTrue(SUT.repOK());
    }

    /** Test of equals method, of class NPointCrossoverMator. */
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        final int NUM_CUT_POINTS = 2;
        final int NUM_PARENTS = 2;
        NPointCrossoverMator SUT = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, false, random);
        NPointCrossoverMator gRef = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS, false, random);
        NPointCrossoverMator bRef = new NPointCrossoverMator<TestGene>(NUM_CUT_POINTS + 1, false, random);
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

    /** Given the frequencies of the occurrence of the first child of NUM_TRIALS
     * crossover operations, this tests that the number of times a given gene
     * in the first parent ends up placed in the first child matches the
     * prediction of the equation given in chanceGeneInFirstChild.
     */
    private void testGenePlacement(Map<List<TestGene>, Integer> count, final int NUM_TRIALS) {
        int[] geneCount = new int[GENOME_LENGTH]; // Counts how many times a gene at location i in parent 1 ended up in child 1
        for (int i = 0; i < GENOME_LENGTH; i++)
            geneCount[i] = 0;
        for (List<TestGene> genome : count.keySet())
        {
            for (int i = 0; i < genome.size(); i++)
            {
                if (genome.get(i).val == 0) // Gene came from parent 1
                    geneCount[i] += count.get(genome);
            }
        }
        
        for (int i = 0; i < geneCount.length; i++)
        {
            //System.out.println("Was: " + geneCount[i] + ", Expected: " + chanceGeneInFirstChild(i, GENOME_LENGTH)*NUM_TRIALS);
            assertEquals(chanceGeneInFirstChild(i, GENOME_LENGTH), (double)geneCount[i]/NUM_TRIALS, 0.01);
        }
    }

    /** Runs NUM_TRIALS cross-over operations and tests that the frequency
     * of different cut patterns matches the mathematical predictions.
     * 
     * @return A hash containing the number of times a given genome occurred
     * in the first child.
     */
    private Map<List<TestGene>, Integer> testChildSequenceDistribution(final int NUM_TRIALS, NPointCrossoverMator SUT, List<List<TestGene>> parents, int numCutPoints)
    {
        // Count the number of times each type of cut is made
        Map<List<TestGene>, Integer> count = new HashMap();
        for (int i = 0; i < NUM_TRIALS; i++)
        {
            List<List<TestGene>> children = SUT.mate(parents);
            List<TestGene> firstChild = children.get(0);
            if (!count.containsKey(firstChild))
                count.put(firstChild, 1);
            else
                count.put(firstChild, count.get(firstChild) + 1);
        }
        // Parents should be unmodified
        assertEquals(makeUniformParent(0, GENOME_LENGTH), parent1);
        assertEquals(makeUniformParent(1, GENOME_LENGTH), parent2);
        // Expected number of children of each combination
        double nonClonePrediction = NUM_TRIALS * factorial(numCutPoints)/Math.pow(GENOME_LENGTH + 1, numCutPoints);
        double clonePrediction = NUM_TRIALS * 1/Math.pow(GENOME_LENGTH + 1, numCutPoints - 1);
        // Collect the mean of the non-cloned children, and count where genes ended up.
        int sum = 0;
        int cloneCount = 0;
        for (List<TestGene> genome : count.keySet())
        {   
            if (genome.equals(parent1) || genome.equals(parent2))
                cloneCount += count.get(genome);
            else
            {
                //System.out.println("Was: " + count.get(genome) + ", Expected: " + nonClonePrediction);
                sum += count.get(genome);
            }
        }
        double mean = sum/(count.size() - 2);
        assertEquals(nonClonePrediction, mean, 15.0);
        
        // TODO check clonePrediction
        // TODO check count.size()
        
        return count;
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
