package SigmaEC.represent.distance;

import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * Computes the Euclidean distance between two real-vector genomes.
 * 
 * @author Eric O. Scott
 */
public class DoubleVectorEuclideanDistanceMeasure extends DistanceMeasure<DoubleVectorIndividual> {

    public DoubleVectorEuclideanDistanceMeasure(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    @Override
    public double distance(final DoubleVectorIndividual a, final DoubleVectorIndividual b) {
        assert(a != null);
        assert(b != null);
        if (a.size() != b.size())
            throw new IllegalArgumentException("Attempted to compute distance between two vectors of differing dimensionality.");
        double sumSquares = 0;
        for (int i = 0; i < a.size(); i++)
            sumSquares += Math.pow(a.getElement(i) - b.getElement(i), 2);
        assert(repOK());
        return Math.sqrt(sumSquares);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof DoubleVectorEuclideanDistanceMeasure);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
