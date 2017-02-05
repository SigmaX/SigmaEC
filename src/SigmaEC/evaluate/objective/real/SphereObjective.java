package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * The so-called "sphere function," f(x) = \sum_i x_i^2.  This is actually a
 * paraboloid, but De Jong (1975) introduced it originally as a function over
 * three variables, which has "spherical constant-cost contours." 
 * Traditionally, each variable is bounded between -5.12 and 5.12 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class SphereObjective extends ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public SphereObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(String.format("%s: %s is < 1.", this.getClass().getSimpleName(), P_NUM_DIMENSIONS));
        assert(repOK());
    }
    
    public SphereObjective(final int numDimensions) {
        if (numDimensions <= 0)
            throw new IllegalArgumentException(String.format("%s: %s is negative, must be positive", this.getClass().getSimpleName(), P_NUM_DIMENSIONS));
        this.numDimensions = numDimensions;
        assert(repOK());
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    @Override
    public ScalarFitness fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for (double d : ind.getGenomeArray())
            sum+= Math.pow(d,2);
        assert(repOK());
        return new ScalarFitness(sum);
    }

    @Override
    public void setStep(final int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && numDimensions > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(), P_NUM_DIMENSIONS, numDimensions);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof SphereObjective))
            return false;
        
        final SphereObjective ref = (SphereObjective) o;
        return numDimensions == ref.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        return hash;
    }
    //</editor-fold>
}
