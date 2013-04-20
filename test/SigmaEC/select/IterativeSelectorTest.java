package SigmaEC.select;

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
}