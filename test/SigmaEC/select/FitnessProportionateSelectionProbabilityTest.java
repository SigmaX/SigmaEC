/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SigmaEC.select;

import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Statistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eric
 */
public class FitnessProportionateSelectionProbabilityTest {
    private final static String BASE = "base";
    private Parameters.Builder paramBuilder;
    private List<TestIndividual> population;
    
    public FitnessProportionateSelectionProbabilityTest() { }
    
    @Before
    public void setUp() {
        paramBuilder = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, FitnessProportionateSelectionProbability.P_DECODER), "SigmaEC.test.TestDecoder")
                .setParameter(Parameters.push(BASE, FitnessProportionateSelectionProbability.P_OBJECTIVE), "SigmaEC.test.TestObjective");
        population = new ArrayList<TestIndividual>() {{
                add(new TestIndividual(3.0));
                add(new TestIndividual(1.0));
                add(new TestIndividual(8.0));
                add(new TestIndividual(5.0));
                add(new TestIndividual(8.1));
        }};
    }

    @Test
    public void testProbability() {
        System.out.println("probability");
        final FitnessProportionateSelectionProbability<TestIndividual, Double> instance = new FitnessProportionateSelectionProbability<TestIndividual, Double>(paramBuilder.build(), BASE);
        double[] expResult = new double[] { 0.1195219123505976,
                                            0.0398406374501992,
                                            0.3187250996015936,
                                            0.199203187250996,
                                            0.32270916334661354
        };
        double[] result = instance.probability(population);
        assertTrue(Statistics.sumsToOne(result));
        assertArrayEquals(expResult, result, 0.00001);
    }

    @Test
    public void testProbabilityMinimize() {
        System.out.println("probability");
        final FitnessProportionateSelectionProbability<TestIndividual, Double> instance = new FitnessProportionateSelectionProbability<TestIndividual, Double>(paramBuilder
                .setParameter(Parameters.push(BASE, FitnessProportionateSelectionProbability.P_MINIMIZE), "true")
                .build(), BASE);
        // XXX Isn't it odd that "flipping" the densities for minimization results in a smaller spread of probabilities?  There may be a better way of inverting the distribution.
        double[] expResult = new double[] { 0.2201195219123506,
                                            0.2400398406374502,
                                            0.1703187250996016,
                                            0.20019920318725098,
                                            0.16932270916334663
        };
        double[] result = instance.probability(population);
        assertTrue(Statistics.sumsToOne(result));
        assertArrayEquals(expResult, result, 0.00001);
    }
}
