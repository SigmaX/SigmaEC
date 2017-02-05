package SigmaEC.evaluate.objective.discrete;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.BitGene;
import SigmaEC.represent.linear.BitStringIndividual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class OneMaxObjective extends ObjectiveFunction<BitStringIndividual<ScalarFitness>, ScalarFitness> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    
    private final int numDimensions;
    
    public OneMaxObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        assert(repOK());
    }
    
    @Override
    public ScalarFitness fitness(final BitStringIndividual<ScalarFitness> ind) {
        assert(ind != null);
        assert(ind.size() == numDimensions);
        int fitness = 0;
        for (final BitGene b : ind.getGenome())
            if (b.value)
                fitness++;
        return new ScalarFitness(fitness);
    }

    @Override
    public void setStep(int i) {
        /* Do nothing. */
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && numDimensions > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OneMaxObjective))
            return false;
        return numDimensions == ((OneMaxObjective)o).numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.numDimensions;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions);
    }
    // </editor-fold>
}
