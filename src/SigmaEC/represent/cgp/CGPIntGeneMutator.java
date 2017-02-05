package SigmaEC.represent.cgp;

import SigmaEC.SRandom;
import SigmaEC.operate.IntGeneMutator;
import SigmaEC.operate.MutationRate;
import SigmaEC.operate.Mutator;
import SigmaEC.represent.linear.IntGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class CGPIntGeneMutator extends Mutator<LinearGenomeIndividual<IntGene>> {
    public final static String P_RANDOM = "random";
    public final static String P_CGP_PARAMETERS = "cgpParameters";
    public final static String P_MUTATION_RATE = "mutationRate";
    
    private final CGPParameters cgpParameters;
    private final IntGeneMutator wrappedMutator;
    
    public CGPIntGeneMutator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        cgpParameters = parameters.getInstanceFromParameter(Parameters.push(base, P_CGP_PARAMETERS), CGPParameters.class);
        final Random random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        final MutationRate mutationRate = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATION_RATE), MutationRate.class);
        wrappedMutator = new IntGeneMutator(mutationRate, random, cgpParameters.getMinBounds(), cgpParameters.getMaxBounds(), new Option(new CartesianIntVectorConstraint(cgpParameters)), true);
        assert(repOK());
    }
    
    @Override
    public LinearGenomeIndividual<IntGene> mutate(final LinearGenomeIndividual<IntGene> ind, final int step) {
        assert(ind != null);
        return wrappedMutator.mutate(ind, step);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_CGP_PARAMETERS != null
                && !P_CGP_PARAMETERS.isEmpty()
                && P_MUTATION_RATE != null
                && !P_MUTATION_RATE.isEmpty()
                && cgpParameters != null
                && wrappedMutator != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CGPIntGeneMutator))
            return false;
        final CGPIntGeneMutator ref = (CGPIntGeneMutator)o;
        return cgpParameters.equals(ref.cgpParameters)
                && wrappedMutator.equals(ref.wrappedMutator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.cgpParameters);
        hash = 79 * hash + Objects.hashCode(this.wrappedMutator);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, wrappedMutator=%s]", this.getClass().getSimpleName(),
                P_CGP_PARAMETERS, cgpParameters,
                wrappedMutator);
    }
    // </editor-fold>
}
