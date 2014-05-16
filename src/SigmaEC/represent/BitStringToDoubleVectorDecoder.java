package SigmaEC.represent;

import SigmaEC.util.Parameters;
import java.util.List;

/**
 * Interpret a string of bits as a vector of doubles via big-endian encoding.
 * @author Eric 'Siggy' Scott
 */
public class BitStringToDoubleVectorDecoder extends Decoder<BitStringIndividual, DoubleVectorPhenotype> {
    public final static String P_NUM_BITS_PER_DIMENSION = "numBitsPerDimension";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_LOWEST_SIGNIFICANCE = "lowestSignificance";
        
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
    
    public BitStringToDoubleVectorDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numBitsPerDimension = parameters.getIntParameter(Parameters.push(base, P_NUM_BITS_PER_DIMENSION));
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        this.lowestSignificance = parameters.getIntParameter(Parameters.push(base, P_LOWEST_SIGNIFICANCE));
        
        if (numBitsPerDimension <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBitsPerDimension is <= 0, must be positive.");
        if (numDimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is <= 0, must be positive.");
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
