package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SchwefelObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_NUM_DIMENSIONS = "numDimensions";

    private final int numDimensions;
    
    public SchwefelObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        double sum = 418.9829*numDimensions;
        for (final double x : ind.getGenomeArray())
            sum -= x * Math.sin(Math.sqrt(Math.abs(x)));
        return sum;
    }

    @Override
    public void setStep(final int i) {
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
