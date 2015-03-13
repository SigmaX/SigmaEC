package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TruncationSelectorTest {
    private final static String BASE = "base";
    
    private Parameters.Builder params;
    private TruncationSelector<TestIndividual, Double> SUT;
    
    private final List<TestIndividual> population = new ArrayList<TestIndividual>(10);
    // The four highest fitness values in the population
    private final static double FIRST = 24.8;
    private final static int FIRST_INDEX = 3;
    private final static double SECOND = 15.0;
    private final static int SECOND_INDEX = 6;
    private final static double THIRD = 3.5;
    private final static int THIRD_INDEX = 8;
    private final static double FOURTH = 0.8;
    private final static int FOURTH_INDEX = 7;
    private final static int SIZE = 10;
    
    public TruncationSelectorTest() { }
    
    @Before
    public void setUp() {
        params = getParams();
        population.clear();
        population.add(new TestIndividual(0.5));
        population.add(new TestIndividual(0.4));
        population.add(new TestIndividual(0.5));
        population.add(new TestIndividual(FIRST));
        population.add(new TestIndividual(0.1));
        population.add(new TestIndividual(0.2));
        population.add(new TestIndividual(SECOND));
        population.add(new TestIndividual(FOURTH));
        population.add(new TestIndividual(THIRD));
        population.add(new TestIndividual(0.5));
        assertEquals(SIZE, population.size());
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, TruncationSelector.P_COMPARATOR), "SigmaEC.select.FitnessComparator")
                .setParameter(Parameters.push(Parameters.push(BASE, TruncationSelector.P_COMPARATOR), FitnessComparator.P_MINIMIZE), "false");
    }

    /** Test of selectIndividual method, of class TruncationSelector. */
    @Test
    public void testSelectIndividual() {
        System.out.println("selectIndividual");
        SUT = new TruncationSelector(params.build(), BASE);
        final TestIndividual result = SUT.selectIndividual(population);
        assertEquals(FIRST, ((TestIndividual)result).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectIndividual method, of class TruncationSelector. */
    @Test
    public void testSelectIndividualIndex() {
        System.out.println("selectIndividualIndex");
        SUT = new TruncationSelector(params.build(), BASE);
        final int result = SUT.selectIndividualIndex(population);
        assertEquals(FIRST_INDEX, result);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsA() {
        System.out.println("selectMultipleIndividuals");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> result = SUT.selectMultipleIndividuals(population, SIZE);
        assertEquals(SIZE, population.size());
        assertEquals(SIZE, result.size());
        for (final Individual i : population)
            assertTrue(result.contains(i));
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualIndicesA() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final int[] result = SUT.selectMultipleIndividualIndices(population, SIZE);
        assertEquals(SIZE, result.length);
        Arrays.sort(result);
        for (int i = 0; i < population.size(); i++)
            assertTrue(Arrays.binarySearch(result, i) >= 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsB() {
        System.out.println("selectMultipleIndividuals");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> result = SUT.selectMultipleIndividuals(population, 4);
        assertEquals(4, result.size());
        assertEquals(FIRST, result.get(0).getTrait(), 0);
        assertEquals(SECOND, result.get(1).getTrait(), 0);
        assertEquals(THIRD, result.get(2).getTrait(), 0);
        assertEquals(FOURTH, result.get(3).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualIndicesB() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final int[] result = SUT.selectMultipleIndividualIndices(population, 4);
        assertEquals(4, result.length);
        assertEquals(FIRST_INDEX, result[0]);
        assertEquals(SECOND_INDEX, result[1]);
        assertEquals(THIRD_INDEX, result[2]);
        assertEquals(FOURTH_INDEX, result[3]);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsC() {
        System.out.println("selectMultipleIndividuals");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> result = SUT.selectMultipleIndividuals(population, 2);
        assertEquals(2, result.size());
        assertEquals(FIRST, result.get(0).getTrait(), 0);
        assertEquals(SECOND, result.get(1).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualIndicesC() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final int[] result = SUT.selectMultipleIndividualIndices(population, 2);
        assertEquals(2, result.length);
        assertEquals(FIRST_INDEX, result[0]);
        assertEquals(SECOND_INDEX, result[1]);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsD() {
        System.out.println("selectMultipleIndividuals");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> result = SUT.selectMultipleIndividuals(population, 1);
        assertEquals(1, result.size());
        assertEquals(FIRST, result.get(0).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualIndicesD() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final int[] result = SUT.selectMultipleIndividualIndices(population, 1);
        assertEquals(1, result.length);
        assertEquals(FIRST_INDEX, result[0]);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsE() {
        System.out.println("selectMultipleIndividuals");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> result = SUT.selectMultipleIndividuals(population, 3);
        assertEquals(3, result.size());
        assertEquals(FIRST, result.get(0).getTrait(), 0);
        assertEquals(SECOND, result.get(1).getTrait(), 0);
        assertEquals(THIRD, result.get(2).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualIndicesE() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final int[] result = SUT.selectMultipleIndividualIndices(population, 3);
        assertEquals(3, result.length);
        assertEquals(FIRST_INDEX, result[0]);
        assertEquals(SECOND_INDEX, result[1]);
        assertEquals(THIRD_INDEX, result[2]);
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testSelectMultipleIndividualIndicesF() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> pop = new ArrayList() {{
           add(new TestIndividual(SECOND)); 
           add(new TestIndividual(FIRST));
        }};
        final int[] result = SUT.selectMultipleIndividualIndices(pop, 2);
        assertEquals(2, result.length);
        assertEquals(1, result[0]);
        assertEquals(0, result[1]);
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testSelectMultipleIndividualIndicesG() {
        System.out.println("selectMultipleIndividualIndices");
        SUT = new TruncationSelector(params.build(), BASE);
        final List<TestIndividual> pop = new ArrayList() {{
           add(new TestIndividual(SECOND)); 
           add(new TestIndividual(FIRST));
           add(new TestIndividual(THIRD));
        }};
        final int[] result = SUT.selectMultipleIndividualIndices(pop, 2);
        assertEquals(2, result.length);
        assertEquals(1, result[0]);
        assertEquals(0, result[1]);
        assertTrue(SUT.repOK());
    }
}
