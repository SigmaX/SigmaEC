package SigmaEC.represent.linear;

import SigmaEC.represent.Decoder;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * Interpret a string of Gray coded bits as a vector of doubles via big-endian encoding.
 * There are many different kinds of Gray codes, but in this case we use a
 * fairly standard reflective Gray code.
 * @author Jeff Bassett
 */
public class GrayBitStringToDoubleVectorDecoder extends Decoder<BitStringIndividual, DoubleVectorIndividual> {
    public final static String P_NUM_BITS_PER_DIMENSION = "numBitsPerDimension";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_LOWEST_SIGNIFICANCE = "lowestSignificance";
    public final static String P_MIN = "min";
    public final static String P_MAX = "max";
        
    final private int numBitsPerDimension;
    final private int numDimensions;
    final private double min;
    final private double max;

    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public int getNumBitsPerDimension() {
        return numBitsPerDimension;
    }

    public int getNumDimensions() {
        return numDimensions;
    }
    // </editor-fold>
    
    public GrayBitStringToDoubleVectorDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numBitsPerDimension = parameters.getIntParameter(Parameters.push(base, P_NUM_BITS_PER_DIMENSION));
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        this.min = parameters.getDoubleParameter(Parameters.push(base, P_MIN));
        this.max = parameters.getDoubleParameter(Parameters.push(base, P_MAX));
        
        if (numBitsPerDimension <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numBitsPerDimension is <= 0, must be positive.");
        if (numDimensions <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is <= 0, must be positive.");
        if (min >= max)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": max is <= min.");
        assert(repOK());
    }

    @Override
    public DoubleVectorIndividual decode(final BitStringIndividual individual) {
        assert(individual.size() == numBitsPerDimension*numDimensions);
        final List<BitGene> genome = individual.getGenome();
        final double[] phenotype = new double[numDimensions];
        final double maxPhenotypeValue = Math.pow(2, numBitsPerDimension) - 1.0;
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            int i = 0;
            int lastb = 0;
            for (int place = 0; place < numBitsPerDimension; place++) {
                int g = (genome.get(dimension*numBitsPerDimension + place).value ? 1 : 0);
                int b = g ^ lastb;   // XOR
                i = (i << 1) + b;
                lastb = b;
            }
            phenotype[dimension] = rescale(i, 0, maxPhenotypeValue, min, max);
        }
        return new DoubleVectorIndividual(phenotype);
    }
    
    private static double rescale(final double val, final double oldMin, final double oldMax, final double newMin, final double newMax) {
        assert(!Double.isNaN(val));
        assert(!Double.isInfinite(val));
        assert(!Double.isNaN(oldMin));
        assert(!Double.isInfinite(oldMin));
        assert(!Double.isNaN(oldMax));
        assert(!Double.isInfinite(oldMax));
        assert(!Double.isNaN(newMin));
        assert(!Double.isInfinite(newMin));
        assert(!Double.isNaN(newMax));
        assert(!Double.isInfinite(newMax));
        assert(oldMax > oldMin);
        assert(val <= oldMax);
        assert(val >= oldMin);
        assert(newMax > newMin);
        
        return newMin + ((val - oldMin)/(oldMax - oldMin))*(newMax - newMin);
    }

    @Override
    public void reset() { assert(repOK()); }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numBitsPerDimension > 0
                && numDimensions > 0
                && !Double.isNaN(min)
                && !Double.isInfinite(min)
                && !Double.isNaN(max)
                && !Double.isInfinite(max)
                && min <= max;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GrayBitStringToDoubleVectorDecoder))
            return false;
        final GrayBitStringToDoubleVectorDecoder ref = (GrayBitStringToDoubleVectorDecoder) o;
        return numBitsPerDimension == ref.numBitsPerDimension
                && numDimensions == ref.numDimensions
                && Misc.doubleEquals(min, ref.min)
                && Misc.doubleEquals(max, ref.max);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.numBitsPerDimension;
        hash = 53 * hash + this.numDimensions;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, numBitsPerDimension=%d, min=%f, max=%f]", this.getClass().getSimpleName(), numDimensions, numBitsPerDimension, min, max);
    }
    // </editor-fold>
    
}
