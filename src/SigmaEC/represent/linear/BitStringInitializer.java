package SigmaEC.represent.linear;

import SigmaEC.represent.Initializer;
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
            throw new IllegalStateException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_POPULATION_SIZE));
        if (numBits <= 0)
            throw new IllegalStateException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_NUM_BITS));
        assert(repOK());
    }
    
    @Override
    public List<BitStringIndividual> generatePopulation() {
        final List<BitStringIndividual> population = new ArrayList<BitStringIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(new BitStringIndividual(random, numBits));
        }};
        assert(repOK());
        return population;
    }

    @Override
    public BitStringIndividual generateIndividual() {
        final BitStringIndividual newInd = new BitStringIndividual(random, numBits);
        assert(repOK());
        return newInd;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_BITS != null
                && !P_NUM_BITS.isEmpty()
                && P_POPULATION_SIZE != null
                && !P_POPULATION_SIZE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && populationSize > 0
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
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.populationSize;
        hash = 23 * hash + this.numBits;
        hash = 23 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s]", this.getClass().getSimpleName(),
                P_POPULATION_SIZE, populationSize,
                P_NUM_BITS, numBits,
                P_RANDOM, random);
    }
    // </editor-fold>
    
}
