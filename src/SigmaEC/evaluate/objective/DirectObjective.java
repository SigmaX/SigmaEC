package SigmaEC.evaluate.objective;

import SigmaEC.util.Parameters;

/**
 * An objective for use when the phenotype is the fitness.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DirectObjective extends ObjectiveFunction<Double> {
    
    public DirectObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public double fitness(final Double ind) {
        return ind;
    }

    @Override
    public void setGeneration(int i) { }

    @Override
    public int getNumDimensions() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ": dimensions not defined for this objective.");
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof DirectObjective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s:]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
