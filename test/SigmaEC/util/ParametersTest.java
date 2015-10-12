package SigmaEC.util;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.real.RastriginObjective;
import SigmaEC.evaluate.objective.real.SphereObjective;
import java.util.ArrayList;
import java.util.List;
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
                .setParameter("dArrayRef", "%dArray")
                .setParameter("sArray", "Hello, world, foo, bar, baz, qux")
                .setParameter("sArrayRef", "%sArray");
    }
    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    @Test
    public void testConstructor1() {
        System.out.println("constructor");
        final Properties properties = new Properties();
        properties.put("hello!", "true");
        final Parameters sut = new Parameters(properties);
        assertTrue(sut.getBooleanParameter("hello!"));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testConstructor2() {
        System.out.println("constructor (defensive copy)");
        final Properties properties = new Properties();
        final Parameters sut = new Parameters(properties);
        properties.put("hello!", "true");
        sut.getBooleanParameter("hello!");
    }
    
    @Test
    public void testConstructor3() {
        System.out.println("constructor (builder)");
        final Properties properties = new Properties();
        properties.put("hello!", "true");
        final Parameters sut = new Parameters.Builder(properties).build();
        assertTrue(sut.getBooleanParameter("hello!"));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testConstructor4() {
        System.out.println("constructor (defensive copy in builder)");
        final Properties properties = new Properties();
        final Parameters sut = new Parameters.Builder(properties).build();
        properties.put("hello!", "true");
        sut.getBooleanParameter("hello!");
    }
    // </editor-fold>

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

    // <editor-fold defaultstate="collapsed" desc="Primitive Types">
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
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetIntParameter6() {
        System.out.println("getIntParameter");
        final String parameterName = "omega";
        sutBuilder.build().getIntParameter(parameterName);
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

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter5() {
        System.out.println("getOptionalIntParameter two-args");
        final String parameterName = "alpha";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final int expResult = -5;
        final int result = sut.getOptionalIntParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter6() {
        System.out.println("getOptionalIntParameter two-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final int expResult = 8;
        final int result = sut.getOptionalIntParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter7() {
        System.out.println("getOptionalIntParameter three-args");
        final String parameterName = "alpha";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final int expResult = -5;
        final int result = sut.getOptionalIntParameter(parameterName, defaultParameterName, 100);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter8() {
        System.out.println("getOptionalIntParameter three-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final int expResult = 8;
        final int result = sut.getOptionalIntParameter(parameterName, defaultParameterName, 100);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalIntParameter9() {
        System.out.println("getOptionalIntParameter three-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder.build();
        final int expResult = 100;
        final int result = sut.getOptionalIntParameter(parameterName, defaultParameterName, 100);
        assertEquals(expResult, result);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Long">
    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetLongParameter1() {
        System.out.println("getLongParameter");
        final String parameterName = "alpha";
        final long expResult = -5L;
        final long result = sutBuilder.build().getLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetLongParameter2() {
        System.out.println("getLongParameter reference");
        final String parameterName = "alphaRef";
        final long expResult = -5L;
        final long result = sutBuilder.build().getLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetLongParameter3() {
        System.out.println("getLongParameter expression");
        final String parameterName = "alphaExp";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "$(7 + 1)*%alpha - 3")
                .build();
        final long expResult = -43L;
        final long result = sut.getLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetLongParameter4() {
        System.out.println("getLongParameter reference chain");
        final String parameterName = "alphaRefRef";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "%alphaRef")
                .build();
        final long expResult = -5L;
        final long result = sut.getIntParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getIntParameter method, of class Parameters. */
    @Test
    public void testGetLongParameter5() {
        System.out.println("getLongParameter reference to expression");
        final String parameterName = "alphaExpRef";
        final Parameters sut = sutBuilder
                .setParameter("alphaExp", "$(7 + 1)*%alpha - 3")
                .setParameter(parameterName, "%alphaExp")
                .build();
        final long expResult = -43L;
        final long result = sut.getLongParameter(parameterName);
        assertEquals(expResult, result);
    }
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetLongParameter6() {
        System.out.println("getLongParameter");
        final String parameterName = "omega";
        sutBuilder.build().getLongParameter(parameterName);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter1() {
        System.out.println("getOptionalLongParameter");
        final String parameterName = "alpha";
        final Option<Long> expResult = new Option<>(-5L);
        final Option<Long> result = sutBuilder.build().getOptionalLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter2() {
        System.out.println("getOptionalLongParameter");
        final String parameterName = "omega";
        final Option<Long> expResult = Option.NONE;
        final Option<Long> result = sutBuilder.build().getOptionalLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter3() {
        System.out.println("getOptionalLongParameter reference");
        final String parameterName = "alphaRef";
        final Option<Long> expResult = new Option<>(-5L);
        final Option<Long> result = sutBuilder.build().getOptionalLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter4() {
        System.out.println("getOptionalLongParameter expression");
        final String parameterName = "alphaExp";
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "$(7 + 1)*%alpha - 3")
                .build();
        final Option<Long> expResult = new Option<>(-43L);
        final Option<Long> result = sut.getOptionalLongParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter5() {
        System.out.println("getOptionalLongParameter two-args");
        final String parameterName = "alpha";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final long expResult = -5L;
        final long result = sut.getOptionalLongParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter6() {
        System.out.println("getOptionalLongParameter two-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final long expResult = 8L;
        final long result = sut.getOptionalLongParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter7() {
        System.out.println("getOptionalLongParameter three-args");
        final String parameterName = "alpha";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "8")
                .build();
        final long expResult = -5L;
        final long result = sut.getOptionalLongParameter(parameterName, defaultParameterName, 100);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter8() {
        System.out.println("getOptionalLongParameter three-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final long expResult = 2L * (long)Integer.MAX_VALUE;
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, Long.toString(expResult))
                .build();
        final long result = sut.getOptionalLongParameter(parameterName, defaultParameterName, 100);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalIntParameter method, of class Parameters. */
    @Test
    public void testGetOptionalLongParameter9() {
        System.out.println("getOptionalLongParameter three-args");
        final String parameterName = "omega";
        final String defaultParameterName = "alpha2";
        final Parameters sut = sutBuilder.build();
        final long expResult = 100L;
        final long result = sut.getOptionalLongParameter(parameterName, defaultParameterName, 100);
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
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetLongParameter14() {
        System.out.println("getBooleanParameter");
        final String parameterName = "omega";
        sutBuilder.build().getBooleanParameter(parameterName);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter1() {
        System.out.println("getOptionalBooleanParameter");
        final String parameterName = "gamma";
        final Option expResult = new Option<>(true);
        final Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter2() {
        System.out.println("getOptionalBooleanParameter reference");
        final String parameterName = "gammaRef";
        final Option expResult = new Option<>(true);
        final Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter3() {
        System.out.println("getOptionalBooleanParameter");
        final String parameterName = "epsilon";
        final Option expResult = new Option<>(false);
        final Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter4() {
        System.out.println("getOptionalBooleanParameter reference");
        final String parameterName = "epsilonRef";
        final Option expResult = new Option<>(false);
        final Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter5() {
        System.out.println("getOptionalBooleanParameter");
        final String parameterName = "omega";
        final Option expResult = Option.NONE;
        final Option result = sutBuilder.build().getOptionalBooleanParameter(parameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter6() {
        System.out.println("getOptionalBooleanParameter two-args");
        final String parameterName = "gamma";
        final String defaultParameterName = "gamma2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "false")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getOptionalBooleanParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter7() {
        System.out.println("getOptionalBooleanParameter two-args");
        final String parameterName = "gamma";
        final String defaultParameterName = "gamma2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameterName, "false")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getOptionalBooleanParameter(parameterName, defaultParameterName);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter8() {
        System.out.println("getOptionalBooleanParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameterName = "gamma2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameterName, "false")
                .build();
        final boolean expResult = true;
        final boolean result = sut.getOptionalBooleanParameter(parameterName, defaultParameterName, false);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter9() {
        System.out.println("getOptionalBooleanParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameterName = "gamma2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameterName, "false")
                .build();
        final boolean expResult = false;
        final boolean result = sut.getOptionalBooleanParameter(parameterName, defaultParameterName, true);
        assertEquals(expResult, result);
    }

    /** Test of getOptionalBooleanParameter method, of class Parameters. */
    @Test
    public void testGetOptionalBooleanParameter10() {
        System.out.println("getOptionalBooleanParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameterName = "gamma2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final boolean expResult = false;
        final boolean result = sut.getOptionalBooleanParameter(parameterName, defaultParameterName, false);
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
                .setParameter(parameterName, "$0.0 - 0.08 + 1/2*%beta")
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
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetDoubleParameter9() {
        System.out.println("getDoubleParameter");
        final String parameterName = "omega";
        sutBuilder.build().getDoubleParameter(parameterName);
    }
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetDoubleParameter10() {
        System.out.println("getDoubleParameter");
        final String parameterName = "%omega";
        sutBuilder.build().getDoubleParameter(parameterName);
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
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter3() {
        System.out.println("getOptionalDoubleParameter two-args");
        final String parameterName = "beta";
        final String defaultParameter = "beta2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87")
                .build();
        final double expResult = 0.72;
        final double result = sut.getOptionalDoubleParameter(parameterName, defaultParameter);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter4() {
        System.out.println("getOptionalDoubleParameter two-args");
        final String parameterName = "omega";
        final String defaultParameter = "beta2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87")
                .build();
        final double expResult = 100.87;
        final double result = sut.getOptionalDoubleParameter(parameterName, defaultParameter);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter5() {
        System.out.println("getOptionalDoubleParameter three-args");
        final String parameterName = "beta";
        final String defaultParameter = "beta2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87")
                .build();
        final double expResult = 0.72;
        final double result = sut.getOptionalDoubleParameter(parameterName, defaultParameter, -0.0009);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter6() {
        System.out.println("getOptionalDoubleParameter three-args");
        final String parameterName = "omega";
        final String defaultParameter = "beta2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87")
                .build();
        final double expResult = 100.87;
        final double result = sut.getOptionalDoubleParameter(parameterName, defaultParameter, -0.0009);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleParameter7() {
        System.out.println("getOptionalDoubleParameter three-args");
        final String parameterName = "beta";
        final String defaultParameter = "beta2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final double expResult = -0.0009;
        final double result = sut.getOptionalDoubleParameter(parameterName, defaultParameter, -0.0009);
        assertEquals(expResult, result, 0.0000001);
        assertTrue(sut.repOK());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Double Array">
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter1() {
        System.out.println("getDoubleArrayParameter");
        final String parameterName = "dArray";
        final double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        final double[] result = sutBuilder.build().getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }

    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetDoubleArrayParameter2() {
        System.out.println("getDoubleArrayParameter reference");
        final String parameterName = "dArrayRef";
        final double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        final double[] result = sutBuilder.build().getDoubleArrayParameter(parameterName);
        assertArrayEquals(expResult, result, 0.00001);
    }
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetDoubleArrayParameter3() {
        System.out.println("getDoubleArrayParameter");
        final String parameterName = "omega";
        sutBuilder.build().getDoubleArrayParameter(parameterName);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter1() {
        System.out.println("getOptionalDoubleArrayParameter");
        final String parameterName = "dArray";
        final double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        final Option<double[]> result = sutBuilder.build().getOptionalDoubleArrayParameter(parameterName);
        assertTrue(result.isDefined());
        assertArrayEquals(expResult, result.get(), 0.00001);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter2() {
        System.out.println("getOptionalDoubleArrayParameter");
        final String parameterName = "omega";
        final Option<double[]> expResult = Option.NONE;
        final Option<double[]> result = sutBuilder.build().getOptionalDoubleArrayParameter(parameterName);
        assertEquals(expResult, result);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter3() {
        System.out.println("getOptionalDoubleArrayParameter two-args");
        final String parameterName = "dArray";
        final String defaultParameter = "dArray2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87, 18.6")
                .build();
        final double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        final double[] result = sut.getOptionalDoubleArrayParameter(parameterName, defaultParameter);
        assertArrayEquals(expResult, result, 0.00001);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter4() {
        System.out.println("getOptionalDoubleArrayParameter two-args");
        final String parameterName = "dArray";
        final String defaultParameter = "dArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameter, "100.87, 18.6")
                .build();
        final double[] expResult = new double[] { 100.87, 18.6 };
        final double[] result = sut.getOptionalDoubleArrayParameter(parameterName, defaultParameter);
        assertArrayEquals(expResult, result, 0.00001);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter5() {
        System.out.println("getOptionalDoubleArrayParameter three-args");
        final String parameterName = "dArray";
        final String defaultParameter = "dArray2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87, 18.6")
                .build();
        final double[] expResult = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };
        final double[] result = sut.getOptionalDoubleArrayParameter(parameterName, defaultParameter, new double[] { -19.0, Double.NaN });
        assertArrayEquals(expResult, result, 0.00001);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter6() {
        System.out.println("getOptionalDoubleArrayParameter three-args");
        final String parameterName = "dArray";
        final String defaultParameter = "dArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameter, "100.87, 18.6")
                .build();
        final double[] expResult = new double[] { 100.87, 18.6 };
        final double[] result = sut.getOptionalDoubleArrayParameter(parameterName, defaultParameter, new double[] { -19.0, Double.NaN });
        assertArrayEquals(expResult, result, 0.00001);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalDoubleArrayParameter7() {
        System.out.println("getOptionalDoubleArrayParameter three-args");
        final String parameterName = "dArray";
        final String defaultParameter = "dArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final double[] expResult = new double[] { -19.0, Double.NaN };
        final double[] result = sut.getOptionalDoubleArrayParameter(parameterName, defaultParameter, new double[] { -19.0, Double.NaN });
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
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetStringParameter7() {
        System.out.println("getStringParameter");
        final String parameterName = "omega";
        sutBuilder.build().getStringParameter(parameterName);
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

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter5() {
        System.out.println("getOptionalStringParameter two-args");
        final String parameterName = "gamma";
        final Parameters sut = sutBuilder
                .build();
        final String expResult = "true";
        final String result = sut.getOptionalStringParameter(parameterName, "This is a default value.");
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter6() {
        System.out.println("getOptionalStringParameter two-args");
        final String parameterName = "gamma";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final String expResult = "This is a default value.";
        final String result = sut.getOptionalStringParameter(parameterName, "This is a default value.");
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter7() {
        System.out.println("getOptionalStringParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameter = "gamma2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "Hide! Uh.")
                .build();
        final String expResult = "true";
        final String result = sut.getOptionalStringParameter(parameterName, defaultParameter, "Downtown.");
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter8() {
        System.out.println("getOptionalStringParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameter = "gamma2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameter, "Hide! Uh.")
                .build();
        final String expResult = "Hide! Uh.";
        final String result = sut.getOptionalStringParameter(parameterName, defaultParameter, "Downtown.");
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }

    /** Test of getStringParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringParameter9() {
        System.out.println("getOptionalStringParameter three-args");
        final String parameterName = "gamma";
        final String defaultParameter = "gamma2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final String expResult = "Downtown.";
        final String result = sut.getOptionalStringParameter(parameterName, defaultParameter, "Downtown.");
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="String Array">
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetStringArrayParameter1() {
        System.out.println("getStringArrayParameter");
        final String parameterName = "sArray";
        final String[] expResult = new String[] { "Hello", "world", "foo", "bar", "baz", "qux" };
        final String[] result = sutBuilder.build().getStringArrayParameter(parameterName);
        assertArrayEquals(expResult, result);
    }

    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetStringArrayParameter2() {
        System.out.println("getStringArrayParameter reference");
        final String parameterName = "sArrayRef";
        final String[] expResult = new String[] { "Hello", "world", "foo", "bar", "baz", "qux" };
        final String[] result = sutBuilder.build().getStringArrayParameter(parameterName);
        assertArrayEquals(expResult, result);
    }
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetStringArrayParameter3() {
        System.out.println("getStringArrayParameter");
        final String parameterName = "omega";
        sutBuilder.build().getStringArrayParameter(parameterName);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter1() {
        System.out.println("getStringArrayParameter");
        final String parameterName = "sArray";
        final String[] expResult = new String[] { "Hello", "world", "foo", "bar", "baz", "qux" };
        final Option<String[]> result = sutBuilder.build().getOptionalStringArrayParameter(parameterName);
        assertTrue(result.isDefined());
        assertArrayEquals(expResult, result.get());
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter2() {
        System.out.println("getStringArrayParameter");
        final String parameterName = "omega";
        final Option<String[]> expResult = Option.NONE;
        final Option<String[]> result = sutBuilder.build().getOptionalStringArrayParameter(parameterName);
        assertEquals(expResult, result);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter3() {
        System.out.println("getStringArrayParameter two-args");
        final String parameterName = "sArray";
        final String defaultParameter = "sArray2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87, 18.6, *snicker*")
                .build();
        final String[] expResult = new String[] { "Hello", "world", "foo", "bar", "baz", "qux" };
        final String[] result = sut.getOptionalStringArrayParameter(parameterName, defaultParameter);
        assertArrayEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter4() {
        System.out.println("getStringArrayParameter two-args");
        final String parameterName = "sArray";
        final String defaultParameter = "sArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameter, "100.87, 18.6, *snicker*")
                .build();
        final String[] expResult = new String[] { "100.87", "18.6", "*snicker*" };
        final String[] result = sut.getOptionalStringArrayParameter(parameterName, defaultParameter);
        assertArrayEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter5() {
        System.out.println("getStringArrayParameter three-args");
        final String parameterName = "sArray";
        final String defaultParameter = "sArray2";
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "100.87, 18.6, *snicker*")
                .build();
        final String[] expResult = new String[] { "Hello", "world", "foo", "bar", "baz", "qux" };
        final String[] result = sut.getOptionalStringArrayParameter(parameterName, defaultParameter, new String[] { "Lorem", "Ipsum" });
        assertArrayEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter6() {
        System.out.println("getStringArrayParameter three-args");
        final String parameterName = "sArray";
        final String defaultParameter = "sArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .setParameter(defaultParameter, "100.87, 18.6, *snicker*")
                .build();
        final String[] expResult = new String[] { "100.87", "18.6", "*snicker*" };
        final String[] result = sut.getOptionalStringArrayParameter(parameterName, defaultParameter, new String[] { "Lorem", "Ipsum" });
        assertArrayEquals(expResult, result);
    }
    
    /** Test of getDoubleArrayParameter method, of class Parameters. */
    @Test
    public void testGetOptionalStringArrayParameter7() {
        System.out.println("getStringArrayParameter three-args");
        final String parameterName = "sArray";
        final String defaultParameter = "sArray2";
        final Parameters sut = sutBuilder
                .clearParameter(parameterName)
                .build();
        final String[] expResult = new String[] { "Lorem", "Ipsum" };
        final String[] result = sut.getOptionalStringArrayParameter(parameterName, defaultParameter, new String[] { "Lorem", "Ipsum" });
        assertArrayEquals(expResult, result);
    }
    
    // </editor-fold>

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="List of Instances">
    
    // <editor-fold defaultstate="collapsed" desc="Required">
    /** Test of getInstancesFromParameter method, of class Parameters. */
    @Test
    public void testGetInstancesFromParameter1() {
        System.out.println("getInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetNewInstancesFromParameter1() {
        System.out.println("getNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter2() {
        System.out.println("getInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getInstancesFromParameter(parameterName, String.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter2() {
        System.out.println("getNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getNewInstancesFromParameter(parameterName, String.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter3() {
        System.out.println("getInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "slimelyClasses.ClassName,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter3() {
        System.out.println("getNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "slimelyClasses.ClassName,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter4() {
        System.out.println("getInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "")
                .build();
        sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter4() {
        System.out.println("getNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "")
                .build();
        sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter5() {
        System.out.println("getInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter5() {
        System.out.println("getNewInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter6() {
        System.out.println("getInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getInstancesFromParameter(parameterName, String.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter6() {
        System.out.println("getNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getNewInstancesFromParameter(parameterName, String.class);
    }
    
    @Test
    public void testGetInstancesFromParameter7() {
        System.out.println("getInstancesFromParameter reference");
        final String parameterName = Parameters.push("test", "objectives");
        final String referenceName = Parameters.push("test", "firstObjective");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, Parameters.REFERENCE_SYMBOL + referenceName + ",SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(referenceName, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(referenceName, "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetNewInstancesFromParameter7() {
        System.out.println("getNewInstancesFromParameter reference");
        final String parameterName = Parameters.push("test", "objectives");
        final String referenceName = Parameters.push("test", "firstObjective");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, Parameters.REFERENCE_SYMBOL + referenceName + ",SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(referenceName, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(referenceName, "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstancesFromParameter8() {
        System.out.println("getInstancesFromParameter reference ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String referenceName = Parameters.push("test", "firstObjective");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, Parameters.REFERENCE_SYMBOL + referenceName + ",SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(referenceName, "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetNewInstancesFromParameter8() {
        System.out.println("getNewInstancesFromParameter reference ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String referenceName = Parameters.push("test", "firstObjective");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, Parameters.REFERENCE_SYMBOL + referenceName + ",SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(referenceName, "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test
    public void testGetInstancesFromParameter9() {
        System.out.println("getInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .registerInstance(parameterName, expResult)
                .build();
        final List<ObjectiveFunction> result = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result);
        assertTrue(expResult == result);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetNewInstancesFromParameter9() {
        System.out.println("getNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .registerInstance(parameterName, expResult)
                .build();
        final List<ObjectiveFunction> result = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result);
        assertFalse(expResult == result);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetInstancesFromParameter10() {
        System.out.println("getInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult)
                .build();
        final List<ObjectiveFunction> result = sut.getInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result);
        assertTrue(expResult == result);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetNewInstancesFromParameter10() {
        System.out.println("getNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult)
                .build();
        final List<ObjectiveFunction> result = sut.getNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        
        assertEquals(expResult, result);
        assertFalse(expResult == result);
        assertTrue(sut.repOK());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional (one arg)">
    @Test
    public void testGetOptionalInstancesFromParameter1() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> expResult = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final Option<List<ObjectiveFunction>> result2 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1.get(), result2.get()));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter1b() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Option<List<ObjectiveFunction>> expResult = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult.get())
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final Option<List<ObjectiveFunction>> result2 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1.get(), result2.get()));
        assertTrue(expResult.get() == result1.get());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter1c() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Option<List<ObjectiveFunction>> expResult = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult.get())
                .build();
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final Option<List<ObjectiveFunction>> result2 = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1.get(), result2.get()));
        assertTrue(expResult.get() == result1.get());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter1() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> expResult = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final Option<List<ObjectiveFunction>> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1.get(), result2.get()));
        assertFalse(expResult.get() == result1.get());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter1b() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Option<List<ObjectiveFunction>> expResult = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult.get())
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        final Option<List<ObjectiveFunction>> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1.get(), result2.get()));
        assertFalse(expResult.get() == result1.get());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter1c() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Option<List<ObjectiveFunction>> instance = new Option<>((List<ObjectiveFunction>) new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }});
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, instance.get())
                .build();
        final Option<List<ObjectiveFunction>> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertNotEquals(instance, result1);
        assertEquals(Option.NONE, result1);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter2() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> expResult = Option.NONE;
        final Option<List<ObjectiveFunction>> result = sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter2() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final Option<List<ObjectiveFunction>> expResult = Option.NONE;
        final Option<List<ObjectiveFunction>> result = sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
        assertEquals(expResult, result);
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter3() {
        System.out.println("getOptionalInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "omegaclass,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter3() {
        System.out.println("getOptionalNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "omegaclass,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter4() {
        System.out.println("getOptionalInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter4() {
        System.out.println("getOptionalInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter5() {
        System.out.println("getOptionalInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "omegaclass,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, EvaluationOperator.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter5() {
        System.out.println("getOptionalNewInstancesFromParameter ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "omegaclass,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, EvaluationOperator.class);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional (one arg with default)">
    @Test
    public void testGetOptionalInstancesFromParameter6() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertNotEquals(defaultValue, result1);
        assertFalse(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter6b() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult)
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertNotEquals(defaultValue, result1);
        assertFalse(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(result1 == result2);
        assertTrue(expResult == result1);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter6c() {
        System.out.println("getOptionalInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult)
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertNotEquals(defaultValue, result1);
        assertFalse(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(result1 == result2);
        assertTrue(expResult == result1);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter6() {
        System.out.println("getOptionalNewInstancesFromParameter with default");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertNotEquals(defaultValue, result1);
        assertFalse(defaultValue == result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter6b() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, expResult)
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertNotEquals(defaultValue, result1);
        assertFalse(defaultValue == result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertFalse(result1 == result2);
        assertFalse(expResult == result1);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter6c() {
        System.out.println("getOptionalNewInstancesFromParameter");
        final String parameterName = Parameters.push("test", "objectives");
        final List<ObjectiveFunction> instance = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final Parameters sut = sutBuilder
                .registerInstance(parameterName, instance)
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(defaultValue, result1);
        assertNotEquals(instance, result1);
        assertTrue(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(result1 == result2);
        assertFalse(instance == result1);
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter7() {
        System.out.println("getOptionalInstancesFromParameter with default");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter7() {
        System.out.println("getOptionalNewInstancesFromParameter with default");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter8() {
        System.out.println("getOptionalInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder.setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        sut.getOptionalInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter8() {
        System.out.println("getOptionalNewInstancesFromParameter missing sub-parameter");
        final String parameterName = Parameters.push("test", "objectives");
        final Parameters sut = sutBuilder.setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        sut.getOptionalNewInstancesFromParameter(parameterName, defaultValue, ObjectiveFunction.class);
    }
    
     // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional (two args)">
    @Test
    public void testGetOptionalInstancesFromParameter9() {
        System.out.println("getOptionalInstancesFromParameter two-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter9() {
        System.out.println("getOptionalNewInstancesFromParameter two-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter10() {
        System.out.println("getOptionalInstancesFromParameter two-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "16")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "17")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter10() {
        System.out.println("getOptionalNewInstancesFromParameter two-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "16")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "17")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter11() {
        System.out.println("getOptionalInstancesFromParameter two-args missing sub-argument");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter11() {
        System.out.println("getOptionalNewInstancesFromParameter two-args missing sub-argument");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter12() {
        System.out.println("getOptionalInstancesFromParameter two-args ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter12() {
        System.out.println("getOptionalNewInstancesFromParameter two-args ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalInstancesFromParameter13() {
        System.out.println("getOptionalInstancesFromParameter two-args ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, SphereObjective.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetOptionalNewInstancesFromParameter13() {
        System.out.println("getOptionalNewInstancesFromParameter two-args ISE");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, SphereObjective.class);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional (three args)">
    @Test
    public void testGetOptionalInstancesFromParameter14() {
        System.out.println("getOptionalInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter14() {
        System.out.println("getOptionalNewInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective,SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(Parameters.push(parameterName, "0"), "numDimensions"), "10")
                .setParameter(Parameters.push(Parameters.push(parameterName, "1"), "numDimensions"), "11")
                .setParameter(Parameters.push(Parameters.push(parameterName, "2"), "numDimensions"), "12")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "11")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "12")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter15() {
        System.out.println("getOptionalInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "16")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "17")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter15() {
        System.out.println("getOptionalNewInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective,SigmaEC.evaluate.objective.real.RastriginObjective")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "0"), "numDimensions"), "15")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "1"), "numDimensions"), "16")
                .setParameter(Parameters.push(Parameters.push(defaultParameter, "2"), "numDimensions"), "17")
                .build();
        final List<ObjectiveFunction> expResult = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "16")
                .build(), "obj"));
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "17")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(expResult, result1);
        assertFalse(expResult == result1);
        assertEquals(result1, result2);
        assertFalse(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalInstancesFromParameter16() {
        System.out.println("getOptionalInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder.build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testGetOptionalNewInstancesFromParameter16() {
        System.out.println("getOptionalNewInstancesFromParameter three-args");
        final String parameterName = Parameters.push("test", "objectives");
        final String defaultParameter = Parameters.push("test", "objectives2");
        final Parameters sut = sutBuilder.build();
        final List<ObjectiveFunction> defaultValue = new ArrayList<ObjectiveFunction>() {{
            add(new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "21")
                .build(), "obj"));
            add(new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "22")
                .build(), "obj"));
        }};
        final List<ObjectiveFunction> result1 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        final List<ObjectiveFunction> result2 = sut.getOptionalNewInstancesFromParameter(parameterName, defaultParameter, defaultValue, ObjectiveFunction.class);
        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertEquals(result1, result2);
        assertTrue(Misc.shallowEquals(result1, result2));
        assertTrue(sut.repOK());
    }
    
    // </editor-fold>
    
    // </editor-fold>
    

    /** Test of equals method, of class Parameters. */
    @Test
    public void testEquals() {
        System.out.println("equals and hashcode");
        
        
        final Parameters sut1 = sutBuilder.build();
        final Parameters sut2 = sutBuilder.build();
        final Parameters sut3 = sutBuilder
                .registerInstance("obj", sphereObjective).build();
        final Parameters sut4 = sutBuilder
                .registerInstance("obj", new SphereObjective(new Parameters.Builder(new Properties())
                    .setParameter("obj.numDimensions", "5").build(), "obj")).build();
        final Parameters sut5 = sutBuilder
                .registerInstance("obj",new SphereObjective(new Parameters.Builder(new Properties())
                    .setParameter("obj.numDimensions", "6").build(), "obj")).build();
        final Parameters sut6 = sutBuilder
                .clearParameter("gamma").build();
        final Parameters sut7 = sutBuilder
                .clearParameter("gamma").build();
        final Parameters sut8 = sutBuilder
                .setParameter("gamma", "false").build();
        
        assertEquals(sut1, sut1);
        assertEquals(sut2, sut2);
        assertEquals(sut3, sut3);
        assertEquals(sut4, sut4);
        assertEquals(sut5, sut5);
        assertEquals(sut6, sut6);
        assertEquals(sut7, sut7);
        assertEquals(sut8, sut8);
        
        assertEquals(sut1, sut2);
        assertEquals(sut2, sut1);
        assertTrue(sut1.hashCode() == sut2.hashCode());
        assertEquals(sut3, sut4);
        assertEquals(sut4, sut3);
        assertTrue(sut3.hashCode() == sut4.hashCode());
        assertEquals(sut6, sut7);
        assertEquals(sut7, sut6);
        assertTrue(sut6.hashCode() == sut7.hashCode());
        
        assertNotEquals(sut1, "Hello");
        assertNotEquals(sut1, sut3);
        assertNotEquals(sut1, sut4);
        assertNotEquals(sut1, sut5);
        assertNotEquals(sut1, sut6);
        assertNotEquals(sut1, sut7);
        assertNotEquals(sut1, sut8);
        assertNotEquals(sut2, sut3);
        assertNotEquals(sut2, sut4);
        assertNotEquals(sut2, sut5);
        assertNotEquals(sut2, sut6);
        assertNotEquals(sut2, sut7);
        assertNotEquals(sut2, sut8);
        assertNotEquals(sut3, sut1);
        assertNotEquals(sut3, sut2);
        assertNotEquals(sut3, sut5);
        assertNotEquals(sut3, sut6);
        assertNotEquals(sut3, sut7);
        assertNotEquals(sut3, sut8);
        assertNotEquals(sut4, sut1);
        assertNotEquals(sut4, sut2);
        assertNotEquals(sut4, sut5);
        assertNotEquals(sut4, sut6);
        assertNotEquals(sut4, sut7);
        assertNotEquals(sut4, sut8);
        assertNotEquals(sut5, sut1);
        assertNotEquals(sut5, sut2);
        assertNotEquals(sut5, sut3);
        assertNotEquals(sut5, sut4);
        assertNotEquals(sut5, sut1);
        assertNotEquals(sut5, sut2);
        assertNotEquals(sut5, sut6);
        assertNotEquals(sut5, sut7);
        assertNotEquals(sut5, sut8);
        assertNotEquals(sut6, sut1);
        assertNotEquals(sut6, sut2);
        assertNotEquals(sut6, sut3);
        assertNotEquals(sut6, sut4);
        assertNotEquals(sut6, sut5);
        assertNotEquals(sut6, sut8);
        assertNotEquals(sut7, sut1);
        assertNotEquals(sut7, sut2);
        assertNotEquals(sut7, sut3);
        assertNotEquals(sut7, sut4);
        assertNotEquals(sut7, sut5);
        assertNotEquals(sut7, sut8);
        assertNotEquals(sut8, sut1);
        assertNotEquals(sut8, sut2);
        assertNotEquals(sut8, sut3);
        assertNotEquals(sut8, sut4);
        assertNotEquals(sut8, sut5);
        assertNotEquals(sut8, sut6);
        assertNotEquals(sut8, sut7);
        
        assertTrue(sut1.repOK());
        assertTrue(sut2.repOK());
        assertTrue(sut3.repOK());
        assertTrue(sut4.repOK());
        assertTrue(sut5.repOK());
        assertTrue(sut6.repOK());
        assertTrue(sut7.repOK());
        assertTrue(sut8.repOK());
    }
}