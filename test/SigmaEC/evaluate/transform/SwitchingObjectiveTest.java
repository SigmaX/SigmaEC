package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ConstantObjective;
import SigmaEC.represent.Individual;
import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class SwitchingObjectiveTest {
    final private static String BASE = "base";
    final private Parameters.Builder builder = new Parameters.Builder(new Properties())
            .setParameter(Parameters.push(BASE, SwitchingObjective.P_INTERVAL), "10")
            .setParameter(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), String.format("%s, %s, %s", ConstantObjective.class.getName(), ConstantObjective.class.getName(), ConstantObjective.class.getName()))
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "0"), ConstantObjective.P_NUM_DIMENSIONS), "5")
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "0"), ConstantObjective.P_VALUE), "10")
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "1"), ConstantObjective.P_NUM_DIMENSIONS), "5")
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "1"), ConstantObjective.P_VALUE), "20")
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "2"), ConstantObjective.P_NUM_DIMENSIONS), "5")
            .setParameter(Parameters.push(Parameters.push(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), "2"), ConstantObjective.P_VALUE), "30");
    
    public SwitchingObjectiveTest() { }

    // <editor-fold defaultstate="collapsed" desc="No repeat">
    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness1() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness2() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        instance.setGeneration(9);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness3() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        instance.setGeneration(10);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness4() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        instance.setGeneration(19);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness5() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        instance.setGeneration(20);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 30.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness6() {
        System.out.println("fitness");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        instance.setGeneration(40);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 30.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="With Repeat">
    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness7() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness8() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(9);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness9() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(10);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness10() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(19);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness11() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(20);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 30.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness12() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(29);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 30.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness13() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(30);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness14() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(39);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 10.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness15() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(40);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness16() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(49);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 20.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of fitness method, of class SwitchingObjective. */
    @Test
    public void testFitness17() {
        System.out.println("fitness (with repeat)");
        final SwitchingObjective instance = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_REPEAT), "true")
                .build(), BASE);
        instance.setGeneration(50);
        final Individual ind = new TestIndividual(0.0);
        double expResult = 30.0;
        double result = instance.fitness(ind);
        assertEquals(expResult, result, 0.0);
    }

    /** Test of getNumDimensions method, of class SwitchingObjective. */
    @Test
    public void testGetNumDimensions() {
        System.out.println("getNumDimensions");
        final SwitchingObjective instance = new SwitchingObjective(builder.build(), BASE);
        final int expResult = 5;
        final int result = instance.getNumDimensions();
        assertEquals(expResult, result);
    }
    // </editor-fold>
    
    /** Test of equals method, of class SwitchingObjective. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        final SwitchingObjective sut1 = new SwitchingObjective(builder.build(), BASE);
        final SwitchingObjective sut2 = new SwitchingObjective(builder.build(), BASE);
        final SwitchingObjective sut3 = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_INTERVAL), "5")
                .build(), BASE);
        final SwitchingObjective sut4 = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_INTERVAL), "5")
                .build(), BASE);
        final SwitchingObjective sut5 = new SwitchingObjective(builder
                .setParameter(Parameters.push(BASE, SwitchingObjective.P_OBJECTIVES), String.format("%s, %s", ConstantObjective.class.getName(), ConstantObjective.class.getName()))
                .build(), BASE);
        
        assertTrue(sut1.equals(sut1));
        assertTrue(sut2.equals(sut2));
        assertTrue(sut3.equals(sut3));
        assertTrue(sut4.equals(sut4));
        assertTrue(sut5.equals(sut5));
        
        assertTrue(sut1.equals(sut2));
        assertEquals(sut1.hashCode(), sut2.hashCode());
        assertFalse(sut1.equals(sut3));
        assertFalse(sut1.equals(sut4));
        assertFalse(sut1.equals(sut5));
        
        assertTrue(sut2.equals(sut1));
        assertFalse(sut2.equals(sut3));
        assertFalse(sut2.equals(sut4));
        assertFalse(sut2.equals(sut5));
        
        assertTrue(sut3.equals(sut4));
        assertEquals(sut3.hashCode(), sut4.hashCode());
        assertFalse(sut3.equals(sut1));
        assertFalse(sut3.equals(sut2));
        assertFalse(sut3.equals(sut5));
        
        assertTrue(sut4.equals(sut3));
        assertFalse(sut4.equals(sut1));
        assertFalse(sut4.equals(sut2));
        assertFalse(sut4.equals(sut5));
        
        assertFalse(sut5.equals(sut1));
        assertFalse(sut5.equals(sut2));
        assertFalse(sut5.equals(sut3));
        assertFalse(sut5.equals(sut4));
    }

}
