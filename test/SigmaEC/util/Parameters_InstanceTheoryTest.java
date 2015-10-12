package SigmaEC.util;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.real.RastriginObjective;
import SigmaEC.evaluate.objective.real.SphereObjective;
import java.util.Properties;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * The theories in this class define the requirements that must be
 * satisfied by the members of Parameters that deal with creating and retrieving
 * instances of objects.
 * 
 * @author Eric O. Scott
 */
@RunWith(Theories.class)
public class Parameters_InstanceTheoryTest {
    
    // <editor-fold defaultstate="collapsed" desc="Domain Model">
    private final static String PARAMETER_NAME_A = Parameters.push("test", "objective");
    private final static String SUB_PARAMETER_NAME_A = Parameters.push(PARAMETER_NAME_A, "numDimensions");
    private final static String PARAMETER_NAME_B = Parameters.push("default", "objective");
    private final static String SUB_PARAMETER_NAME_B = Parameters.push(PARAMETER_NAME_B, "numDimensions");
    
    private final static SphereObjective INSTANCE_A = new SphereObjective(new Parameters.Builder(new Properties())
            .setParameter("obj.numDimensions", "10")
            .build(), "obj");
    private final static SphereObjective INSTANCE_B = new SphereObjective(new Parameters.Builder(new Properties())
            .setParameter("obj.numDimensions", "15")
            .build(), "obj");
    private final static ObjectiveFunction DEFAULT_VALUE = new RastriginObjective(new Parameters.Builder(new Properties())
                .setParameter("obj.numDimensions", "10")
                .build(), "obj");
    
    public static class AInstance {
        final Object instance;
        AInstance(final Object instance) { this.instance = instance; }
    }
    
    public static class BInstance {
        final Object instance;
        BInstance(final Object instance) { this.instance = instance; }
    }
    
    @DataPoint
    public static Parameters.Builder PARAMETERS_A_1 = new Parameters.Builder(new Properties());
    
    @DataPoint
    public static Parameters.Builder PARAMETERS_A_2 = new Parameters.Builder(new Properties())
            .setParameter(PARAMETER_NAME_A, "SigmaEC.evaluate.objective.real.SphereObjective")
            .setParameter(Parameters.push(PARAMETER_NAME_A, "numDimensions"), "10");
    @DataPoint
    public static Parameters.Builder PARAMETERS_A_3 = new Parameters.Builder(new Properties())
            .setParameter(PARAMETER_NAME_A, "SigmaEC.evaluate.objective.real.SphereObjective");
    
    @DataPoint
    public static AInstance INSTANCE_A_1 = null;
    @DataPoint
    public static AInstance INSTANCE_A_2 = new AInstance(INSTANCE_A);
    
    @DataPoint
    public static Parameters PARAMETERS_B_1 = new Parameters.Builder(new Properties()).build();
    
    @DataPoint
    public static Parameters PARAMETERS_B_2 = new Parameters.Builder(new Properties())
            .setParameter(PARAMETER_NAME_B, "SigmaEC.evaluate.objective.real.SphereObjective")
            .setParameter(Parameters.push(PARAMETER_NAME_B, "numDimensions"), "15").build();
    
    @DataPoint
    public static Parameters PARAMETERS_B_3 = new Parameters.Builder(new Properties())
            .setParameter(PARAMETER_NAME_B, "SigmaEC.evaluate.objective.real.SphereObjective").build();
    
    @DataPoint
    public static BInstance INSTANCE_B_1 = null;
    @DataPoint
    public static BInstance INSTANCE_B_2 = new BInstance(INSTANCE_B);
    
    @DataPoint
    public static ObjectiveFunction DEFAULT_1 = DEFAULT_VALUE;
    // </editor-fold>
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public Parameters_InstanceTheoryTest() { }
    
    private static Parameters assembleParameters(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance) {
        assert(aBuilder != null);
        assert(b != null);
        final Parameters.Builder builder = new Parameters.Builder(new Properties());
        if (aInstance != null)
            builder.registerInstance(PARAMETER_NAME_A, aInstance.instance);
        if (bInstance != null)
            builder.registerInstance(PARAMETER_NAME_B, bInstance.instance);
        return builder
                .addAll(aBuilder.build())
                .addAll(b)
                .build();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Single Instances">
    
    // <editor-fold defaultstate="collapsed" desc="Required Parameter">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created/returned,
     * and the same object should be returned if we call the method twice.
     */
    @Theory
    public void testGetInstanceFromParameter1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1);
        assertTrue(result1 == result2);
    }
    
    /** If an instance has already been registered for this parameter, then
     * that instance should be returned (rather than creating a new one). It
     * does not matter if parameters describing the object have been defined.
     */
    @Theory
    public void testGetInstanceFromParameter2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isRegistered(PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1);
        assertTrue(INSTANCE_A == result1);
        assertTrue(result1 == result2);
    }
    
    /** If the parameter is not defined and no instance is registered, an exception is thrown. */
    @Theory
    public void testGetInstanceFromParameter3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, and no instance is registered for that parameter,
     * then an exception is thrown. */
    @Theory
    public void testGetInstanceFromParameter4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetInstanceFromParameter5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getInstanceFromParameter(PARAMETER_NAME_A, String.class);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Required Parameter (New instance)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created and returned,
     * and a *different* object should be returned if we call the method twice.
     * The object will also not be the same object as any instance that may be
     * registered.
     */
    @Theory
    public void testGetNewInstanceFromParameter1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1);
        assertFalse(INSTANCE_A == result1);
        assertFalse(INSTANCE_A == result2);
        assertFalse(result1 == result2);
    }
    
    /** If the parameter is not defined, an exception is thrown. */
    @Theory
    public void testGetNewInstanceFromParameter2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, then an exception is thrown. */
    @Theory
    public void testGetNewInstanceFromParameter3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetNewInstanceFromParameter4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getNewInstanceFromParameter(PARAMETER_NAME_A, String.class);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (one arg)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created/returned,
     * and the same object should be returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalInstanceFromParameter1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final Option<ObjectiveFunction> result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final Option<ObjectiveFunction> result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1.get());
        assertTrue(result1.get() == result2.get());
    }
    
    /** If an instance has already been registered for this parameter, then
     * that instance should be returned (rather than creating a new one). It
     * does not matter if parameters describing the object have been defined.
     */
    @Theory
    public void testGetOptionalInstanceFromParameter2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isRegistered(PARAMETER_NAME_A));
        
        final Option<ObjectiveFunction> result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final Option<ObjectiveFunction> result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1.get());
        assertTrue(INSTANCE_A == result1.get());
        assertTrue(result1.get() == result2.get());
    }
    
    /** If the parameter is not defined and no instance is registered, Option.NONE is returned. */
    @Theory
    public void testGetOptionalInstanceFromParameter3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        
        final Option<ObjectiveFunction> result = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(Option.NONE, result);
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, and no instance is registered for that parameter,
     * then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameter4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameter5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, String.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (one arg, new instance)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object is created/returned,
     * and a *different* is returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameter1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final Option<ObjectiveFunction> result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        final Option<ObjectiveFunction> result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1.get());
        assertFalse(INSTANCE_A == result1.get());
        assertFalse(INSTANCE_A == result2.get());
        assertFalse(result1.get() == result2.get());
    }
    
    /** If the parameter is not defined, Option.NONE is returned. */
    @Theory
    public void testGetOptionalNewInstanceFromParameter2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        
        final Option<ObjectiveFunction> result = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
        
        assertEquals(Option.NONE, result);
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameter3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, SphereObjective.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameter4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, String.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (wih default)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created/returned,
     * and the same object should be returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterWithDefault1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);

        assertEquals(INSTANCE_A, result1);
        assertTrue(result1 == result2);
    }
    
    /** If an instance has already been registered for this parameter, then
     * that instance should be returned (rather than creating a new one). It
     * does not matter if parameters describing the object have been defined.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterWithDefault2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        
        assertEquals(INSTANCE_A, result1);
        assertTrue(INSTANCE_A == result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If the parameter is not defined and no instance is registered, the default value is returned. */
    @Theory
    public void testGetOptionalInstanceFromParameterWithDefault3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        
        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertTrue(defaultValue == result2);
        assertTrue(sut.repOK());
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, and no instance is registered for that parameter,
     * then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterWithDefault4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterWithDefault5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, defaultValue, RastriginObjective.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (wih default, new instance)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object is created,
     * and a *different* instance is returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterWithDefault1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);

        assertEquals(INSTANCE_A, result1);
        assertFalse(INSTANCE_A == result1);
        assertFalse(result1 == result2);
    }
    
    /** If the parameter is not defined, the default value is returned. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterWithDefault2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);

        assertEquals(defaultValue, result1);
        assertTrue(defaultValue == result1);
        assertTrue(defaultValue == result2);
        assertTrue(sut.repOK());
    }
    
    /** If the sub-parameters for a parameter describing an object
     * are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterWithDefault3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, ObjectiveFunction.class);
    }
    
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterWithDefault4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (with default)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, defaultValue, RastriginObjective.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (two args)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created/returned,
     * and the same object should be returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);

        assertEquals(INSTANCE_A, result1);
        assertTrue(result1 == result2);
    }
    
    /** If an instance has already been registered for this parameter, then
     * that instance should be returned (rather than creating a new one). It
     * does not matter if parameters describing the object have been defined.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isRegistered(PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        
        assertEquals(INSTANCE_A, result1);
        assertTrue(INSTANCE_A == result1);
        assertTrue(result1 == result2);
    }
    
    /** If parameter A is not defined and no instance is registered, but the parameter B
     * is defined and its sub-parameters are correctly defined, then an object
     * corresponding to parameter B is created/returned.  The same object
     * should be returned if the method is called more than once. */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        
        assertEquals(INSTANCE_B, result1);
        assertTrue(result1 == result2);
    }
    
    /** If the sub-parameters for parameter A
     * are not defined, and no instance is registered for that parameter,
     * then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If a parameter or instance for A is not defined, and parameter B is
     * defined and no instance is registered for it, and the sub-parameters
     * for parameter B are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If no parameter or instance is defined for either A or B, then an
     *  exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs6(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If parameter A exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs7(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, String.class);
    }
    
    /** If parameter A is not defined nor an instance registered for it,
     * and parameter B exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterTwoArgs8(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, String.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (two args, new instance)">
    /** If a parameter for an object exists and has its sub-parameters correctly
     * defined, then an appropriate object is created,
     * and a *different* object is returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);

        assertEquals(INSTANCE_A, result1);
        assertFalse(INSTANCE_A == result1);
        assertEquals(result1, result2);
        assertFalse(result1 == result2);
    }
    
    /** If parameter A is not defined and no instance is registered, but parameter B
     * is defined and its sub-parameters are correctly defined, then an object
     * corresponding to parameter B is created.  A different object
     * should be returned if the method is called more than once. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
        
        assertEquals(INSTANCE_B, result1);
        assertFalse(INSTANCE_B == result1);
        assertEquals(result1, result2);
        assertFalse(result1 == result2);
    }
    
    /** If the sub-parameters for parameter A
     * are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If a parameter for A is not defined, and parameter B is
     * defined, and the sub-parameters
     * for parameter B are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If no parameter is defined for either A or B, then an
     *  exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs6(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, SphereObjective.class);
    }
    
    /** If parameter A exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs7(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, String.class);
    }
    
    /** If parameter A is not defined,
     * and parameter B exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterTwoArgs8(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (two args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, String.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (three args)">
    /** If parameter A exists and has its sub-parameters correctly
     * defined, then an appropriate object should be created/returned,
     * and the same object should be returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);

        assertEquals(INSTANCE_A, result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If an instance has already been registered for this parameter, then
     * that instance should be returned (rather than creating a new one). It
     * does not matter if parameters describing the object have been defined.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs2(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        
        assertEquals(INSTANCE_A, result1);
        assertTrue(INSTANCE_A == result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If parameter A is not defined and no instance is registered, but the parameter B
     * is defined and its sub-parameters are correctly defined, then an object
     * corresponding to parameter B is created/returned.  The same object
     * should be returned if the method is called more than once. */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        
        assertEquals(INSTANCE_B, result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If the sub-parameters for parameter A
     * are not defined, and no instance is registered for that parameter,
     * then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
    }
    
    /** If a parameter or instance for A is not defined, and parameter B is
     * defined and no instance is registered for it, and the sub-parameters
     * for parameter B are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
    }
    
    /** If no parameter or instance is defined for either A or B, then the default value is returned.
     *  If the method is called twice, the same instance is returned. */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs6(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        
        assertEquals(DEFAULT_VALUE, result1);
        assertTrue(DEFAULT_VALUE == result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If parameter A exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs7(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        try {
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, RastriginObjective.class);
        }
        catch (Throwable e) {
            throw e;
        }
    }
    
    /** If parameter A is not defined nor an instance registered for it,
     * and parameter B exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalInstanceFromParameterThreeArgs8(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, RastriginObjective.class);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Optional Parameter (three args, new instance)">
    /** If parameter A exists and has its sub-parameters correctly
     * defined, then an appropriate object is created,
     * and a *different* object is returned if we call the method twice.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs1(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);

        assertEquals(INSTANCE_A, result1);
        assertFalse(INSTANCE_A == result1);
        assertEquals(result1, result2);
        assertFalse(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If parameter A is not defined and no instance is registered, but the parameter B
     * is defined and its sub-parameters are correctly defined, then an object
     * corresponding to parameter B is created.  A *different* object
     * is returned if the method is called more than once. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs3(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isRegistered(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        
        assertEquals(INSTANCE_B, result1);
        assertFalse(INSTANCE_B == result1);
        assertEquals(result1, result2);
        assertFalse(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If the sub-parameters for parameter A
     * are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs4(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
    }
    
    /** If a parameter for A is not defined, and parameter B is
     * defined, and the sub-parameters
     * for parameter B are not defined, then an exception is thrown. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs5(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeFalse(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
    }
    
    /** If no parameter is defined for either A or B, then the default value is returned.
     *  If the method is called twice, the same instance is returned. */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs6(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeFalse(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        
        final ObjectiveFunction result1 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        final ObjectiveFunction result2 = sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, ObjectiveFunction.class);
        
        assertEquals(DEFAULT_VALUE, result1);
        assertTrue(DEFAULT_VALUE == result1);
        assertTrue(result1 == result2);
        assertTrue(sut.repOK());
    }
    
    /** If parameter A exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs7(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeTrue(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_A));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, RastriginObjective.class);
    }
    
    /** If parameter A is not defined,
     * and parameter B exists and has its sub-parameters correctly
     * defined, but the object returned does not match the expected superclass,
     * then an exception is thrown.
     */
    @Theory
    public void testGetOptionalNewInstanceFromParameterThreeArgs8(final Parameters.Builder aBuilder,  final AInstance aInstance, final Parameters b, final BInstance bInstance, final ObjectiveFunction defaultValue) {
        final Parameters sut = assembleParameters(aBuilder, aInstance, b, bInstance);
        System.out.println(String.format("getOptionalNewInstanceFromParameter (three args)\n%s, %s", sut, defaultValue));
        assumeFalse(sut.isDefined(PARAMETER_NAME_A));
        assumeTrue(sut.isDefined(PARAMETER_NAME_B));
        assumeTrue(sut.isDefined(SUB_PARAMETER_NAME_B));
        assumeTrue(defaultValue != null);
        thrown.expect(IllegalStateException.class);
        
        sut.getOptionalNewInstanceFromParameter(PARAMETER_NAME_A, PARAMETER_NAME_B, defaultValue, RastriginObjective.class);
    }
    
    // </editor-fold>
    
    // </editor-fold>
}
