package SigmaEC.represent.cgp;

import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eric
 */
public class CartesianIntVectorConstraintTest {
    private final static String BASE = "base";
    
    public CartesianIntVectorConstraintTest() {
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), "SigmaEC.represent.cgp.CGPParameters")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_NUM_INPUTS), "2")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_NUM_OUTPUTS), "1")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_NUM_LAYERS), "2")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_NUM_NODES_PER_LAYER), "2")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_MAX_ARITY), "2")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_LEVELS_BACK), "1")
                .setParameter(Parameters.push(Parameters.push(BASE, CartesianIntVectorConstraint.P_CGP_PARMAETERS), CGPParameters.P_NUM_PRIMITIVES), "4");
    }

    @Test
    public void testIsViolated1() {
        System.out.println("isViolated");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 1,
                                                                                           1, 2, 3,   0, 2, 2,
                                                                                           4}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = false;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated2() {
        System.out.println("isViolated (incorrect dimensions)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 1,
                                                                                           1, 2, 3,   0, 2, 2}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated3() {
        System.out.println("isViolated (incorrect dimensions)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 1,
                                                                                           1, 2, 3,   0, 2, 2,
                                                                                           0, 4, 4,   1, 5, 5,
                                                                                           6}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated4() {
        System.out.println("isViolated (cross edge)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 2,
                                                                                           1, 2, 3,   0, 2, 2,
                                                                                           4}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated5() {
        System.out.println("isViolated (forward edge)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 3,
                                                                                           1, 2, 3,   0, 2, 2,
                                                                                           4}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated6() {
        System.out.println("isViolated (back edge beyond levelsBack)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 0, 1,   0, 0, 1,
                                                                                           1, 2, 3,   0, 2, 0,
                                                                                           4}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated7() {
        System.out.println("isViolated (back edge beyond levelsBack)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 1, 0,   0, 1, 1,
                                                                                           0, 0, 3,   1, 2, 2,
                                                                                           0}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = true;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    @Test
    public void testIsViolated8() {
        System.out.println("isViolated (permit output to be sourced from beyond levelsBack)");
        final IntVectorIndividual individual = new IntVectorIndividual.Builder(new int[] { 0, 1, 0,   0, 1, 1,
                                                                                           0, 2, 3,   1, 2, 2,
                                                                                           0}).build();
        final CartesianIntVectorConstraint instance = new CartesianIntVectorConstraint(getParams().build(), BASE);
        final boolean expResult = false;
        final boolean result = instance.isViolated(individual);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
}
