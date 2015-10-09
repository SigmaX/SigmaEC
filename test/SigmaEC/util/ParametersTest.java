package SigmaEC.util;

import SigmaEC.evaluate.objective.ObjectiveFunction;
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
                .setParameter("dArrayRef", "%dArray")
                .setParameter("sArray", "Hello, world, foo, bar, baz, qux")
                .setParameter("sArrayRef", "%sArray");
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
    
    /** Test of getIntParameter method, of class Parameters. */
    @Test (expected = IllegalStateException.class)
    public void testGetDoubleParameter9() {
        System.out.println("getDoubleParameter");
        final String parameterName = "omega";
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
    
    // <editor-fold defaultstate="collapsed" desc="Instances">
    
    // <editor-fold defaultstate="collapsed" desc="Single Instances">
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
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstanceFromParameter3() {
        System.out.println("getInstanceFromParameter bad class name");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "omega.badpackage.awefipahewfiawe")
                .setParameter("test.objective1.numDimensions", "10")
                .build();
        sut.getInstanceFromParameter(parameterName, SphereObjective.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstanceFromParameter4() {
        System.out.println("getInstanceFromParameter missing required sub-parameter");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.real.SphereObjective")
                .build();
        sut.getInstanceFromParameter(parameterName, SphereObjective.class);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testGetInstanceFromParameter5() {
        System.out.println("getInstanceFromParameter incorrect expected class");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter("test.objective1.numDimensions", "10")
                .build();
        sut.getInstanceFromParameter(parameterName, String.class);
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

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter3() {
        System.out.println("getOptionalInstanceFromParameter with default value");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder
                .setParameter("test.objective1", "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter("test.objective1.numDimensions", "10")
                .build();
        final SphereObjective expResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultValue);
        
        assertEquals(expResult, result);
        assertNotEquals(sphereObjective, result);
        assertNotEquals(defaultValue, result);
        assertFalse(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter4() {
        System.out.println("getOptionalInstanceFromParameter with default value");
        final String parameterName = Parameters.push("test", "objective1");
        final Parameters sut = sutBuilder.build();
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultValue);
        
        assertNotEquals(sphereObjective, result);
        assertEquals(defaultValue, result);
        assertTrue(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter5() {
        System.out.println("getOptionalInstanceFromParameter two-args");
        final String parameterName = Parameters.push("test", "objective1");
        final String defaultParameter = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(parameterName, "numDimensions"), "10")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(defaultParameter, "numDimensions"), "15").build();
        final SphereObjective expectedResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        
        assertEquals(expectedResult, result);
        assertFalse(expectedResult == result);
        assertNotEquals(defaultValue, result);
        assertFalse(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter6() {
        System.out.println("getOptionalInstanceFromParameter two-args");
        final String parameterName = Parameters.push("test", "objective1");
        final String defaultParameter = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(defaultParameter, "numDimensions"), "15").build();
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultParameter, ObjectiveFunction.class);
        
        assertEquals(defaultValue, result);
        assertFalse(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter7() {
        System.out.println("getOptionalInstanceFromParameter three-args");
        final String parameterName = Parameters.push("test", "objective1");
        final String defaultParameter = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder
                .setParameter(parameterName, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(parameterName, "numDimensions"), "10")
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(defaultParameter, "numDimensions"), "15").build();
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj");
        final SphereObjective expectedResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultParameter, defaultValue);
        
        assertEquals(expectedResult, result);
        assertFalse(expectedResult == result);
        assertNotEquals(defaultValue, result);
        assertFalse(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter8() {
        System.out.println("getOptionalInstanceFromParameter three-args");
        final String parameterName = Parameters.push("test", "objective1");
        final String defaultParameter = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder
                .setParameter(defaultParameter, "SigmaEC.evaluate.objective.real.SphereObjective")
                .setParameter(Parameters.push(defaultParameter, "numDimensions"), "15").build();
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj");
        final SphereObjective expectedResult = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "15")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultParameter, defaultValue);
        
        assertEquals(expectedResult, result);
        assertFalse(expectedResult == result);
        assertNotEquals(defaultValue, result);
        assertFalse(defaultValue == result);
        assertTrue(sut.repOK());
    }

    /** Test of getOptionalInstanceFromParameter method, of class Parameters. */
    @Test
    public void testGetOptionalInstanceFromParameter9() {
        System.out.println("getOptionalInstanceFromParameter three-args");
        final String parameterName = Parameters.push("test", "objective1");
        final String defaultParameter = Parameters.push("test", "objective2");
        final Parameters sut = sutBuilder.build();
        final SphereObjective defaultValue = new SphereObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "20")
                .build(), "obj");
        
        final ObjectiveFunction result = sut.getOptionalInstanceFromParameter(parameterName, defaultParameter, defaultValue);
        
        assertEquals(defaultValue, result);
        assertTrue(defaultValue == result);
        assertTrue(sut.repOK());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="List of Instances">
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
    
    // </editor-fold>

    /** Test of equals method, of class Parameters. */
    @Test
    public void testEquals() {
        System.out.println("equals and hashcode");
       
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}