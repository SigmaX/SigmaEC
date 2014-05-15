package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SchwefelObjective extends ObjectiveFunction<DoubleVectorPhenotype> {

    private final int numDimensions;
    
    public SchwefelObjective(final int numDimensions) {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        this.numDimensions = numDimensions;
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorPhenotype ind) {
        assert(ind.size() == numDimensions);
        double sum = 418.9829*numDimensions;
        for (final double x : ind.getVector())
            sum -= x * Math.sin(Math.sqrt(Math.abs(x)));
        return sum;
    }

    @Override
    public void setGeneration(final int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() { return numDimensions; }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numDimensions > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d]", this.getClass().getSimpleName(), numDimensions);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SchwefelObjective))
            return false;
        
        final SchwefelObjective cRef = (SchwefelObjective) o;
        return numDimensions == cRef.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.numDimensions;
        return hash;
    }
    // </editor-fold>
}
