package SigmaEC.represent;

import SigmaEC.BuilderT;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Properties;

/**
 * Interpret a string of bits as a vector of doubles via big-endian encoding.
 * @author Eric 'Siggy' Scott
 */
public class BitStringToDoubleVectorDecoder extends Decoder<BitStringIndividual, DoubleVectorPhenotype> {
    final private int numBitsPerDimension;
    final private int numDimensions;
    final private int lowestSignificance;

    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public int getNumBitsPerDimension() {
        return numBitsPerDimension;
    }

    public int getNumDimensions() {
        return numDimensions;
    }

    public int getLowestSignificance() {
        return lowestSignificance;
    }
    // </editor-fold>
    
    public BitStringToDoubleVectorDecoder(final int numBitsPerDimension, final int numDimensions, final int lowestSignificance) {
        if (numBitsPerDimension <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBitsPerDimension is <= 0, must be positive.");
        if (numDimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is <= 0, must be positive.");
        this.numBitsPerDimension = numBitsPerDimension;
        this.numDimensions = numDimensions;
        this.lowestSignificance = lowestSignificance;
        assert(repOK());
    }
    
    private BitStringToDoubleVectorDecoder(final Builder builder) {
        this(builder.numBitsPerDimension, builder.numDimensions, builder.lowestSignificance);
        assert(repOK());
    } 

    @Override
    public DoubleVectorPhenotype decode(final BitStringIndividual individual) {
        assert(individual.size() == numBitsPerDimension*numDimensions);
        final List<BitGene> genome = individual.getGenome();
        final double[] phenotype = new double[numDimensions];
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            for (int place = 0; place < numBitsPerDimension; place++) {
                final int power = lowestSignificance + place;
                if (genome.get(dimension*numBitsPerDimension + place).value)
                    phenotype[dimension] += Math.pow(2, power);
            }
        }
        return new DoubleVectorPhenotype(phenotype);
    }
    
    public static class Builder implements BuilderT<BitStringToDoubleVectorDecoder> {
        public final static String P_NUM_BITS_PER_DIMENSION = "numBitsPerDimension";
        public final static String P_NUM_DIMENSIONS = "numDimensions";
        public final static String P_LOWEST_SIGNIFICANCE = "lowestSignificance";
        
        private int numBitsPerDimension;
        private int numDimensions;
        private int lowestSignificance;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            
            this.numBitsPerDimension = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_BITS_PER_DIMENSION));
            this.numDimensions = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_DIMENSIONS));
            this.lowestSignificance = Parameters.getIntParameter(properties, Parameters.push(base, P_LOWEST_SIGNIFICANCE));
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
                && numDimensions == ref.numDimensions
                && lowestSignificance == ref.lowestSignificance;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.numBitsPerDimension;
        hash = 37 * hash + this.numDimensions;
        hash = 37 * hash + this.lowestSignificance;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, numBitsPerDimension=%d, lowestSignificance=%d]", this.getClass().getSimpleName(), numDimensions, numBitsPerDimension, lowestSignificance);
    }
    // </editor-fold>
    
}
