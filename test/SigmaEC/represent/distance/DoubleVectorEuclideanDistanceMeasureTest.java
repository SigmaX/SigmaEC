package SigmaEC.represent.distance;

import SigmaEC.represent.linear.DoubleVectorIndividual;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Eric O. Scott
 */
@RunWith(Parameterized.class)
public class DoubleVectorEuclideanDistanceMeasureTest {
    private final DoubleVectorIndividual ind1;
    private final DoubleVectorIndividual ind2;
    private final double expectedDistance;
    
    private DoubleVectorEuclideanDistanceMeasure sut;
        
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {  new DoubleVectorIndividual.Builder(new double[] { 0, 0, 0 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 0, 0, 0 }).build(),
                    0
                 },
                 {  new DoubleVectorIndividual.Builder(new double[] { 0, 1 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 0, 0 }).build(),
                    1
                 },
                 {  new DoubleVectorIndividual.Builder(new double[] { 0, 0 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 1, 0 }).build(),
                    1
                 },
                 {  new DoubleVectorIndividual.Builder(new double[] { 0, 0 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 1, 1 }).build(),
                    Math.sqrt(2)
                 },
                 {  new DoubleVectorIndividual.Builder(new double[] { 5, 6, 7 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 9, 10, 11 }).build(),
                    6.92820323028
                 },
                 {  new DoubleVectorIndividual.Builder(new double[] { 5, -6, -7 }).build(),
                    new DoubleVectorIndividual.Builder(new double[] { 9, 10, 11 }).build(),
                    24.4131112315
                 }
           });
    }
    
    public DoubleVectorEuclideanDistanceMeasureTest(final DoubleVectorIndividual ind1, final DoubleVectorIndividual ind2, final double expectedDistance) {
        assert(ind1 != null);
        assert(ind2 != null);
        assert(Double.isFinite(expectedDistance));
        this.ind1 = ind1;
        this.ind2 = ind2;
        this.expectedDistance = expectedDistance;
    }
    
    @Before
    public void setUp() {
        sut = new DoubleVectorEuclideanDistanceMeasure(new SigmaEC.util.Parameters.Builder(new Properties()).build(), "");
    }

    /** Test of distance method, of class DoubleVectorEuclideanDistanceMeasure. */
    @Test
    public void testDistance() {
        System.out.println("distance");
        double result1 = sut.distance(ind1, ind2);
        assertEquals(expectedDistance, result1, 0.0000001);
        
        double result2 = sut.distance(ind2, ind1);
        assertEquals(expectedDistance, result2, 0.0000001);
    }
}
