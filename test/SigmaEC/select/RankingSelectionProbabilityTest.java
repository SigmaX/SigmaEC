/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SigmaEC.select;

import SigmaEC.test.TestIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class RankingSelectionProbabilityTest {
    private final static String BASE = "base";
    private Parameters.Builder paramBuilder;
    private List<TestIndividual> population;
    
    public RankingSelectionProbabilityTest() { }
    
    @Before
    public void setUp() {
        paramBuilder = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, RankingSelectionProbability.P_POWER), "1")
                .setParameter(Parameters.push(BASE, RankingSelectionProbability.P_COMPARATOR), "SigmaEC.select.ScalarFitnessComparator");
        population = new ArrayList<TestIndividual>() {{
                add(new TestIndividual(3.0));
                add(new TestIndividual(1.0));
                add(new TestIndividual(8.0));
                add(new TestIndividual(5.0));
                add(new TestIndividual(8.1));
        }};
    }

    @Test
    public void testProbability1() {
        System.out.println("probability (linear)");
        final Parameters params = paramBuilder.build();
        final RankingSelectionProbability instance = new RankingSelectionProbability(params, BASE);
        final int n = population.size();
        final double slope = 2.0/(n*(n+1));
        final double[] expResult = new double[] { 2*slope, 1*slope, 4*slope, 3*slope, 5*slope};
        double[] result = instance.probability(population);
        assertArrayEquals(expResult, result, 0.000001);
    }

    @Test
    public void testProbability2() {
        System.out.println("probability (quadratic)");
        final Parameters params = paramBuilder.setParameter(Parameters.push(BASE, RankingSelectionProbability.P_POWER), "2").build();
        final RankingSelectionProbability instance = new RankingSelectionProbability(params, BASE);
        final int n = population.size();
        final double unit = 6.0/(n*(n+1)*(2*n+1));
        final double[] expResult = new double[] { 2*2*unit, 1*unit, 4*4*unit, 3*3*unit, 5*5*unit};
        double[] result = instance.probability(population);
        assertArrayEquals(expResult, result, 0.000001);
    }
}
