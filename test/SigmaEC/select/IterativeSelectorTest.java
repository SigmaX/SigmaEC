package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Eric 'Siggy' Scott
 */
public class IterativeSelectorTest {
    IterativeSelector<TestIndividual> SUT;
    List<TestIndividual> population;
    final static int POP_SIZE = 10;
    
    public IterativeSelectorTest() {
    }
    
    @Before
    public void setUp() {
        population = new ArrayList<TestIndividual>(POP_SIZE);
        for (int i = 0; i < POP_SIZE; i++)
            population.add(new TestIndividual(i));
        SUT = new IterativeSelector<TestIndividual>( new Parameters.Builder(new Properties()).build(), "base");
    }

    /** Test of selectIndividual method, of class IterativeSelector. */
    @Test
    public void testSelectIndividual() {
        System.out.println("selectIndividual");
        
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < POP_SIZE; i++)
                assertEquals(i, (int) SUT.selectIndividual(population).getTrait());
    }

    /** Test of selectIndividual method, of class IterativeSelector. */
    @Test
    public void selectMultipleIndividuals() {
        System.out.println("selectMultipleIndividuals");
        
        for (int j = 0; j < 5; j++) {
            final List<TestIndividual> inds = SUT.selectMultipleIndividuals(population, POP_SIZE);
            assertEquals(POP_SIZE, inds.size());
            for (int i = 0; i < POP_SIZE; i++)
                assertEquals(i, (int) inds.get(i).getTrait());
        }
    }

    /** Test of selectIndividual method, of class IterativeSelector. */
    @Test
    public void selectMultipleIndividualIndices() {
        System.out.println("selectMultipleIndividualIndices");
        
        for (int j = 0; j < 5; j++) {
            final int[] inds = SUT.selectMultipleIndividualIndices(population, POP_SIZE);
            assertEquals(POP_SIZE, inds.length);
            for (int i = 0; i < POP_SIZE; i++)
                assertEquals(i, inds[i]);
        }
    }

    /** Test of selectIndividual method, of class IterativeSelector. */
    @Test
    public void testSelectIndividualIndex() {
        System.out.println("selectIndividualIndex");
        
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < POP_SIZE; i++)
                assertEquals(i, (int) SUT.selectIndividualIndex(population));
    }
    
    @Test
    public void testToString()
    {
        System.out.println("toString");
        assertEquals("[IterativeSelector]", SUT.toString());
        assertTrue(SUT.repOK());
    }
    
    @Test
    public void testEquals()
    {
        System.out.println("equals & hashcode");
        final IterativeSelector SUT = new IterativeSelector<TestIndividual>();
        final IterativeSelector gRef = new IterativeSelector<TestIndividual>();
        final IterativeSelector bRef = new IterativeSelector<Individual>();
        bRef.selectIndividual(new ArrayList<TestIndividual>() {{ add(new TestIndividual(0)); }});
        
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
}