package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Random;

/**
 * The function \sum_i^n{i&x_i^4 + G(0,1)}, where G(0,1) is Gaussian noise with 
 * mean 0 and std 1. From the De Jong suite.  Traditionally, each variable is
 * bounded between -1.28 and 1.28 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class NoiseyQuarticObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_NUM_DIMENSIONS = "numDimensions";
    private final static String P_RANDOM = "random";
    
    private final Random random;
    private final int numDimensions;

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }
    
    public NoiseyQuarticObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for (int i = 0; i < ind.size(); i++)
            sum += i*Math.pow(ind.getElement(i), 4) + 500.0*random.nextGaussian();
        assert(repOK());
        return sum;
    }

    @Override
    public void setStep(final int i) {
        // Do nothing
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return random != null
                && numDimensions > 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: numDimensions=%d, random=%s]", this.getClass().getSimpleName(), numDimensions, random);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NoiseyQuarticObjective))
            return false;
        
        final NoiseyQuarticObjective cRef = (NoiseyQuarticObjective) o;
        return numDimensions == cRef.numDimensions
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 43 * hash + this.numDimensions;
        return hash;
    }
    // </editor-fold>
}
