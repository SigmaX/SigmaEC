package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eric 'Siggy' Scott
 */
public class RandomSelectorTest {
    private Parameters.Builder params;
    private RandomSelector<TestIndividual> SUT;
    private Random random;
    private List<TestIndividual> population;
    private final static int POP_SIZE = 10;
    private final static String BASE = "base";
    
    public RandomSelectorTest() { }
    
    @Before
    public void setUp() {
        params = getParams();
        population = new ArrayList(POP_SIZE) {{
            for (int i = 0; i < POP_SIZE; i++)
                add(new TestIndividual(i));
        }};
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, RandomSelector.P_RANDOM), "SigmaEC.SRandom");
    }

    @Test
    public void testSelectIndividual() {
        System.out.println("selectIndividual");
        SUT = new RandomSelector<TestIndividual>(params.build(), BASE);
        final int NUM_SAMPLES = 10000000;
        final double EPSILON = 0.001;
        
        final Map<TestIndividual, Integer> count = new HashMap<TestIndividual, Integer>(POP_SIZE);
        for (final TestIndividual ind : population)
            count.put(ind, 0);
        for (int i = 0; i < NUM_SAMPLES; i++) {
            final TestIndividual ind = SUT.selectIndividual(population);
            count.put(ind, count.get(ind) + 1);
        }
        
        for (final TestIndividual ind : population)
            assertEquals(1.0/POP_SIZE, count.get(ind)/(double)NUM_SAMPLES, EPSILON);
        
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testSelectIndividualIndex() {
        System.out.println("selectIndividualIndex");
        SUT = new RandomSelector<TestIndividual>(params.build(), BASE);
        final int NUM_SAMPLES = 10000000;
        final double EPSILON = 0.001;
        
        final int[] count = new int[POP_SIZE];
        for (int i = 0; i < NUM_SAMPLES; i++) {
            final int ind = SUT.selectIndividualIndex(population);
            count[ind]++;
        }
        
        for (int i = 0; i < POP_SIZE; i++)
            assertEquals(1.0/POP_SIZE, count[i]/(double)NUM_SAMPLES, EPSILON);
        
        assertTrue(SUT.repOK());
    }

    @Test
    public void testSelectMultipleIndividuals() {
        System.out.println("selectMultipleIndividuals");
        SUT = new RandomSelector<TestIndividual>(params.build(), BASE);
        final int NUM_SAMPLES = 10000000;
        final int INDS_PER_CALL = 10;
        final double EPSILON = 0.001;
        assert(NUM_SAMPLES%INDS_PER_CALL == 0);
        
        final Map<TestIndividual, Integer> count = new HashMap<TestIndividual, Integer>(POP_SIZE);
        for (final TestIndividual ind : population)
            count.put(ind, 0);
        for (int i = 0; i < NUM_SAMPLES/INDS_PER_CALL; i++) {
            final List<TestIndividual> inds = SUT.selectMultipleIndividuals(population, INDS_PER_CALL);
            for (final TestIndividual ind : inds)
                count.put(ind, count.get(ind) + 1);
        }
        
        for (final TestIndividual ind : population)
            assertEquals(1.0/POP_SIZE, count.get(ind)/(double)NUM_SAMPLES, EPSILON);
        
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testSelectMultipleIndividualIndices() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new RandomSelector<TestIndividual>(params.build(), BASE);
        final int NUM_SAMPLES = 10000000;
        final int INDS_PER_CALL = 10;
        final double EPSILON = 0.001;
        assert(NUM_SAMPLES%INDS_PER_CALL == 0);
        
        final int[] count = new int[POP_SIZE];
        for (int i = 0; i < NUM_SAMPLES/INDS_PER_CALL; i++) {
            final int[] inds = SUT.selectMultipleIndividualIndices(population, INDS_PER_CALL);
            for (final int ind : inds)
                count[ind]++;
        }
        
        for (int i = 0; i < POP_SIZE; i++)
            assertEquals(1.0/POP_SIZE, count[i]/(double)NUM_SAMPLES, EPSILON);
        
        assertTrue(SUT.repOK());
    }
}
