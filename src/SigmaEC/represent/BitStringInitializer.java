package SigmaEC.represent;

import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringInitializer extends Initializer<BitStringIndividual> {
    private final int populationSize;
    private final int numBits;
    private final Random random;
    
    private BitStringInitializer(final Builder builder) {
        assert(builder != null);
        this.populationSize = builder.populationSize;
        this.numBits = builder.numBits;
        this.random = builder.random;
        assert(repOK());
    }
    
    public static class Builder implements InitializerBuilder<BitStringIndividual> {
        private final static String P_POPULATION_SIZE = "populationSize";
        private final static String P_NUM_BITS = "numBits";
        
        private int populationSize;
        private int numBits;
        private Random random;
        
        public Builder(final int populationSize, final int numBits) {
            assert(populationSize > 1);
            assert(numBits > 0);
            this.populationSize = populationSize;
            this.numBits = numBits;
        }
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.populationSize = Parameters.getIntParameter(properties, Parameters.push(base, P_POPULATION_SIZE));
            this.numBits = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_BITS));
        }
        
        @Override
        public BitStringInitializer build() {
            return new BitStringInitializer(this);
        }

        @Override
        public Builder random(final Random random) {
            this.random = random;
            return this;
        }
        
        public Builder populationSize(final int populationSize) {
            assert(populationSize > 1);
            this.populationSize = populationSize;
            return this;
        }
        
        public Builder numBits(final int numBits) {
            assert(numBits > 0);
            this.numBits = numBits;
            return this;
        }
    }
    
    
    @Override
    public List<BitStringIndividual> generateInitialPopulation() {
        
        return new ArrayList<BitStringIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(new BitStringIndividual(random, numBits));
        }};
    }

    @Override
    public final boolean repOK() {
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
