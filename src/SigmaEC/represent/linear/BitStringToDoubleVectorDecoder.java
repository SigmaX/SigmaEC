package SigmaEC.represent.linear;

import SigmaEC.represent.Decoder;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.List;

/**
 * Interpret a string of bits as a vector of doubles via little-endian encoding.
 * @author Eric 'Siggy' Scott
 */
public class BitStringToDoubleVectorDecoder extends Decoder<BitStringIndividual, DoubleVectorIndividual> {
    public final static String P_NUM_BITS_PER_DIMENSION = "numBitsPerDimension";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_GRAY_CODE = "grayCode";
    public final static String P_MIN = "min";
    public final static String P_MAX = "max";
        
    final private int numBitsPerDimension;
    final private int numDimensions;
    final private double min;
    final private double max;
    final private boolean gray;

    // <editor-fold defaultstate="collapsed" desc="Accessors">
    public int getNumBitsPerDimension() {
        return numBitsPerDimension;
    }

    public int getNumDimensions() {
        return numDimensions;
    }
    // </editor-fold>
    
    public BitStringToDoubleVectorDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numBitsPerDimension = parameters.getIntParameter(Parameters.push(base, P_NUM_BITS_PER_DIMENSION));
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        gray = parameters.getBooleanParameter(Parameters.push(base, P_GRAY_CODE));
        min = parameters.getDoubleParameter(Parameters.push(base, P_MIN));
        max = parameters.getDoubleParameter(Parameters.push(base, P_MAX));
        
        if (numBitsPerDimension <= 0)
            throw new IllegalArgumentException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_NUM_BITS_PER_DIMENSION));
        if (numDimensions <= 0)
            throw new IllegalArgumentException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_NUM_DIMENSIONS));
        if (min >= max)
            throw new IllegalArgumentException(String.format("%s: %s is <= %s.", this.getClass().getSimpleName(), P_MAX, P_MIN));
        assert(repOK());
    }

    @Override
    public DoubleVectorIndividual decode(final BitStringIndividual individual) {
        assert(individual.size() == numBitsPerDimension*numDimensions);
        final List<BitGene> genome = individual.getGenome();
        final double[] phenotype = new double[numDimensions];
        final double maxPhenotypeValue = Math.pow(2, numBitsPerDimension) - 1.0;
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            final double unScaledValue = gray ? decodeGray(genome, dimension) : decodeEndian(genome, dimension);
            phenotype[dimension] = rescale(unScaledValue, 0, maxPhenotypeValue, min, max);
        }
        return new DoubleVectorIndividual(phenotype);
    }

    private double decodeEndian(final List<BitGene> genome, int dimension) {
        assert(genome != null);
        assert(dimension >= 0);
        assert(genome.size() == numDimensions*numBitsPerDimension);
        double result = 0.0;
        for (int place = 0; place < numBitsPerDimension; place++) {
            final int power = numBitsPerDimension - place - 1; // Little endian
            if (genome.get(dimension*numBitsPerDimension + place).value)
                result += Math.pow(2, power);
        }
        return result;
    }
    
    private double decodeGray(final List<BitGene> genome, final int dimension) {
        assert(genome != null);
        assert(dimension >= 0);
        assert(genome.size() == numDimensions*numBitsPerDimension);
        double value = 0.0;
        boolean lastBit = false;
        for (int place = 0; place < numBitsPerDimension; place++) {
            final boolean g = genome.get(dimension*numBitsPerDimension + place).value;
            boolean b = g ^ lastBit;
            lastBit = g;
            if (b)
                value += Math.pow(2, numBitsPerDimension - place - 1); // Little endian
        }
        return value;
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
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_GRAY_CODE != null
                && !P_GRAY_CODE.isEmpty()
                && P_MAX != null
                && !P_MAX.isEmpty()
                && P_MIN != null
                && !P_MIN.isEmpty()
                && P_NUM_BITS_PER_DIMENSION != null
                && !P_NUM_BITS_PER_DIMENSION.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && numBitsPerDimension > 0
                && numDimensions > 0
                && !Double.isNaN(min)
                && !Double.isInfinite(min)
                && !Double.isNaN(max)
                && !Double.isInfinite(max)
                && min <= max;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitStringToDoubleVectorDecoder))
            return false;
        final BitStringToDoubleVectorDecoder ref = (BitStringToDoubleVectorDecoder) o;
        return numBitsPerDimension == ref.numBitsPerDimension
                && numDimensions == ref.numDimensions
                && gray == ref.gray
                && Misc.doubleEquals(min, ref.min)
                && Misc.doubleEquals(max, ref.max);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.numBitsPerDimension;
        hash = 83 * hash + this.numDimensions;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
        hash = 83 * hash + (this.gray ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%f, %s=%f]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions,
                P_NUM_BITS_PER_DIMENSION, numBitsPerDimension,
                P_GRAY_CODE, gray,
                P_MIN, min,
                P_MAX, max);
    }
    // </editor-fold>
    
}
