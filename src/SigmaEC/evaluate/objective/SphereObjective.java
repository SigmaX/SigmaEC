package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * The so-called "sphere function," f(x) = \sum_i x_i^2.  This is actually a
 * paraboloid, but De Jong (1975) introduced it originally as a function over
 * three variables, which has "spherical constant-cost contours." 
 * Traditionally, each variable is bounded between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class SphereObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public SphereObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        assert(repOK());
    }
    
    public SphereObjective(final int numDimensions) {
        if (numDimensions <= 0)
            throw new IllegalArgumentException(String.format("%s: numDimensions is negative, must be positive", this.getClass().getSimpleName()));
        this.numDimensions = numDimensions;
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for (double d : ind.getGenomeArray())
            sum+= Math.pow(d,2);
        assert(repOK());
        return sum;
    }

    @Override
    public void setGeneration(final int i) {
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
        if (!(o instanceof SphereObjective))
            return false;
        
        final SphereObjective cRef = (SphereObjective) o;
        return numDimensions == cRef.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
