package SigmaEC.represent;

import SigmaEC.BuilderT;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringToDoubleVectorDecoder extends Decoder<BitStringIndividual, DoubleVectorPhenotype> {
    final private int numBitsPerDimension;
    final private int numDimensions;
    final private int highestSignificance;
    
    public BitStringToDoubleVectorDecoder(final int numBitsPerDimension, final int numDimensions, final int highestSignificance) {
        if (numBitsPerDimension <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBitsPerDimension is <= 0, must be positive.");
        if (numDimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is <= 0, must be positive.");
        this.numBitsPerDimension = numBitsPerDimension;
        this.numDimensions = numDimensions;
        this.highestSignificance = highestSignificance;
        assert(repOK());
    }
    
    private BitStringToDoubleVectorDecoder(final Builder builder) {
        this(builder.numBitsPerDimension, builder.numDimensions, builder.highestSignificance);
        assert(repOK());
    } 

    @Override
    public DoubleVectorPhenotype decode(final BitStringIndividual individual) {
        assert(individual.size() == numBitsPerDimension*numDimensions);
        final List<BitGene> genome = individual.getGenome();
        final double[] phenotype = new double[numDimensions];
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            for (int place = 0; place < numBitsPerDimension; place++) {
                final int power = highestSignificance - place;
                if (genome.get(dimension*numBitsPerDimension + place).value)
                    phenotype[dimension] += Math.pow(2, power);
            }
        }
        return new DoubleVectorPhenotype(phenotype);
    }
    
    public static class Builder implements BuilderT<BitStringToDoubleVectorDecoder> {
        private final static String P_NUM_BITS_PER_DIMENSION = "numBitsPerDimension";
        private final static String P_NUM_DIMENSIONS = "numDimensions";
        private final static String P_HIGHEST_SIGNIFICANCE = "highestSignificance";
        
        private int numBitsPerDimension;
        private int numDimensions;
        private int highestSignificance;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            
            this.numBitsPerDimension = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_BITS_PER_DIMENSION));
            this.numDimensions = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_DIMENSIONS));
            this.highestSignificance = Parameters.getIntParameter(properties, Parameters.push(base, P_HIGHEST_SIGNIFICANCE));
        }

        @Override
        public BitStringToDoubleVectorDecoder build() {
            return new BitStringToDoubleVectorDecoder(this);
        }
    
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numBitsPerDimension > 0
                && numDimensions > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitStringToDoubleVectorDecoder))
            return false;
        final BitStringToDoubleVectorDecoder ref = (BitStringToDoubleVectorDecoder) o;
        return numBitsPerDimension == ref.numBitsPerDimension
                && numDimensions == ref.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.numBitsPerDimension;
        hash = 59 * hash + this.numDimensions;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numBitsPerDimension=%d, numDimensions=%d]", this.getClass().getSimpleName(), numBitsPerDimension, numDimensions);
    }
    // </editor-fold>
    
}
