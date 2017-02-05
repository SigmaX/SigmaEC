package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.test.TestIndividual;
import SigmaEC.test.TestObjective;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class MultiplicativeNoiseObjectiveTest {
    private final static String BASE = "base";
    
    public MultiplicativeNoiseObjectiveTest() {
    }
    
    private static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_RANDOM), SRandom.class.getName())
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_OBJECTIVE), TestObjective.class.getName())
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_STD_FRACTION), "0")
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_GLOBAL_BEST_FITNESS), "0");
    }

    @Test
    public void testFitness1() {
        System.out.println("fitness");
        final double fitness = 10.0;
        final TestIndividual ind = new TestIndividual(10.0);
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams().build(), BASE);
        double result = instance.fitness(ind).asScalar();
        assertEquals(fitness, result, 0.000000001);
    }

    @Test
    public void testFitness2() {
        System.out.println("fitness");
        final double fitness = 10.0;
        final double std = 0.1;
        final int numSamples = 10000;
        final TestIndividual ind = new TestIndividual(fitness);
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_STD_FRACTION), String.valueOf(std))
                .build(), BASE);
        final double[] resultSamples = new double[numSamples];
        for (int i = 0; i < numSamples; i++)
            resultSamples[i] = instance.fitness(ind).asScalar();
        
        final double resultMean = Statistics.mean(resultSamples);
        final double resultStd = Statistics.std(resultSamples);
        assertEquals(fitness, resultMean, 0.1);
        assertEquals(std, resultStd/fitness, 0.005);
    }

    @Test
    public void testFitness3() {
        System.out.println("fitness");
        final double fitness = -10.0;
        final double std = 0.1;
        final int numSamples = 10000;
        final TestIndividual ind = new TestIndividual(fitness);
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_STD_FRACTION), String.valueOf(std))
                .build(), BASE);
        final double[] resultSamples = new double[numSamples];
        for (int i = 0; i < numSamples; i++)
            resultSamples[i] = instance.fitness(ind).asScalar();
        
        final double resultMean = Statistics.mean(resultSamples);
        final double resultStd = Statistics.std(resultSamples);
        assertEquals(fitness, resultMean, 0.1);
        assertEquals(std, resultStd/Math.abs(fitness), 0.005);
    }

    @Test
    public void testFitness4() {
        System.out.println("fitness");
        final double fitness = 10.0;
        final double globalBest = -20.0;
        final double std = 0.1;
        final int numSamples = 10000;
        final TestIndividual ind = new TestIndividual(fitness);
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_STD_FRACTION), String.valueOf(std))
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_GLOBAL_BEST_FITNESS), String.valueOf(globalBest))
                .build(), BASE);
        final double[] resultSamples = new double[numSamples];
        for (int i = 0; i < numSamples; i++)
            resultSamples[i] = instance.fitness(ind).asScalar();
        
        final double resultMean = Statistics.mean(resultSamples);
        final double resultStd = Statistics.std(resultSamples);
        assertEquals(fitness, resultMean, 0.1);
        assertEquals(std, resultStd/Math.abs(fitness - globalBest), 0.005);
    }

    @Test
    public void testFitness5() {
        System.out.println("fitness");
        final double fitness = 20.0;
        final double globalBest = -20.0;
        final double std = 0.1;
        final int numSamples = 10000;
        final TestIndividual ind = new TestIndividual(fitness);
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_STD_FRACTION), String.valueOf(std))
                .setParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_GLOBAL_BEST_FITNESS), String.valueOf(globalBest))
                .build(), BASE);
        final double[] resultSamples = new double[numSamples];
        for (int i = 0; i < numSamples; i++)
            resultSamples[i] = instance.fitness(ind).asScalar();
        
        final double resultMean = Statistics.mean(resultSamples);
        final double resultStd = Statistics.std(resultSamples);
        assertEquals(fitness, resultMean, 0.1);
        assertEquals(std, resultStd/Math.abs(fitness - globalBest), 0.005);
    }

    @Test
    public void testGetNumDimensions() {
        System.out.println("getNumDimensions");
        MultiplicativeNoiseObjective instance = null;
        int expResult = 0;
        int result = instance.getNumDimensions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStep method, of class MultiplicativeNoiseObjective.
     */
    @Test
    public void testSetStep() {
        System.out.println("setStep");
        int i = 0;
        MultiplicativeNoiseObjective instance = null;
        instance.setStep(i);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class MultiplicativeNoiseObjective.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        MultiplicativeNoiseObjective instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
