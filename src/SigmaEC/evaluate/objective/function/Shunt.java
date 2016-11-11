package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 * Eric 'Siggy' Scott */
public class Shunt extends ContractObject implements BooleanFunction  {
    public final static String P_ARITY = "arity";
    
    private final int arity;
    
    public Shunt(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        arity = parameters.getOptionalIntParameter(Parameters.push(base, P_ARITY), 1);
        assert(repOK());
    }
    
    @Override public boolean[] execute(boolean[] inputs) {
        return Arrays.copyOf(inputs, inputs.length);
    }

    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return arity;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_ARITY != null
                && !P_ARITY.isEmpty()
                && arity > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Shunt))
            return false;
        final Shunt ref = (Shunt)o;
        return arity == ref.arity;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.arity;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_ARITY, arity);
    }
    // </editor-fold>
}
