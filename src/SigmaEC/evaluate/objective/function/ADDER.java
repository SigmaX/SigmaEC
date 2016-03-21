package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class ADDER extends ContractObject implements BooleanFunction {
    public final static String P_ARITY = "arity";
    
    private final int arity;
    private final FULL_ADDER fullAdder;
    
    public ADDER(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        arity = parameters.getOptionalIntParameter(Parameters.push(base, P_ARITY), 4);
        fullAdder = new FULL_ADDER(parameters, base);
        assert(repOK());
    }
    
    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return arity()/2 + 1;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        boolean[] output = new boolean[numOutputs()];
        boolean carry = false;
        for (int i = output.length - 2; i >= 0; i--)
        {
            boolean[] sum = fullAdder.execute(new boolean[] { carry, input[i], input[arity()/2 + i] });
            output[i+1] = sum[1];
            carry = sum[0];
        }
        output[0] = carry;
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
        if (!(o instanceof ADDER))
            return false;
        return arity == ((ADDER)o).arity;
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
