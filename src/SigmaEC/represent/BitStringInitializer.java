package SigmaEC.represent;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringInitializer extends Initializer<BitStringIndividual> {
    private final static String P_POPULATION_SIZE = "populationSize";
    private final int populationSize;
    
    public static class Builder
    
    
    @Override
    public List<BitStringIndividual> generateInitialPopulation(final Properties properties, final String base) {
        
        return new ArrayList<BitStringIndividual>(params.populationSize) {{
            for (int i = 0; i < params.populationSize; i++)
                add(new BitStringIndividual(random, params.numBits));
        }};
    }

    @Override
    public boolean repOK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
