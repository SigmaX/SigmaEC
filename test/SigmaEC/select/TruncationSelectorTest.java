package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestDecoder;
import SigmaEC.test.TestIndividual;
import SigmaEC.test.TestObjective;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TruncationSelectorTest
{
    private TruncationSelector SUT;
    private List<TestIndividual> population = new ArrayList<TestIndividual>(10);
    // The four highest fitness values in the population
    final private double FIRST = 24.8;
    final private double SECOND = 15.0;
    final private double THIRD = 3.5;
    final private double FOURTH = 0.8;
    final private int SIZE = 10;
    
    public TruncationSelectorTest()
    {
    }
    
    @Before
    public void setUp()
    {
        SUT = new TruncationSelector(new TestObjective(), new TestDecoder(), new FitnessComparator(false));
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
    }

    /** Test of selectIndividual method, of class TruncationSelector. */
    @Test
    public void testSelectIndividual()
    {
        System.out.println("selectIndividual");
        Individual result = SUT.selectIndividual(population);
        assertEquals(SIZE, population.size());
        assertEquals(FIRST, ((TestIndividual)result).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectIndividual method, of class TruncationSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectIndividualIAE()
    {
        System.out.println("selectIndividual IAE");
        SUT.selectIndividual(new ArrayList());
    }

    /** Test of selectIndividual method, of class TruncationSelector. */
    @Test(expected = NullPointerException.class)
    public void testSelectIndividualNPE()
    {
        System.out.println("selectIndividual NPE");
        SUT.selectIndividual(null);
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsA()
    {
        System.out.println("selectMultipleIndividuals a");
        List result = SUT.selectMultipleIndividuals(population, 4);
        assertEquals(SIZE, population.size());
        assertEquals(4, result.size());
        assertEquals(FIRST, ((TestIndividual)result.get(0)).getTrait(), 0);
        assertEquals(SECOND, ((TestIndividual)result.get(1)).getTrait(), 0);
        assertEquals(THIRD, ((TestIndividual)result.get(2)).getTrait(), 0);
        assertEquals(FOURTH, ((TestIndividual)result.get(3)).getTrait(), 0);
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test
    public void testSelectMultipleIndividualsB()
    {
        System.out.println("selectMultipleIndividuals b");
        List result = SUT.selectMultipleIndividuals(population, SIZE);
        assertEquals(SIZE, population.size());
        assertEquals(SIZE, result.size());
        for (Individual i : population)
            assertTrue(result.contains(i));
        assertTrue(SUT.repOK());
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test(expected = NullPointerException.class)
    public void testSelectMultipleIndividualsNPE()
    {
        System.out.println("selectMultipleIndividuals NPE");
        SUT.selectMultipleIndividuals(null, SIZE);
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectMultipleIndividualsIAE1()
    {
        System.out.println("selectMultipleIndividuals IAE1");
        SUT.selectMultipleIndividuals(population, SIZE + 1);
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectMultipleIndividualsIAE2()
    {
        System.out.println("selectMultipleIndividuals IAE2");
        SUT.selectMultipleIndividuals(new ArrayList(), 2);
    }

    /** Test of selectMultipleIndividuals method, of class TruncationSelector. */
    @Test(expected = IllegalArgumentException.class)
    public void testSelectMultipleIndividualsIAE3()
    {
        System.out.println("selectMultipleIndividuals IAE3");
        SUT.selectMultipleIndividuals(population, 0);
    }
    
    /*
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        fail("Test case not implemented");
    } */
}
