package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ConstantObjective;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Matrix;
import SigmaEC.util.math.Vector;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class AffineTransformedDoubleObjectiveTest {
    final private static double MAX_FITNESS = 1.0;
    final private static double MIN_FITNESS = Double.NEGATIVE_INFINITY;
    final private static double SPIKE_WIDTH = 0.01;
    final private static String BASE = "base";
    private ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> wrappedObjective;
    private AffineTransformedDoubleObjective sut;
    
    public AffineTransformedDoubleObjectiveTest() {
    }
    
    @Before
    public void setUp() {
        wrappedObjective = spikeObjective(new double[] { 0.0, 0.0, 1.0 }, SPIKE_WIDTH);
        sut = new AffineTransformedDoubleObjective(new double[] { 0.0, 0.0, 0.0 }, 1.0, wrappedObjective);
    }
    
    private static ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> spikeObjective(final double[] position, final double width) {
        ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> obj = new ConstantObjective<>(new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, ConstantObjective.P_NUM_DIMENSIONS), String.valueOf(position.length))
                .setParameter(Parameters.push(BASE, ConstantObjective.P_VALUE), String.valueOf(MAX_FITNESS)).build(), BASE);
        obj = new BoundedDoubleObjective(position.length, width, obj, MIN_FITNESS);
        return new TranslatedDoubleObjective(Vector.vectorNegate(position), obj);
    }
    
    @Test
    public void testConstructor() {
        System.out.println("constructor");
        assertEquals(wrappedObjective, sut.getWrappedObjective());
        assertTrue(sut.repOK());
    }

    /** Test of getNumDimensions method, of class AffineTransformedDoubleObjective. */
    @Test
    public void testGetNumDimensions() {
        System.out.println("getNumDimensions");
        assertEquals(3, sut.getNumDimensions());
        assertTrue(sut.repOK());
    }

    /** Test of getTransformationMatrix method, of class AffineTransformedDoubleObjective. */
    @Test
    public void testTransformationMatrix() {
        System.out.println("getTransformationMatrix");
        final double[][] matrix = sut.getTransformationMatrix();
        final double[][] expectedMatrix = new double[][] {
            new double[] { 1.0, 0.0, 0.0 },
            new double[] { 0.0, 1.0, 0.0 },
            new double[] { 0.0, 0.0, 1.0 }
        };
        assertTrue(Matrix.equals(expectedMatrix, matrix, 0.000001));
        
        final AffineTransformedDoubleObjective sut2 = new AffineTransformedDoubleObjective(new double[] { Math.PI/4, 0.0, 0.0 }, 1.0, wrappedObjective);
        final double[][] matrix2 = sut2.getTransformationMatrix();
        final double[][] expectedMatrix2 = new double[][] {
            new double[] { Math.sqrt(2.0)/2.0, -Math.sqrt(2.0)/2.0, 0.0 },
            new double[] { Math.sqrt(2.0)/2.0, Math.sqrt(2.0)/2.0, 0.0 },
            new double[] { 0.0, 0.0, 1.0 }
        };
        assertTrue(Matrix.equals(expectedMatrix2, matrix2, 0.000001));
        
        final AffineTransformedDoubleObjective sut3 = new AffineTransformedDoubleObjective(new double[] { 0.0, Math.PI/4, 0.0 }, 1.0, wrappedObjective);
        final double[][] matrix3 = sut3.getTransformationMatrix();
        final double[][] expectedMatrix3 = new double[][] {
            new double[] { Math.sqrt(2.0)/2.0, 0.0, -Math.sqrt(2.0)/2.0 },
            new double[] { 0.0, 1.0, 0.0 },
            new double[] { Math.sqrt(2.0)/2.0, 0.0, Math.sqrt(2.0)/2.0 }
        };
        assertTrue(Matrix.equals(expectedMatrix3, matrix3, 0.000001));
        
        assertTrue(sut.repOK());
    }

    /** Test of transform method, of class AffineTransformedDoubleObjective. */
    @Test
    public void testTransform() {
        System.out.println("transform");
        final AffineTransformedDoubleObjective sut2 = new AffineTransformedDoubleObjective(new double[] { 0.0, Math.PI/4, 0.0 }, 1.0, wrappedObjective);
        assertEquals(MAX_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { Math.sqrt(2.0)/2, 0.0, Math.sqrt(2.0)/2 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { -Math.sqrt(2.0)/2, 0.0, Math.sqrt(2.0)/2 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 1.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 1.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { 1.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut2.fitness(new DoubleVectorIndividual.Builder(new double[] { 2.0, 2.0, 2.0 }).build()).asScalar(), 0.000001);
        
        final AffineTransformedDoubleObjective sut3 = new AffineTransformedDoubleObjective(new double[] { 0.0, 0.0, Math.PI/3 }, 1.0, wrappedObjective);
        assertEquals(MAX_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, Math.sqrt(3)/2, 0.5 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, -Math.sqrt(3)/2, 0.5 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 1.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 1.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 1.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut3.fitness(new DoubleVectorIndividual.Builder(new double[] { 2.0, 2.0, 2.0 }).build()).asScalar(), 0.000001);
        
        final AffineTransformedDoubleObjective sut4 = new AffineTransformedDoubleObjective(new double[] { Math.PI/2, Math.PI/2, Math.PI/2 }, 1.0, wrappedObjective);
        assertEquals(MAX_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 1.0, 0.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 1.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 1.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 1.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 1.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut4.fitness(new DoubleVectorIndividual.Builder(new double[] { 2.0, 2.0, 2.0 }).build()).asScalar(), 0.000001);
    }

    /** Test of fitness method, of class AffineTransformedDoubleObjective. */
    @Test
    public void testFitness() {
        System.out.println("fitness");
        assertEquals(MAX_FITNESS, wrappedObjective.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 1.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, wrappedObjective.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        
        assertEquals(MAX_FITNESS, sut.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 1.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut.fitness(new DoubleVectorIndividual.Builder(new double[] { 0.0, 1.0, 0.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut.fitness(new DoubleVectorIndividual.Builder(new double[] { 1.0, 0.0, 2.0 }).build()).asScalar(), 0.000001);
        assertEquals(MIN_FITNESS, sut.fitness(new DoubleVectorIndividual.Builder(new double[] { 2.0, 2.0, 2.0 }).build()).asScalar(), 0.000001);
        
        assertTrue(sut.repOK());
    }

    /**
     * Test of equals method, of class AffineTransformedDoubleObjective.
     */
    /*
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object ref = null;
        AffineTransformedDoubleObjective instance = null;
        boolean expResult = false;
        boolean result = instance.equals(ref);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    } */

    /**
     * Test of hashCode method, of class AffineTransformedDoubleObjective.
     */
    /*
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        AffineTransformedDoubleObjective instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    } */
}