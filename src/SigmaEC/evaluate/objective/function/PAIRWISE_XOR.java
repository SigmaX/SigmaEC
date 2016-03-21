package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class PAIRWISE_XOR extends ContractObject implements BooleanFunction {
    public final static String P_ARITY = "arity";
    
    private final int arity;
    
    public PAIRWISE_XOR(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        arity = parameters.getOptionalIntParameter(Parameters.push(base, P_ARITY), 4);
        if (!(arity % 2 == 0))
            throw new IllegalStateException(String.format("%s: parameter '%s' must be even.", this.getClass().getSimpleName(), Parameters.push(base, P_ARITY)));
        assert(repOK());
    }
    
    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return arity()/2;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        assert(input.length >= arity);
        boolean[] output = new boolean[numOutputs()];
        for (int i = 0; i < numOutputs(); i++)
            output[i] = input[2*i] ^ input[2*i + 1];
        return output;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_ARITY != null
                && !P_ARITY.isEmpty()
                && arity >= 3;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PAIRWISE_XOR))
            return false;
        return arity == ((PAIRWISE_XOR)o).arity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.arity;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_ARITY, arity);
    }
    // </editor-fold>
    
}
