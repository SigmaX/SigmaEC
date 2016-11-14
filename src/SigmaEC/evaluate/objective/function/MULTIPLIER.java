package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 *
 * @author Eric O. Scott
 */
public class MULTIPLIER extends ContractObject implements BooleanFunction {
    public final static String P_ARITY = "arity";
    
    private final int arity;
    
    public MULTIPLIER(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        arity = parameters.getOptionalIntParameter(Parameters.push(base, P_ARITY), 4);
        assert(repOK());
    }
    
    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return (arity % 2 == 0) ? arity : arity + 1;
    }

    @Override
    public boolean[] execute(final boolean[] input) {
        assert(input != null);
        assert(input.length == arity);
        final boolean[] firstParam = Arrays.copyOfRange(input, 0, (int) Math.ceil(arity/2.0));
        final boolean[] secondParam = Arrays.copyOfRange(input, (int) Math.ceil(arity/2.0), arity);
        assert(firstParam.length == (int) Math.ceil(arity/2.0));
        assert(!((arity % 2 == 0) && (firstParam.length != secondParam.length))); // If arity is even, both parameters should be the same length
        assert(!((arity % 2 != 0) && (secondParam.length != firstParam.length - 1))); // If arity is odd, the second parameter should be one bit shorter
        
        final int resultInt = Misc.bitStringToInt(firstParam) * Misc.bitStringToInt(secondParam);        
        final boolean[] result = new boolean[numOutputs()];
        int remaining = resultInt;
        for (int i = 0; i < result.length; i++) {
            final int bitVal = (int) Math.pow(2, result.length - 1 - i);
            if (remaining >= bitVal) {
                result[i] = true;
                remaining -= bitVal;
            }
        }
        assert(resultInt == Misc.bitStringToInt(result));
        assert(repOK());
        return result;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_ARITY != null
                && !P_ARITY.isEmpty()
                && arity > 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MULTIPLIER))
            return false;
        final MULTIPLIER ref = (MULTIPLIER)o;
        return arity == ref.arity;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.arity;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_ARITY, arity);
    }
    // </editor-fold>
}
