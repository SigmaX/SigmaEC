package SigmaEC.test;

import SigmaEC.represent.Initializer;
import SigmaEC.represent.linear.BitStringIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * This test stub provides initial populations that can be used to test
 * various evolutionary algorithms.
 * 
 * @author Eric O. Scott
 */
public class TestInitializer extends Initializer<BitStringIndividual> {
    public final static String P_POPULATION = "population";
    public final static int NUM_POPULATIONS = 3;
    
    private final int populationID;
    
    public TestInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        populationID = parameters.getOptionalIntParameter(Parameters.push(base, P_POPULATION), 0);
        if (populationID < 0 || populationID >= NUM_POPULATIONS)
            throw new IllegalStateException(String.format("%s: parameter '%s' is set to %d, but must be >= 0 and less than %d.", this.getClass().getSimpleName(), Parameters.push(base, P_POPULATION), populationID, NUM_POPULATIONS));
        assert(repOK());
    }
    
    public TestInitializer(final int populationID) {
        assert(populationID >= 0);
        assert(populationID < NUM_POPULATIONS);
        this.populationID = populationID;
        assert(repOK());
    }
    
    @Override
    public List<BitStringIndividual> generatePopulation() {
        final List<BitStringIndividual> population = new ArrayList<>();
        switch (populationID) {
            case 0: // 12 random bitstrings of length 5
                population.add(new BitStringIndividual.Builder(new boolean[] { false, true, true, false, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, false, false, true, true }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, true, true, true, true }).setFitness(1.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { true, true, false, true, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, false, true, false, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { true, true, false, true, true }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { true, true, true, false, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, true, true, false, false }).setFitness(0.9).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, false, true, false, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { true, false, true, true, false }).setFitness(0.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, false, true, true, false }).setFitness(-1.0).build());
                population.add(new BitStringIndividual.Builder(new boolean[] { false, false, true, false, true }).setFitness(0.0).build());
                break;
            case 1: // A single individual
                population.add(new BitStringIndividual.Builder(new boolean[] { true, false, true, false, false }).build());
                break;
            case 2: // A single individual with only one gene
                population.add(new BitStringIndividual.Builder(new boolean[] { true }).build());
                break;
            default:
                throw new IllegalStateException();
        }
        assert(repOK());
        return population;
    }

    @Override
    public BitStringIndividual generateIndividual() {
        return generatePopulation().get(0);
    }

    @Override
    public final boolean repOK() {
        return P_POPULATION != null
                && !P_POPULATION.isEmpty()
                && NUM_POPULATIONS > 0
                && populationID >= 0
                && populationID < NUM_POPULATIONS;
    }

    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
