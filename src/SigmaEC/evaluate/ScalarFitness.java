package SigmaEC.evaluate;

import SigmaEC.meta.Fitness;
import SigmaEC.util.Misc;

/**
 *
 * @author Eric O Scott
 */
public class ScalarFitness extends Fitness {
    private final double fitness;
    
    public ScalarFitness(final double fitness) {
        assert(Double.isFinite(fitness));
        this.fitness = fitness;
        assert(repOK());
    }
    
    @Override
    public double asScalar() {
        return fitness;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return Double.isFinite(fitness);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ScalarFitness))
            return false;
        final ScalarFitness ref = (ScalarFitness)o;
        return Misc.doubleEquals(fitness, ref.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.fitness) ^ (Double.doubleToLongBits(this.fitness) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%f", fitness);
    }
    // </editor-fold>
}
