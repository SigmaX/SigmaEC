package SigmaEC.util;

import SigmaEC.evaluate.objective.real.SphereObjective;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class ParametersTest {
    private Parameters.Builder sutBuilder;
    private SphereObjective sphereObjective;
    
    public ParametersTest() {
    }
    
    @Before
    public void setUp() {
        sphereObjective = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "5")
                .build(), "obj");
        
        sutBuilder = new Parameters.Builder(new Properties())
                .setParameter("alpha", "-5")
                .setParameter("alphaRef", "%alpha")
                .setParameter("beta", "0.72")
                .setParameter("betaRef", "%beta")
                .setParameter("gamma", "true")
                .setParameter("gammaRef", "%gamma")
                .setParameter("epsilon", "false")
                .setParameter("epsilonRef", "%epsilon")
                .setParameter("dArray", "0.1, 0.2,0.3, 0.4, 0.5")
                .setParameter("dArrayRef", "%dArray");
    }

    // <editor-fold defaultstate="collapsed" desc="Helpers">
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
        boolean result = sutBuilder.build().isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined2() {
        System.out.println("isDefined");
        String parameterName = "alpha";
        boolean expResult = true;
        boolean result = sutBuilder.build().isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined3() {
        System.out.println("isDefined reference");
        String parameterName = "alphaRef";
        boolean expResult = true;
        boolean result = sutBuilder.build().isDefined(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of isDefined method, of class Parameters. */
    @Test
    public void testIsDefined4() {
        System.out.println("isDefined double reference");
        String parameterName = "alphaRefRef";
        boolean expResult = true;
        boolean result = sutBuilder
                .setParameter("alphaRefRef", "%alphaRef")
                .build().isDefined(parameterName);
        assertEquals(expResult, result);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Basic Types">
    // <editor-fold defaultstate="collapsed" desc="Int">
    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter1() {
        System.out.println("getIntParameter");
        String parameterName = "alpha";
        int expResult = -5;
        int result = sutBuilder.build().getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter2() {
        System.out.println("getIntParameter reference");
        final String parameterName = "alphaRef";
        final int expResult = -5;
        final int result = sutBuilder.build().getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter3() {
        System.out.println("getIntParameter expression");
        final String parameterName = "alphaExp";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "$(7 + 1)*%alpha - 3")
                .build();
        final int expResult = -43;
        final int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter4() {
        System.out.println("getIntParameter reference chain");
        final String parameterName = "alphaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%alphaRef")
                .build();
        final int expResult = -5;
        final int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetIntParameter5() {
        System.out.println("getIntParameter reference to expression");
        final String parameterName = "alphaExpRef";
        final Parameters sut = sutBuilder
                .setParameter("alphaExp", "$(7 + 1)*%alpha - 3")
                .setParameter(parameterName, "%alphaExp")
                .build();
        final int expResult = -43;
        final int result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter1() {
        System.out.println("getOptionalIntParameter");
        final String parameterName = "alpha";
        final Option expResult = new Option<>(-5);
        final Option result = sutBuilder.build().getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter2() {
        System.out.println("getOptionalIntParameter");
        final String parameterName = "omega";
        final Option expResult = Option.NONE;
        final Option result = sutBuilder.build().getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter3() {
        System.out.println("getOptionalIntParameter reference");
        final String parameterName = "alphaRef";
        final Option expResult = new Option<>(-5);
        final Option result = sutBuilder.build().getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter4() {
        System.out.println("getOptionalIntParameter expression");
        final String parameterName = "alphaExp";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "$(7 + 1)*%alpha - 3")
                .build();
        final Option expResult = new Option<>(-43);
        final Option result = sut.getOptionalIntParameter(parameterName);
        assertEquals(expResult, result);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Boolean">

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter1() {
        System.out.println("getBooleanParameter");
        final String parameterName = "gamma";
        final Parameters sut = sutBuilder.build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter2() {
        System.out.println("getBooleanParameter reference");
        final String parameterName = "gammaRef";
        final Parameters sut = sutBuilder.build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter3() {
        System.out.println("getBooleanParameter");
        final String parameterName = "epsilon";
        final Parameters sut = sutBuilder.build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter4() {
        System.out.println("getBooleanParameter reference");
        final String parameterName = "epsilonRef";
        final Parameters sut = sutBuilder.build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter5() {
        System.out.println("getBooleanParameter");
        final String parameterName = "gamma1";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "True")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter6() {
        System.out.println("getBooleanParameter");
        final String parameterName = "gamma2";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "TRUE")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter7() {
        System.out.println("getBooleanParameter");
        final String parameterName = "gamma3";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "TrUe")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter8() {
        System.out.println("getBooleanParameter");
        final String parameterName = "epsilon1";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "False")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter9() {
        System.out.println("getBooleanParameter");
        final String parameterName = "epsilon2";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "FALSE")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter10() {
        System.out.println("getBooleanParameter");
        final String parameterName = "epsilon3";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "FaLsE")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetBooleanParameter11() {
        System.out.println("getBooleanParameter");
        final String parameterName = "gammaBad";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "Hello")
                .build();
        sut.getBooleanParameter(parameterName);
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter12() {
        System.out.println("getBooleanParameter reference chaine");
        final String parameterName = "gammaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%gammaRef")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getBooleanParameter method, of class Parameters. */
    @Test
    public void testGetBooleanParameter13() {
        System.out.println("getBooleanParameter reference chaine");
        final String parameterName = "epsilonRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%epsilonRef")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getBooleanParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter1() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "gamma";
        Option expResult = new Option<>(true);
        Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter2() {
        System.out.println("getOptionalBooleanParameter reference");
        String parameterName = "gammaRef";
        Option expResult = new Option<>(true);
        Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter3() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "epsilon";
        Option expResult = new Option<>(false);
        Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter4() {
        System.out.println("getOptionalBooleanParameter reference");
        String parameterName = "epsilonRef";
        Option expResult = new Option<>(false);
        Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter5() {
        System.out.println("getOptionalBooleanParameter");
        String parameterName = "omega";
        Option expResult = Option.NONE;
        Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Double">
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter1() {
        System.out.println("getDoubleParameter");
        final String parameterName = "beta";
        final double expResult = 0.72;
        final double result = sutBuilder.build().getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter2() {
        System.out.println("getDoubleParameter reference");
        final String parameterName = "betaRef";
        final double expResult = 0.72;
        final double result = sutBuilder.build().getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter3() {
        System.out.println("getDoubleParameter double reference");
        final String parameterName = "betaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%betaRef")
                .build();
        final double expResult = 0.72;
        final double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter4() {
        System.out.println("getDoubleParameter expression");
        final String parameterName = "betaExp";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "$%beta/2 - 0.08")
                .build();
        final double expResult = 0.28;
        final double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter5() {
        System.out.println("getDoubleParameter reference to expression");
        final String parameterName = "betaExpRef";
        final Parameters sut = sutBuilder
                .setParameter("betaExp", "$%beta/2 - 0.08")
                .setParameter(parameterName, "%betaExp")
                .build();
        final double expResult = 0.28;
        final double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter6() {
        System.out.println("getDoubleParameter");
        final String parameterName = "alpha";
        final double expResult = -5.0;
        final double result = sutBuilder.build().getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter7() {
        System.out.println("getDoubleParameter reference");
        final String parameterName = "alphaRef";
        final double expResult = -5.0;
        final double result = sutBuilder.build().getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetDoubleParameter8() {
        System.out.println("getDoubleParameter expression with reference to expression");
        final String parameterName = "expRef";
        final Parameters sut = sutBuilder
                .setParameter("betaExp", "$%beta/2 - 0.08")
                .setParameter(parameterName, "$%betaExp*10")
                .build();
        final double expResult = 2.8;
        final double result = sut.getDoubleParameter(parameterName);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter1() {
        System.out.println("getOptionalDoubleParameter reference");
        final String parameterName = "betaRef";
        final Option expResult = new Option<>(0.72);
        final Option result = sutBuilder.build().getOptionalDoubleParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter2() {
        System.out.println("getOptionalDoubleParameter");
        final String parameterName = "omega";
        final Option expResult = Option.NONE;
        final Option result = sutBuilder.build().getOptionalDoubleParameter(parameterName);
        assertEquals(expResult, result);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Double Array">
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter1() {
        System.out.println("getDoubleArrayParameter");
        String parameterName = "dArray";
        double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] result = sutBuilder.build().getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }

    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter2() {
        System.out.println("getDoubleArrayParameter reference");
        String parameterName = "dArrayRef";
        double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] result = sutBuilder.build().getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String">
    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter1() {
        System.out.println("getStringParameter");
        final String parameterName = "gamma";
        final String expResult = "true";
        final String result = sutBuilder.build().getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter2() {
        System.out.println("getStringParameter");
        final String parameterName = "epsilon";
        final String expResult = "false";
        final String result = sutBuilder.build().getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter3() {
        System.out.println("getStringParameter reference");
        final String parameterName = "gammaRef";
        final String expResult = "true";
        final String result = sutBuilder.build().getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter4() {
        System.out.println("getStringParameter reference");
        final String parameterName = "epsilonRef";
        final String expResult = "false";
        final String result = sutBuilder.build().getStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter5() {
        System.out.println("getStringParameter double reference");
        final String parameterName = "gammaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%gammaRef")
                .build();
        final String expResult = "true";
        final String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetStringParameter6() {
        System.out.println("getStringParameter double reference");
        final String parameterName = "epsilonRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%epsilonRef")
                .build();
        final String expResult = "false";
        final String result = sut.getStringParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter1() {
        System.out.println("getOptionalStringParameter");
        final String parameterName = "alpha";
        final Option expResult = new Option<>("-5");
        final Option result = sutBuilder.build().getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter2() {
        System.out.println("getOptionalStringParameter reference");
        final String parameterName = "alphaRef";
        final Option expResult = new Option<>("-5");
        final Option result = sutBuilder.build().getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter3() {
        System.out.println("getOptionalStringParameter double reference");
        final String parameterName = "alphaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%alphaRef")
                .build();
        final Option expResult = new Option<>("-5");
        final Option result = sut.getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter4() {
        System.out.println("getOptionalStringParameter");
        final String parameterName = "omega";
        final Option expResult = Option.NONE;
        final Option result = sutBuilder.build().getOptionalStringParameter(parameterName);
        assertEquals(expResult, result);
    }
    // </editor-fold>

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Instances">
    /** Test of getInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetInstanceFromParameter1() {
        System.out.println("getInstanceFromParameter");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter("test.objective1.numDimensions", "10")
                .build();
        final Object expResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
        final Object result = sut.getInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertFalse(result == sphereObjective);
        assertTrue(sut.repOK());
    }

    /** Test of getInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetInstanceFromParameter2() {
        System.out.println("getInstanceFromParameter from registry");
        final String parameterName = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder
                .registerInstance(Parameters.push("test", "objective2"), sphereObjective)
                .build();
        final Object expResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "5")
                .build(), "obj");
        final Object result = sut.getInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertEquals(sphereObjective, result);
        assertTrue(result == sphereObjective);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter1() {
        System.out.println("getOptionalInstanceFromParameter");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter("test.objective1.numDimensions", "10")
                .build();
        final Option<Object> expResult = new Option(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
        final Option<Object> result = sut.getOptionalInstanceFromParameter(parameterName, SphereObjective.class);
        assertEquals(expResult, result);
        assertFalse(result.get() == sphereObjective);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter2() {
        System.out.println("getOptionalInstanceFromParameter");
        final String parameterName = Parameters.push("test", "objective3");
        final Option<Object> expResult = Option.NONE;
        final Option<Object> result = sutBuilder.build().getOptionalInstanceFromParameter(parameterName, SphereObjective.class);
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
    // </editor-fold>

    /** Test of equals method, of class Parameters. */
    @Test
    public void testEquals() {
        System.out.println("equals and hashcode");
       
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}