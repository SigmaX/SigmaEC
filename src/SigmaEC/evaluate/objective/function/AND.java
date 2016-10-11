package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class AND extends ContractObject implements BooleanFunction {
    public final static String P_ARITY = "arity";
    
    private final int arity;
    
    public AND(final int arity) {
        assert(arity > 0);
        this.arity = arity;
        assert(repOK());
    }
    
    public AND(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        arity = parameters.getOptionalIntParameter(Parameters.push(base, P_ARITY), 2);
        assert(repOK());
    }

    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return 1;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        assert(input.length >= arity);
        boolean result = true;
        for (final boolean b : input)
            result &= b;
        return new boolean[] { result };
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
        if (!(o instanceof AND))
            return false;
        final AND ref = (AND)o;
        return arity == ref.arity;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + this.arity;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_ARITY, arity);
    }
    // </editor-fold>
}
