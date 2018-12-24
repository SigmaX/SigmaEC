package SigmaEC.select;

import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class SUSSelectorTest {
    private final static String BASE = "base";
    private List<TestIndividual> population;
    
    private final static double FIRST_FITNESS = 1.0;
    private final static int FIRST_INDEX = 1;
    private final static TestIndividual FIRST = new TestIndividual(FIRST_FITNESS);
    private final static double SECOND_FITNESS = 3.0;
    private final static int SECOND_INDEX = 0;
    private final static TestIndividual SECOND = new TestIndividual(SECOND_FITNESS);
    private final static double THIRD_FITNESS = 5.0;
    private final static int THIRD_INDEX = 3;
    private final static TestIndividual THIRD = new TestIndividual(THIRD_FITNESS);
    private final static double FOURTH_FITNESS = 8.0;
    private final static int FOURTH_INDEX = 2;
    private final static TestIndividual FOURTH = new TestIndividual(FOURTH_FITNESS);
    private final static double FIFTH_FITNESS = 8.1;
    private final static int FIFTH_INDEX = 4;
    private final static TestIndividual FIFTH = new TestIndividual(FIFTH_FITNESS);
    
    public SUSSelectorTest() { }
    
    @Before
    public void setUp() {
        population = new ArrayList<TestIndividual>() {{
                add(SECOND);
                add(FIRST);
                add(FOURTH);
                add(THIRD);
                add(FIFTH);
        }};
    }
    
    private static Parameters.Builder getRankingParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, SUSSelector.P_RANDOM), "SigmaEC.SRandom")
                .setParameter(Parameters.push(BASE, SUSSelector.P_PROBABILITY), "SigmaEC.select.RankingSelectionProbability")
                .setParameter(Parameters.push(Parameters.push(BASE, SUSSelector.P_PROBABILITY), RankingSelectionProbability.P_POWER), "1")
                .setParameter(Parameters.push(Parameters.push(BASE, SUSSelector.P_PROBABILITY), RankingSelectionProbability.P_COMPARATOR), "SigmaEC.select.ScalarFitnessComparator");
    }

    /**
     * Test of selectIndividualIndex method, of class SUSSelector.
     */
    @Test
    public void testSelectIndividualIndex() {
        System.out.println("selectIndividualIndex (linear ranking)");
        final SUSSelector<TestIndividual, Double> instance = new SUSSelector<TestIndividual, Double>(getRankingParams().build(), BASE);
        final int n = population.size();
        final Map<Integer, Integer> counts = new HashMap<Integer, Integer>() {{
           for (int i = 0; i < n; i++)
               put(i, 0);
        }};
        final int NUM_CALLS = 300000;
        for (int i = 0; i < NUM_CALLS; i++) {
            final int j = instance.selectIndividualIndex(population);
            counts.put(j, counts.get(j) + 1);
        }
        final double PRECISION = 0.001;
        final double SLOPE = 2.0/(n*(n+1));
        assertEquals(SLOPE, (double) counts.get(FIRST_INDEX)/NUM_CALLS, PRECISION);
        assertEquals(2*SLOPE, (double) counts.get(SECOND_INDEX)/NUM_CALLS, PRECISION);
        assertEquals(3*SLOPE, (double) counts.get(THIRD_INDEX)/NUM_CALLS, PRECISION);
        assertEquals(4*SLOPE, (double) counts.get(FOURTH_INDEX)/NUM_CALLS, PRECISION);
        assertEquals(5*SLOPE, (double) counts.get(FIFTH_INDEX)/NUM_CALLS, PRECISION);
    }

    /**
     * Test of selectMultipleIndividualIndices method, of class SUSSelector.
     */
    @Test
    public void testSelectMultipleIndividualIndices() {
        System.out.println("selectMultipleIndividualIndices (linear ranking)");
        final SUSSelector<TestIndividual, Double> instance = new SUSSelector<TestIndividual, Double>(getRankingParams().build(), BASE);
        final int n = population.size();
        final Map<Integer, Integer> counts = new HashMap<Integer, Integer>() {{
           for (int i = 0; i < n; i++)
               put(i, 0);
        }};
        final int NUM_CALLS = 100000;
        final int INDS_PER_CALL = 3;
        for (int i = 0; i < NUM_CALLS; i++) {
            final int[] result = instance.selectMultipleIndividualIndices(population, INDS_PER_CALL);
            for (final int j : result)
                counts.put(j, counts.get(j) + 1);
        }
        final int totalCalls = (NUM_CALLS*INDS_PER_CALL);
        final double PRECISION = 0.001;
        final double SLOPE = 2.0/(n*(n+1));
        assertEquals(SLOPE, (double) counts.get(FIRST_INDEX)/totalCalls, PRECISION);
        assertEquals(2*SLOPE, (double) counts.get(SECOND_INDEX)/totalCalls, PRECISION);
        assertEquals(3*SLOPE, (double) counts.get(THIRD_INDEX)/totalCalls, PRECISION);
        assertEquals(4*SLOPE, (double) counts.get(FOURTH_INDEX)/totalCalls, PRECISION);
        assertEquals(5*SLOPE, (double) counts.get(FIFTH_INDEX)/totalCalls, PRECISION);
    }
}
