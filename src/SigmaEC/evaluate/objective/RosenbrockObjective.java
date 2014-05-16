package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Parameters;

/**
 * An n-dimensional generalization of Rosenbrock's objective function:
 * f(x) = \sum_i^{n-1} 100(x_i^2 - x_{i+1})^2 + (1 - x_i)^2.  Traditionally,
 * each variable is bound between -2.048 and 2.048 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class RosenbrockObjective extends ObjectiveFunction<DoubleVectorPhenotype>
{
    private final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public RosenbrockObjective(final Parameters parameters, final String base) {
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
    public double fitness(final DoubleVectorPhenotype ind)
    {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for(int i = 0; i < ind.size() - 1; i++)
            sum+= 100*Math.pow((ind.getElement(i+1) - Math.pow(ind.getElement(i), 2)), 2) + Math.pow((1 - ind.getElement(i)), 2);
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
        return String.format("[%s: numDimensionts=%d]", this.getClass().getSimpleName(), numDimensions);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof RosenbrockObjective))
            return false;
        
        final RosenbrockObjective cRef = (RosenbrockObjective) o;
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
