package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * The Rastrigin function, a.k.a. De Jong's f_6.  An evenly spaced grid of
 * optima.  The Rastrigin function is linearly separable.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RastriginObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public RastriginObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        
        assert(repOK());
    }

    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == numDimensions);
        double sum = 10*ind.size();
        for (final double x : ind.getGenomeArray())
            sum += x*x - 10*Math.cos(2*Math.PI*x);
        return sum;
    }

    @Override
    public void setStep(int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numDimensions > 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RastriginObjective))
            return false;
        final RastriginObjective ref = (RastriginObjective) o;
        return numDimensions == ref.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.numDimensions;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d]", this.getClass().getSimpleName(), numDimensions);
    }
    // </editor-fold>
}
