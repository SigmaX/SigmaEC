package SigmaEC.select;

import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Eric 'Siggy' Scott
 */
public class IterativeSelectorTest
{
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
        SUT = new IterativeSelector<TestIndividual>();
    }

    /** Test of selectIndividual method, of class IterativeSelector. */
    @Test
    public void testSelectIndividual() {
        System.out.println("selectIndividual");
        
        for (int j = 0; j < 5; j++)
            for (int i = 0; i < POP_SIZE; i++)
                assertEquals(i, (int) SUT.selectIndividual(population).getTrait());
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
        IterativeSelector SUT = new IterativeSelector<TestIndividual>();
        IterativeSelector gRef = new IterativeSelector<TestIndividual>();
        IterativeSelector bRef = new IterativeSelector<Individual>();
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