package SigmaEC.evaluate.objective;

import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * A piecewise continuous step function from De Jong's original suite.  Sums the
 * floor of a vector's elements.  Traditionally, each variable is bounded
 * between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class StepObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public StepObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        int sum = 0;
        for(double d : ind.getGenomeArray())
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
