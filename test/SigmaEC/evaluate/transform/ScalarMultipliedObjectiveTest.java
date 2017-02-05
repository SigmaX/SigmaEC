package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.real.SphereObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.represent.linear.DoubleVectorInitializer;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class ScalarMultipliedObjectiveTest {
    private final static String BASE = "base";
    
    private final Parameters.Builder builder;
    private final int numDimensions = 3;
    private final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective = new SphereObjective(numDimensions);
    
    public ScalarMultipliedObjectiveTest() {
        builder = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, ScalarMultipliedObjective.P_MULTIPLIER), "1.0")
                .registerInstance(Parameters.push(BASE, ScalarMultipliedObjective.P_OBJECTIVE), objective);
    }

    /** Test of getNumDimensions method, of class ScalarMultipliedObjective. */
    @Test
    public void testGetNumDimensions() {
        System.out.println("getNumDimensions");
        final ScalarMultipliedObjective instance = new ScalarMultipliedObjective(builder.build(), BASE);
        assertEquals(numDimensions, instance.getNumDimensions());
    }

    /** Test of fitness method, of class ScalarMultipliedObjective. */
    @Test
    public void testFitness() {
        System.out.println("fitness");
        testMultiplier(1.0);
        testMultiplier(-1.0);
        testMultiplier(100.07);
        testMultiplier(-100.07);
        testMultiplier(.07);
        testMultiplier(-.07);
        testMultiplier(30000);
    }
    
    private void testMultiplier(final double multiplier) {
        builder.setParameter("initializer", DoubleVectorInitializer.class.getName())
                .setParameter(Parameters.push("initializer", DoubleVectorInitializer.P_NUM_DIMENSIONS), String.valueOf(numDimensions))
                .setParameter(Parameters.push("initializer", DoubleVectorInitializer.P_DEFAULT_MAX_VALUE), "10")
                .setParameter(Parameters.push("initializer", DoubleVectorInitializer.P_DEFAULT_MIN_VALUE), "-10")
                .setParameter(Parameters.push("initializer", DoubleVectorInitializer.P_POPULATION_SIZE), "10")
                .setParameter(Parameters.push("initializer", DoubleVectorInitializer.P_RANDOM), SRandom.class.getName());
        final DoubleVectorInitializer initializer = new DoubleVectorInitializer(builder.build(), "initializer");
        final ScalarMultipliedObjective instance = new ScalarMultipliedObjective(builder
                .setParameter(Parameters.push(BASE, ScalarMultipliedObjective.P_MULTIPLIER), String.valueOf(multiplier))
                .build(), BASE);
        
        for (int i = 0; i < 50; i++) {
            final DoubleVectorIndividual ind = initializer.generateIndividual();
            double expResult = multiplier*objective.fitness(ind).asScalar();
            double result = instance.fitness(ind).asScalar();
            assertEquals(expResult, result, 0.000001);
        }
    }

    /** Test of equals method, of class ScalarMultipliedObjective. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        final ScalarMultipliedObjective instance1 = new ScalarMultipliedObjective(builder.build(), BASE);
        final ScalarMultipliedObjective instance2 = new ScalarMultipliedObjective(builder.build(), BASE);
        final ScalarMultipliedObjective instance3 = new ScalarMultipliedObjective(builder
                .setParameter(Parameters.push(BASE, ScalarMultipliedObjective.P_MULTIPLIER), "2.0")
                .build(), BASE);
        final ScalarMultipliedObjective instance4 = new ScalarMultipliedObjective(builder
                .setParameter(Parameters.push(BASE, ScalarMultipliedObjective.P_MULTIPLIER), "2.0")
                .build(), BASE);
        final ScalarMultipliedObjective instance5 = new ScalarMultipliedObjective(builder
                .setParameter(Parameters.push(BASE, ScalarMultipliedObjective.P_MULTIPLIER), "2.0")
                .registerInstance(Parameters.push(BASE, ScalarMultipliedObjective.P_OBJECTIVE), new SphereObjective(numDimensions + 1))
                .build(), BASE);
        
        assertEquals(instance1, instance1);
        assertEquals(instance2, instance2);
        assertEquals(instance3, instance3);
        assertEquals(instance4, instance4);
        assertEquals(instance5, instance5);
        
        assertEquals(instance1, instance2);
        assertEquals(instance1.hashCode(), instance2.hashCode());
        assertNotEquals(instance1, instance3);
        assertNotEquals(instance1, instance4);
        assertNotEquals(instance1, instance5);
        
        assertEquals(instance2, instance1);
        assertEquals(instance2.hashCode(), instance1.hashCode());
        assertNotEquals(instance2, instance3);
        assertNotEquals(instance2, instance4);
        assertNotEquals(instance2, instance5);
        
        assertEquals(instance3, instance4);
        assertEquals(instance3.hashCode(), instance4.hashCode());
        assertNotEquals(instance3, instance1);
        assertNotEquals(instance3, instance2);
        assertNotEquals(instance3, instance5);
        
        assertEquals(instance4, instance3);
        assertEquals(instance4.hashCode(), instance3.hashCode());
        assertNotEquals(instance4, instance1);
        assertNotEquals(instance4, instance2);
        assertNotEquals(instance4, instance5);
        
        assertNotEquals(instance5, instance1);
        assertNotEquals(instance5, instance2);
        assertNotEquals(instance5, instance3);
        assertNotEquals(instance5, instance4);
    }
    
}
