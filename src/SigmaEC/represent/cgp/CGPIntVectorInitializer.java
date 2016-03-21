package SigmaEC.represent.cgp;

import SigmaEC.SRandom;
import SigmaEC.represent.Initializer;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.represent.linear.IntVectorInitializer;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class CGPIntVectorInitializer extends Initializer<IntVectorIndividual> {
    public final static String P_POPULATION_SIZE = "populationSize";
    public final static String P_RANDOM = "random";
    public final static String P_CGP_PARAMETERS = "cgpParameters";
    
    private final CGPParameters cgpParameters;
    private final IntVectorInitializer wrappedInitializer;
    
    public CGPIntVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        cgpParameters = parameters.getInstanceFromParameter(Parameters.push(base, P_CGP_PARAMETERS), CGPParameters.class);
        final Random random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        final int populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        wrappedInitializer = new IntVectorInitializer(populationSize,
                cgpParameters.getNumDimensions(),
                cgpParameters.getMinBounds(),
                cgpParameters.getMaxBounds(),
                random,
                new Option(new CartesianIntVectorConstraint(cgpParameters)),
                true);
        assert(repOK());
    }

    @Override
    public List<IntVectorIndividual> generatePopulation() {
        assert(repOK());
        return  wrappedInitializer.generatePopulation();
    }

    @Override
    public IntVectorIndividual generateIndividual() {
        assert(repOK());
        return wrappedInitializer.generateIndividual();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_CGP_PARAMETERS != null
                && !P_CGP_PARAMETERS.isEmpty()
                && P_POPULATION_SIZE != null
                && !P_POPULATION_SIZE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && cgpParameters != null
                && wrappedInitializer != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CGPIntVectorInitializer))
            return false;
        final CGPIntVectorInitializer ref = (CGPIntVectorInitializer)o;
        return cgpParameters.equals(ref.cgpParameters)
                && wrappedInitializer.equals(wrappedInitializer);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.cgpParameters);
        hash = 67 * hash + Objects.hashCode(this.wrappedInitializer);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, wrappedInitializer=%s]", this.getClass().getSimpleName(),
                P_CGP_PARAMETERS, cgpParameters,
                wrappedInitializer);
    }
    // </editor-fold>
}
