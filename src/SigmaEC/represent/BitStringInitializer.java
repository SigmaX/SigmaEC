package SigmaEC.represent;

import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringInitializer extends Initializer<BitStringIndividual> {
    private final static String P_POPULATION_SIZE = "populationSize";
    private final static String P_NUM_BITS = "numBits";
    private final static String P_RANDOM = "random";
    
    private final int populationSize;
    private final int numBits;
    private final Random random;
    
    public BitStringInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        this.numBits = parameters.getIntParameter(Parameters.push(base, P_NUM_BITS));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        
        if (populationSize <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": populationSize is <= 0, must be positive.");
        if (numBits <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBits is <= 0, must be positive.");
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
        assert(repOK());
    }
    
    @Override
    public List<BitStringIndividual> generatePopulation() {
        
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
