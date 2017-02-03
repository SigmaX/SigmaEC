package SigmaEC.represent.cgp;

import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 * Verify whether an integer vector is a valid representation of a CGP individual.
 * 
 * @author Eric O. Scott
 */
public class CartesianIntVectorConstraint extends Constraint<IntVectorIndividual> {
    public final static String P_CGP_PARMAETERS = "cgpParameters";
    
    private final CGPParameters cgpParameters;
    
    public CartesianIntVectorConstraint(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        cgpParameters = parameters.getInstanceFromParameter(Parameters.push(base, P_CGP_PARMAETERS), CGPParameters.class);
        assert(repOK());
    }
    
    public CartesianIntVectorConstraint(final CGPParameters cgpParameters) {
        assert(cgpParameters != null);
        this.cgpParameters = cgpParameters;
        assert(repOK());
    }

    @Override
    public boolean isViolated(final IntVectorIndividual individual) {
        assert(individual != null);
        if (individual.size() != cgpParameters.getNumDimensions())
            return true;
        for (int layer = 0; layer < cgpParameters.numLayers(); layer++) {
            for (int node = 0; node < cgpParameters.numNodesPerLayer(); node++) {
                final int functionID = individual.getElement(cgpParameters.getFunctionGeneForNode(layer, node));
                if (functionID < 0 || functionID >= cgpParameters.numPrimitives())
                    return true;
                
                for (int i = 0; i < cgpParameters.maxArity(); i++) {
                    final int source = individual.getElement(cgpParameters.getInputGeneForNode(layer, node, i));
                    if (source >= cgpParameters.numInputs() + layer*cgpParameters.numNodesPerLayer())
                        return true;
                    if (layer >= cgpParameters.levelsBack() 
                            && (source < cgpParameters.numInputs() + (layer - cgpParameters.levelsBack())*cgpParameters.numNodesPerLayer()))
                        return true;
                    if (layer < cgpParameters.levelsBack() && source < 0)
                        return true;
                }
            }
        }
        for (int i = cgpParameters.numLayers()*cgpParameters.numNodesPerLayer()*(cgpParameters.maxArity() + 1); i < individual.size(); i++) {
            final int outputSource = individual.getElement(i);
            if (outputSource < 0 || outputSource > cgpParameters.numInputs() + cgpParameters.numLayers()*cgpParameters.numNodesPerLayer())
                return true;
        }
        return false;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_CGP_PARMAETERS != null
                && !P_CGP_PARMAETERS.isEmpty()
                && cgpParameters != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CartesianIntVectorConstraint))
            return false;
        final CartesianIntVectorConstraint ref = (CartesianIntVectorConstraint)o;
        return cgpParameters.equals(ref.cgpParameters);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.cgpParameters);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_CGP_PARMAETERS, cgpParameters);
    }
    // </editor-fold>
}
