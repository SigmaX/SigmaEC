package SigmaEC.select;

import SigmaEC.represent.Individual;
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
public class TournamentSelectorTest {
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
    
    public TournamentSelectorTest() { }
    
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
    private static TournamentSelector<TestIndividual, Double> getInstance(final int tournamentSize) {
        final Parameters params = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, TournamentSelector.P_RANDOM), "SigmaEC.SRandom")
                .setParameter(Parameters.push(BASE, TournamentSelector.P_TOURNAMENT_SIZE), String.valueOf(tournamentSize))
                .setParameter(Parameters.push(BASE, TournamentSelector.P_COMPARATOR), "SigmaEC.select.FitnessComparator")
                .setParameter(Parameters.push(Parameters.push(BASE, TournamentSelector.P_COMPARATOR), FitnessComparator.P_DECODER), "SigmaEC.test.TestDecoder")
                .setParameter(Parameters.push(Parameters.push(BASE, TournamentSelector.P_COMPARATOR), FitnessComparator.P_OBJECTIVE), "SigmaEC.test.TestObjective")
                .build();
        return new TournamentSelector<TestIndividual, Double>(params, BASE);
    }
    
    // The expected behavior of tournament selection shouldmatch a ranking distribution -- so we compare against a RankingSelectionProbability.
    private static RankingSelectionProbability<TestIndividual, Double> getRanker(final int power) {
        final Parameters params = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, RankingSelectionProbability.P_POWER), String.valueOf(power))
                .setParameter(Parameters.push(BASE, RankingSelectionProbability.P_COMPARATOR), "SigmaEC.select.FitnessComparator")
                .setParameter(Parameters.push(Parameters.push(BASE, RankingSelectionProbability.P_COMPARATOR), FitnessComparator.P_DECODER), "SigmaEC.test.TestDecoder")
                .setParameter(Parameters.push(Parameters.push(BASE, RankingSelectionProbability.P_COMPARATOR), FitnessComparator.P_OBJECTIVE), "SigmaEC.test.TestObjective")
                .build();
        return new RankingSelectionProbability<TestIndividual, Double>(params, BASE);
    }

    /** Test of selectIndividual method, of class TournamentSelector. */
    @Test
    public void testSelectIndividual() {
        System.out.println("selectIndividual");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of selectMultipleIndividuals method, of class TournamentSelector. */
    @Test
    public void testSelectMultipleIndividuals() {
        System.out.println("selectMultipleIndividuals");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of selectIndividualIndex method, of class TournamentSelector. */
    @Test
    public void testSelectIndividualIndex() {
        System.out.println("selectIndividualIndex");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of selectMultipleIndividualIndices method, of class TournamentSelector. */
    @Test
    public void testSelectMultipleIndividualIndices() {
        System.out.println("selectMultipleIndividualIndices");
        final TournamentSelector<TestIndividual, Double> instance = getInstance(2);
        final int n = population.size();
        final Map<Integer, Integer> counts = new HashMap<Integer, Integer>() {{
           for (int i = 0; i < n; i++)
               put(i, 0);
        }};
        final int NUM_CALLS = 10000000;
        final int INDS_PER_CALL = 3;
        for (int i = 0; i < NUM_CALLS; i++) {
            final int[] result = instance.selectMultipleIndividualIndices(population, INDS_PER_CALL);
            for (final int j : result)
                counts.put(j, counts.get(j) + 1);
        }
        final int totalCalls = (NUM_CALLS*INDS_PER_CALL);
        final double PRECISION = 0.001;
        
        final RankingSelectionProbability<TestIndividual, Double> ranker = getRanker(2);
        final double[] pmf = ranker.probability(population);
        for (int i = 0; i < n; i++)
            assertEquals(pmf[i], (double) counts.get(i)/totalCalls, PRECISION);
    }
    
}
