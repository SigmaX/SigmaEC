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
        
        if (populationSize <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": populationSize is <= 0, must be positive.");
        if (numBits <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBits is <= 0, must be positive.");
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
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

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return populationSize > 0
                && numBits > 0
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitStringInitializer))
            return false;
        final BitStringInitializer ref = (BitStringInitializer) o;
        return populationSize == ref.populationSize
                && numBits == ref.numBits
                && random.equals(random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.populationSize;
        hash = 67 * hash + this.numBits;
        hash = 67 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: populationSize=%d, numBits=%d, random=%s]", this.getClass().getSimpleName(), populationSize, numBits, random.toString());
    }
    // </editor-fold>
    
}
