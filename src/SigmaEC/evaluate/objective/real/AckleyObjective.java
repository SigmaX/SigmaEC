package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleGene;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class AckleyObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public AckleyObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        assert(repOK());
        
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        assert(ind != null);
        assert(ind.size() == numDimensions);
        double sumA = 0;
        double sumB = 0;
        for (final DoubleGene x : ind.getGenome()) {
            sumA += Math.pow(x.value, 2);
            sumB += Math.cos(2.0*Math.PI*x.value);
        }
        assert(repOK());
        return -20*Math.exp(-0.2*Math.sqrt(sumA/ind.size())) - Math.exp(sumB/ind.size()) + 20 + Math.E;
    }

    @Override
    public void setGeneration(int i) { /** Do nothing */ }

    @Override
    public int getNumDimensions() { return numDimensions; }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numDimensions > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AckleyObjective))
            return false;
        final AckleyObjective ref = (AckleyObjective)o;
        return numDimensions == ref.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.numDimensions;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions);
    }
    // </editor-fold>
}
