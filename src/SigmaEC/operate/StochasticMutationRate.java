package SigmaEC.operate;

import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.dist.Distribution;
import java.util.Objects;

/**
 *
 * See Benjamin Doerr et al., "Fast Genetic Algorithms," https://arxiv.org/abs/1703.03334
 * 
 * @author Eric O. Scott
 */
public class StochasticMutationRate extends MutationRate {
    public final static String P_DISTRIBUTION = "distribution";
    
    private final Distribution distribution;
    
    public StochasticMutationRate(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        distribution = parameters.getInstanceFromParameter(Parameters.push(base, P_DISTRIBUTION), Distribution.class);
        assert(repOK());
    }
    
    @Override
    public double getRateForGene(final int gene, final int step, final LinearGenomeIndividual ind) {
        return distribution.sample();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DISTRIBUTION != null
                && !P_DISTRIBUTION.isEmpty()
                && distribution != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof StochasticMutationRate))
            return false;
        final StochasticMutationRate ref = (StochasticMutationRate)o;
        return distribution.equals(ref.distribution);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.distribution);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_DISTRIBUTION, distribution);
    }
    // </editor-fold>
}
