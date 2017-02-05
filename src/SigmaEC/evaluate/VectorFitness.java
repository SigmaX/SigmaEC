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
    private final double asScalar;
    
    public VectorFitness(final double asScalar, final double[] fitnesses) {
        assert(!Double.isNaN(asScalar));
        assert(fitnesses != null);
        assert(!Misc.containsNaNs(fitnesses));
        this.asScalar = asScalar;
        this.fitnesses = Arrays.copyOf(fitnesses, fitnesses.length);
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
    
    @Override
    public double asScalar() {
        return asScalar;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return !Double.isNaN(asScalar)
                && fitnesses != null
                && !Misc.containsNaNs(fitnesses);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof VectorFitness))
            return false;
        final VectorFitness ref = (VectorFitness)o;
        return Misc.doubleEquals(asScalar, ref.asScalar)
                && Misc.doubleArrayEquals(fitnesses, ref.fitnesses);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Arrays.hashCode(this.fitnesses);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.asScalar) ^ (Double.doubleToLongBits(this.asScalar) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%f: [%s]", asScalar, Arrays.toString(fitnesses));
    }
    // </editor-fold>
}
