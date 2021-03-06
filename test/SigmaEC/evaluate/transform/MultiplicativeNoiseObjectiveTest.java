package SigmaEC.evaluate.transform;

import SigmaEC.SRandom;
import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.test.TestIndividual;
import SigmaEC.test.TestObjective;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.Properties;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
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
        // Mocking the wrapped objective so we can specify its numDimensions as an indirect input to the SUT
        final Mockery context = new Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective = context.mock(ObjectiveFunction.class);
        context.checking(new Expectations() {{
            allowing (objective).getNumDimensions(); will(returnValue(2));
        }});
        
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .clearParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_OBJECTIVE))
                .registerInstance(Parameters.push(BASE, MultiplicativeNoiseObjective.P_OBJECTIVE), objective)
                .build(), BASE);
        
        int expResult = 2;
        int result = instance.getNumDimensions();
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of setStep method, of class MultiplicativeNoiseObjective. */
    @Test
    public void testSetStep() {
        System.out.println("setStep");
        final int step = 17;
        // Mocking the wrapped objective so we can check that its setStep method is called as an indirect output of the SUT
        final Mockery context = new Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        final ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> objective = context.mock(ObjectiveFunction.class);
        context.checking(new Expectations() {{
            allowing (objective).getNumDimensions(); will(returnValue(2));
            oneOf (objective).setStep(17);
        }});
        
        final MultiplicativeNoiseObjective<TestIndividual> instance = new MultiplicativeNoiseObjective(getParams()
                .clearParameter(Parameters.push(BASE, MultiplicativeNoiseObjective.P_OBJECTIVE))
                .registerInstance(Parameters.push(BASE, MultiplicativeNoiseObjective.P_OBJECTIVE), objective)
                .build(), BASE);
        
        instance.setStep(step);
        context.assertIsSatisfied();
        assertTrue(instance.repOK());
    }
}
