package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;

/**
 * A piecewise continuous step function from De Jong's original suite.  Sums the
 * floor of a vector's elements.  Traditionally, each variable is bounded
 * between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class StepObjective extends ObjectiveFunction<DoubleVectorPhenotype>
{
    private final int numDimensions;
    
    public StepObjective(int numDimensions) {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        this.numDimensions = numDimensions;
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    
    @Override
    public double fitness(final DoubleVectorPhenotype ind) {
        assert(ind.size() == numDimensions);
        int sum = 0;
        for(double d : ind.getVector())
            sum += (int) d;
        assert(repOK());
        return sum;
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numDimensions > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: NumDimensions=%d]", this.getClass().getSimpleName(), numDimensions);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof StepObjective))
            return false;
        
        final StepObjective cRef = (StepObjective) o;
        return numDimensions == cRef.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>

}
