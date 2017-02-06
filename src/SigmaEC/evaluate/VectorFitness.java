package SigmaEC.evaluate;

import SigmaEC.meta.Fitness;
import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 * A fitness measurement that is expressed as a vector of several values.
 * @author Eric O. Scott
 */
public class VectorFitness extends Fitness {
    private final double[] fitnesses;
    private final String[] dimensionNames;
    private final double asScalar;
    
    public VectorFitness(final double asScalar, final double[] fitnesses, final String[] dimensionNames) {
        assert(!Double.isNaN(asScalar));
        assert(fitnesses != null);
        assert(fitnesses.length > 0);
        assert(!Misc.containsNaNs(fitnesses));
        assert(dimensionNames != null);
        assert(dimensionNames.length == fitnesses.length);
        assert(!Misc.containsNulls(dimensionNames));
        this.asScalar = asScalar;
        this.fitnesses = Arrays.copyOf(fitnesses, fitnesses.length);
        this.dimensionNames = Arrays.copyOf(dimensionNames, dimensionNames.length);
        assert(repOK());
    }
    
    public double[] getFitnesses() {
        return Arrays.copyOf(fitnesses, fitnesses.length);
    }
    
    public double getFitness(final int element) {
        assert(element >= 0);
        assert(element < fitnesses.length);
        return fitnesses[element];
    }
    
    public String[] getDimensionNames() {
        return Arrays.copyOf(dimensionNames, dimensionNames.length);
    }
    
    public String getDimensionName(final int i) {
        assert(i >= 0);
        assert(i < dimensionNames.length);
        return dimensionNames[i];
    }
    
    public int numDimensions() {
        return fitnesses.length;
    }
    
    @Override
    public double asScalar() {
        return asScalar;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return !Double.isNaN(asScalar)
                && fitnesses != null
                && fitnesses.length > 0
                && dimensionNames != null
                && dimensionNames.length == fitnesses.length
                && !Misc.containsNaNs(fitnesses)
                && !Misc.containsNulls(dimensionNames);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VectorFitness))
            return false;
        final VectorFitness ref = (VectorFitness)o;
        return Misc.doubleEquals(asScalar, ref.asScalar)
                && Misc.doubleArrayEquals(fitnesses, ref.fitnesses)
                && Arrays.equals(dimensionNames, ref.dimensionNames);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Arrays.hashCode(this.fitnesses);
        hash = 89 * hash + Arrays.deepHashCode(this.dimensionNames);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.asScalar) ^ (Double.doubleToLongBits(this.asScalar) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%f: [%s]", asScalar, Arrays.toString(fitnesses));
    }
    // </editor-fold>
}
