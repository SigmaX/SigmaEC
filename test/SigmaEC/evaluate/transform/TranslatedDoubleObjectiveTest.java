package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.RosenbrockObjective;
import SigmaEC.evaluate.objective.SphereObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TranslatedDoubleObjectiveTest {
    private static String BASE = "base";
    private Properties properties;
    
    public TranslatedDoubleObjectiveTest() { }
    
    @Before
    public void setUp() {
        properties = new Properties();
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.class.getName());
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "2");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "5,5");
    }

    @Test (expected=NullPointerException.class)
    public void testConstructorNPE1() {
        System.out.println("constructor NPE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(null, new SphereObjective(2));
    }

    @Test (expected=NullPointerException.class)
    public void testConstructorNPE2() {
        System.out.println("constructor NPE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5,5}, null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void testConstructorIAE1() {
        System.out.println("constructor ISE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5}, new SphereObjective(2));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testConstructorIAE2() {
        System.out.println("constructor ISE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5,5}, new SphereObjective(20));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testConstructorIAE3() {
        System.out.println("constructor ISE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5,Double.NaN}, new SphereObjective(2));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testConstructorIAE4() {
        System.out.println("constructor ISE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5,Double.POSITIVE_INFINITY}, new SphereObjective(2));
    }

    @Test (expected=IllegalArgumentException.class)
    public void testConstructorIAE5() {
        System.out.println("constructor ISE");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {Double.NaN}, new SphereObjective(2));
    }
    
    @Test (expected=IllegalStateException.class)
    public void testParameterConstructorISE1() {
        System.out.println("parameter constructor ISE");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "10");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
    }

    @Test (expected=IllegalStateException.class)
    public void testParameterConstructorISE2() {
        System.out.println("parameter constructor ISE");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "5,5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
    }

    @Test
    public void testGetNumDimensions1() {
        System.out.println("getNumDimensions");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        int expResult = 2;
        int result = sut.getNumDimensions();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNumDimensions2() {
        System.out.println("getNumDimensions");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "10");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "5,5,5,5,5,5,5,5,5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        int expResult = 10;
        int result = sut.getNumDimensions();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFitness1() {
        System.out.println("fitness");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { 0, 0 });
        double expResult = 50.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness2() {
        System.out.println("fitness");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { -10, -10 });
        double expResult = 50.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness3() {
        System.out.println("fitness");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { 10, -10 });
        double expResult = 250.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness4() {
        System.out.println("fitness");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { -5, 0 });
        double expResult = 25.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness5() {
        System.out.println("fitness");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "3");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "-5,-5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { 0, 0, 0});
        double expResult = 75.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness6() {
        System.out.println("fitness");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "3");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "-5,-5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { -10, -10, 10 });
        double expResult = 675;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness7() {
        System.out.println("fitness");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "3");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "-5,-5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { 10, -10, -5 });
        double expResult = 250.0;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }
    
    @Test
    public void testFitness8() {
        System.out.println("fitness");
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "3");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "-5,-5,5");
        final Parameters parameters = new Parameters(properties);
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(parameters, BASE);
        
        final DoubleVectorIndividual ind = new DoubleVectorIndividual(new double[] { -5, 0, 0 });
        double expResult = 150;
        double result = sut.fitness(ind);
        assertEquals(expResult, result, 0.0000001);
    }

    @Test
    public void testSetGeneration() {
        System.out.println("setGeneration");
        final int i = 0;
        // Mocking the wrapped objective to confirm that its setGeneration method is called.
        final Mockery context = new Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        final ObjectiveFunction<DoubleVectorIndividual> objective = context.mock(ObjectiveFunction.class);
        context.checking(new Expectations() {{
            allowing (objective).getNumDimensions(); will(returnValue(2));
            oneOf (objective).setGeneration(i);
        }});
        TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new double[] {5, 5}, objective);
        
        sut.setGeneration(i);
        context.assertIsSatisfied();
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        final TranslatedDoubleObjective sut = new TranslatedDoubleObjective(new Parameters(properties), BASE);
        final TranslatedDoubleObjective sut1 = new TranslatedDoubleObjective(new Parameters(properties), BASE);
        
        properties.setProperty(Parameters.push(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), SphereObjective.P_NUM_DIMENSIONS), "3");
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OFFSET), "-5,-5,5");
        final TranslatedDoubleObjective sut2 = new TranslatedDoubleObjective(new Parameters(properties), BASE);
        
        assert(SphereObjective.P_NUM_DIMENSIONS.equals(RosenbrockObjective.P_NUM_DIMENSIONS));
        properties.setProperty(Parameters.push(BASE, TranslatedDoubleObjective.P_OBJECTIVE), RosenbrockObjective.class.getName());
        final TranslatedDoubleObjective sut3 = new TranslatedDoubleObjective(new Parameters(properties), BASE);
        
        assertEquals(sut, sut);
        assertEquals(sut1, sut1);
        assertEquals(sut2, sut2);
        assertEquals(sut3, sut3);
        
        assertEquals(sut, sut1);
        assertEquals(sut1, sut);
        
        assertFalse(sut.equals(sut2));
        assertFalse(sut.equals(sut3));
        assertFalse(sut2.equals(sut3));
        
        assertEquals(sut.hashCode(), sut1.hashCode());
        
    }
}