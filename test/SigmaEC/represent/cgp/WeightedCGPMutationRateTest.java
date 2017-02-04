package SigmaEC.represent.cgp;

import SigmaEC.evaluate.objective.function.ConcatenatedBooleanFunction;
import SigmaEC.evaluate.objective.function.NAND;
import SigmaEC.measure.ConcatenatedBooleanObjectivePopulationMetric;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Parameters;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class WeightedCGPMutationRateTest {
    private final static String BASE = "base";
    
    public WeightedCGPMutationRateTest() {
    }
    
    private final static Parameters.Builder getParams() {
        return new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_MIN), "0.1")
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_MAX), "0.5")
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_C), "3")
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_SCHEME), "LINEAR")
                
                // High-level CGP parameters
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_CGP_PARAMETERS), "%cgpParams")
                .setParameter("cgpParams", "SigmaEC.represent.cgp.CGPParameters")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_NUM_INPUTS), "2")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_NUM_OUTPUTS), "2")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_NUM_LAYERS), "2")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_NUM_NODES_PER_LAYER), "3")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_MAX_ARITY), "2")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_LEVELS_BACK), "2")
                .setParameter(Parameters.push("cgpParams", CGPParameters.P_NUM_PRIMITIVES), "4")
                
                // Genotype-phenotype decoder
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_DECODER), "%decoder")
                .setParameter("decoder", "SigmaEC.represent.cgp.IntVectorToCartesianIndividualDecoder")
                .setParameter(Parameters.push("decoder", IntVectorToCartesianIndividualDecoder.P_CGP_PARAMETERS), "%cgpParams")
                .setParameter(Parameters.push("decoder", IntVectorToCartesianIndividualDecoder.P_PRIMITIVES), "SigmaEC.evaluate.objective.function.NAND")
                
                // Objective function
                .setParameter("objective", "SigmaEC.evaluate.objective.function.ConcatenatedBooleanFunction")
                .setParameter(Parameters.push("objective", ConcatenatedBooleanFunction.P_FUNCTIONS),
                        "SigmaEC.evaluate.objective.function.XOR," +
                        "SigmaEC.evaluate.objective.function.AND")
                
                // Fitness evaluation components
                .setParameter(Parameters.push(BASE, WeightedCGPMutationRate.P_POPULATION_METRIC), "%metric")
                .setParameter("metric", "SigmaEC.measure.ConcatenatedBooleanObjectivePopulationMetric")
                .setParameter(Parameters.push("metric", ConcatenatedBooleanObjectivePopulationMetric.P_DECODER), "%decoder")
                .setParameter(Parameters.push("metric", ConcatenatedBooleanObjectivePopulationMetric.P_TARGET_FUNCTION), "%objective")
                .setParameter(Parameters.push("metric", ConcatenatedBooleanObjectivePopulationMetric.P_COMPARATOR), "SigmaEC.select.FitnessComparator")
                .setParameter(Parameters.push(Parameters.push("metric", ConcatenatedBooleanObjectivePopulationMetric.P_COMPARATOR), FitnessComparator.P_MINIMIZE), "false")
                .setParameter(Parameters.push(Parameters.push("metric", ConcatenatedBooleanObjectivePopulationMetric.P_COMPARATOR), FitnessComparator.P_EQUAL_IS_BETTER), "true");
    }
    
    private final static LinearGenomeIndividual ind = new IntVectorIndividual.Builder(new int[] {
            0, 0, 1,
            0, 0, 1,
            0, 1, 1,
            0, 0, 2,
            0, 2, 3,
            0, 4, 4,
            5, 6
        }).setFitness(0.625).build();

    /** Test of getRateForGene method, of class WeightedCGPMutationRate. */
    @Test
    public void testGetRateForGene() {
        System.out.println("getRateForGene");
        final WeightedCGPMutationRate instance = new WeightedCGPMutationRate(getParams().build(), BASE);
        assertEquals(0.25, instance.getRateForGene(0, 0, ind), 0);
        assertEquals(0.25, instance.getRateForGene(1, 0, ind), 0);
        assertEquals(0.25, instance.getRateForGene(2, 0, ind), 0);
        
        assertEquals(0.1, instance.getRateForGene(3, 0, ind), 0);
        assertEquals(0.1, instance.getRateForGene(4, 0, ind), 0);
        assertEquals(0.1, instance.getRateForGene(5, 0, ind), 0);
        
        assertEquals(0.5, instance.getRateForGene(6, 0, ind), 0);
        assertEquals(0.5, instance.getRateForGene(7, 0, ind), 0);
        assertEquals(0.5, instance.getRateForGene(8, 0, ind), 0);
        
        assertEquals(0.4, instance.getRateForGene(9, 0, ind), 0);
        assertEquals(0.4, instance.getRateForGene(10, 0, ind), 0);
        assertEquals(0.4, instance.getRateForGene(11, 0, ind), 0);
        
        assertEquals(0.1, instance.getRateForGene(12, 0, ind), 0);
        assertEquals(0.1, instance.getRateForGene(13, 0, ind), 0);
        assertEquals(0.1, instance.getRateForGene(14, 0, ind), 0);
        
        assertEquals(0.5, instance.getRateForGene(15, 0, ind), 0);
        assertEquals(0.5, instance.getRateForGene(16, 0, ind), 0);
        assertEquals(0.5, instance.getRateForGene(17, 0, ind), 0);
        
        assertEquals(0.2, instance.getRateForGene(18, 0, ind), 0);
        assertEquals(0.5, instance.getRateForGene(19, 0, ind), 0);
        assertTrue(instance.repOK());
    }
}
