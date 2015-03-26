package SigmaEC.util;

import SigmaEC.evaluate.objective.real.SphereObjective;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class ParametersTest {
    private Parameters sut;
    private SphereObjective sphereObjective;
    
    public ParametersTest() {
    }
    
    @Before
    public void setUp() {
        sphereObjective = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "5")
                .build(), "obj");
        
        sut = new Parameters.Builder(new Properties())
                .setParameter("alpha", "-5")
                .setParameter("beta", "0.72")
                .setParameter("gamma", "true")
                .setParameter("epsilon", "false")
                .setParameter("alphaRef", "%alpha")
                .setParameter("alphaRefRef", "%alphaRef")
                .setParameter("alphaExp", "$(7 + 1)*%alpha - 3")
                .setParameter("alphaExpRef", "%alphaExp")
                .setParameter("betaRef", "%beta")
                .setParameter("betaRefRef", "%betaRef")
                .setParameter("betaExp", "$%beta/2 - 0.08")
                .setParameter("betaExpRef", "%betaExp")
                .setParameter("gammaRef", "%gamma")
                .setParameter("gammaRefRef", "%gammaRef")
                .setParameter("epsilonRef", "%epsilon")
                .setParameter("epsilonRefRef", "%epsilonRef")
                .setParameter("dArray", "0.1, 0.2,0.3, 0.4, 0.5")
                .setParameter("dArrayRef", "%dArray")
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.SphereObjective")
                .setParameter("test.objective1.numDimensions", "10")
                .setParameter("expRef", "$%betaExp*10")
                .registerInstance(Parameters.push("test", "objective2"), sphereObjective)
                .build();
    }
    
    @After
    public void tearDown() {
    }

    /** Test of push method, of class Parameters. */
    @Test
    public void testPush() {
        System.out.println("push");
        String expResult = "base.param";
        String result = Parameters.push("base", "param");
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined1() {
        System.out.println("isDefined");
        String parameterName = "omega";
        boolean expResult = false;
        boolean result = sut.isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined2() {
        System.out.println("isDefined");
        String parameterName = "alpha";
        boolean expResult = true;
        boolean result = sut.isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined3() {
        System.out.println("isDefined reference");
        String parameterName = "alphaRef";
        boolean expResult = true;
        boolean result = sut.isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined4() {
        System.out.println("isDefined double reference");
        String parameterName = "alphaRefRef";
        boolean expResult = true;
        boolean result = sut.isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter1() {
        System.out.println("getIntParameter");
        String parameterName = "alpha";
        int expResult = -5;
        int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter2() {
        System.out.println("getIntParameter reference");
        String parameterName = "alphaRef";
        int expResult = -5;
        int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter3() {
        System.out.println("getIntParameter double reference");
        String parameterName = "alphaRefRef";
        int expResult = -5;
        int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter4() {
        System.out.println("getIntParameter expression");
        String parameterName = "alphaExp";
        int expResult = -43;
        int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter5() {
        System.out.println("getIntParameter reference to expression");
        String parameterName = "alphaExpRef";
        int expResult = -43;
        int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter1() {
        System.out.println("getOptionalIntParameter");
        String parameterName = "alpha";
        Option expResult = new Option<Integer>(-5);
        Option result = sut.getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter2() {
        System.out.println("getOptionalIntParameter");
        String parameterName = "omega";
        Option expResult = Option.NONE;
        Option result = sut.getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter3() {
        System.out.println("getOptionalIntParameter reference");
        String parameterName = "alphaRef";
        Option expResult = new Option<Integer>(-5);
        Option result = sut.getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter4() {
        System.out.println("getOptionalIntParameter expression");
        String parameterName = "alphaExp";
        Option expResult = new Option<Integer>(-43);
        Option result = sut.getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter1() {
        System.out.println("getBooleanParameter");
        String parameterName = "gamma";
        boolean expResult = true;
        boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter2() {
        System.out.println("getBooleanParameter reference");
        String parameterName = "gammaRef";
        boolean expResult = true;
        boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter3() {
        System.out.println("getBooleanParameter");
        String parameterName = "epsilon";
        boolean expResult = false;
        boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter4() {
        System.out.println("getBooleanParameter reference");
        String parameterName = "epsilonRef";
        boolean expResult = false;
        boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter1() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "gamma";
        Option expResult = new Option<Boolean>(true);
        Option result = sut.getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter2() {
        System.out.println("getOptionalBooleanParameter reference");
        String parameterName = "gammaRef";
        Option expResult = new Option<Boolean>(true);
        Option result = sut.getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter3() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "epsilon";
        Option expResult = new Option<Boolean>(false);
        Option result = sut.getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter4() {
        System.out.println("getOptionalBooleanParameter reference");
        String parameterName = "epsilonRef";
        Option expResult = new Option<Boolean>(false);
        Option result = sut.getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter5() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "omega";
        Option expResult = Option.NONE;
        Option result = sut.getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter1() {
        System.out.println("getDoubleParameter");
        String parameterName = "beta";
        double expResult = 0.72;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter2() {
        System.out.println("getDoubleParameter reference");
        String parameterName = "betaRef";
        double expResult = 0.72;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter3() {
        System.out.println("getDoubleParameter double reference");
        String parameterName = "betaRefRef";
        double expResult = 0.72;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter4() {
        System.out.println("getDoubleParameter expression");
        String parameterName = "betaExp";
        double expResult = 0.28;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter5() {
        System.out.println("getDoubleParameter reference to expression");
        String parameterName = "betaExpRef";
        double expResult = 0.28;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter6() {
        System.out.println("getDoubleParameter");
        String parameterName = "alpha";
        double expResult = -5.0;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter7() {
        System.out.println("getDoubleParameter reference");
        String parameterName = "alphaRef";
        double expResult = -5.0;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter8() {
        System.out.println("getDoubleParameter expression with reference to expression");
        String parameterName = "expRef";
        double expResult = 2.8;
        double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }

    /** Test of getOptionalDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter1() {
        System.out.println("getOptionalDoubleParameter reference");
        String parameterName = "betaRef";
        Option expResult = new Option<Double>(0.72);
        Option result = sut.getOptionalDoubleParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter2() {
        System.out.println("getOptionalDoubleParameter");
        String parameterName = "omega";
        Option expResult = Option.NONE;
        Option result = sut.getOptionalDoubleParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter1() {
        System.out.println("getDoubleArrayParameter");
        String parameterName = "dArray";
        double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] result = sut.getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }

    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter2() {
        System.out.println("getDoubleArrayParameter reference");
        String parameterName = "dArrayRef";
        double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] result = sut.getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter1() {
        System.out.println("getStringParameter");
        String parameterName = "gamma";
        String expResult = "true";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter2() {
        System.out.println("getStringParameter");
        String parameterName = "epsilon";
        String expResult = "false";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter3() {
        System.out.println("getStringParameter reference");
        String parameterName = "gammaRef";
        String expResult = "true";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter4() {
        System.out.println("getStringParameter reference");
        String parameterName = "epsilonRef";
        String expResult = "false";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter5() {
        System.out.println("getStringParameter double reference");
        String parameterName = "gammaRefRef";
        String expResult = "true";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter6() {
        System.out.println("getStringParameter double reference");
        String parameterName = "epsilonRefRef";
        String expResult = "false";
        String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter1() {
        System.out.println("getOptionalStringParameter");
        String parameterName = "alpha";
        Option expResult = new Option<String>("-5");
        Option result = sut.getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter2() {
        System.out.println("getOptionalStringParameter reference");
        String parameterName = "alphaRef";
        Option expResult = new Option<String>("-5");
        Option result = sut.getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter3() {
        System.out.println("getOptionalStringParameter double reference");
        String parameterName = "alphaRefRef";
        Option expResult = new Option<String>("-5");
        Option result = sut.getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter4() {
        System.out.println("getOptionalStringParameter");
        String parameterName = "omega";
        Option expResult = Option.NONE;
        Option result = sut.getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetInstanceFromParameter1() {
        System.out.println("getInstanceFromParameter");
        String parameterName = Parameters.push("test", "objective1");
        Object expResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
        Object result = sut.getInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertFalse(result == sphereObjective);
    }

    /** Test of getInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetInstanceFromParameter2() {
        System.out.println("getInstanceFromParameter from registry");
        String parameterName = Parameters.push("test", "objective2");
        Object expResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "5")
                .build(), "obj");
        Object result = sut.getInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertEquals(sphereObjective, result);
        assertTrue(result == sphereObjective);
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter1() {
        System.out.println("getOptionalInstanceFromParameter");
        String parameterName = Parameters.push("test", "objective1");
        Option<Object> expResult = new Option(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
        Option<Object> result = sut.getOptionalInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertFalse(result.get() == sphereObjective);
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter2() {
        System.out.println("getOptionalInstanceFromParameter");
        String parameterName = Parameters.push("test", "objective3");
        Option<Object> expResult = Option.NONE;
        Option<Object> result = sut.getOptionalInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
    }

    /** Test of getInstancesFromParameter method, of class Parameters. */
    @Test
    public void testGetInstancesFromParameter() {
        System.out.println("getInstancesFromParameter");
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of getOptionalInstancesFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstancesFromParameter() {
        System.out.println("getOptionalInstancesFromParameter");
        String parameterName = "";
       
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /** Test of equals method, of class Parameters. */
    @Test
    public void testEquals() {
        System.out.println("equals and hashcode");
       
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}