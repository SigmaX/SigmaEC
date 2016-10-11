package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class ConcatenatedBooleanFunction extends ContractObject implements BooleanFunction {
    public final static String P_FUNCTIONS = "functions";
    public final static String P_SHARED_INPUTS = "sharedInputs";
    
    private final boolean sharedInputs;
    private final List<BooleanFunction> functions;
    private final int arity;
    private final int numOutputs;
    
    public ConcatenatedBooleanFunction(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        sharedInputs = parameters.getOptionalBooleanParameter(Parameters.push(base, P_SHARED_INPUTS), true);
        functions = parameters.getInstancesFromParameter(Parameters.push(base, P_FUNCTIONS), BooleanFunction.class);
        int arity = 0;
        int numOutputs = 0;
        for (final BooleanFunction f : functions) {
            arity = sharedInputs ? Math.max(arity, f.arity()) : arity + f.arity();
            numOutputs += f.numOutputs();
        }
        this.arity = arity;
        this.numOutputs = numOutputs;
        assert(repOK());
    }
    
    public BooleanFunction getFunction(final int i) {
        assert(i >= 0);
        assert(i < functions.size());
        assert(repOK());
        return functions.get(i);
    }
    
    public int getNumFunctions() {
        return functions.size();
    }
    
    public boolean isSharedInputs() {
        return sharedInputs;
    }
    
    @Override
    public int arity() {
        return arity;
    }

    @Override
    public int numOutputs() {
        return numOutputs;
    }

    @Override
    public boolean[] execute(final boolean[] input) {
        assert(input != null);
        assert(input.length == arity);
        boolean[] output = new boolean[0];
        int funInputStart = 0;
        for (final BooleanFunction f : functions) {
            final int funInputEnd = funInputStart + f.arity();
            assert(funInputEnd <= input.length);
            final boolean[] funInput = Arrays.copyOfRange(input, funInputStart, funInputEnd);
            final boolean[] funOutput = f.execute(funInput);
            output = Misc.prepend(output, funOutput);
            if (!sharedInputs)
                funInputStart += f.arity();
        }
        assert(output.length == numOutputs);
        return output;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_FUNCTIONS != null
                && !P_FUNCTIONS.isEmpty()
                && P_SHARED_INPUTS != null
                && !P_SHARED_INPUTS.isEmpty()
                && functions != null
                && !functions.isEmpty()
                && !Misc.containsNulls(functions)
                && numOutputs == totalOutputs(functions)
                && !(!sharedInputs && (arity != totalArity(functions)))
                && !(sharedInputs && (arity != maxArity(functions)));
    }
    
    private final int totalArity(final List<BooleanFunction> functions) {
        assert(functions != null);
        assert(!Misc.containsNulls(functions));
        int arity = 0;
        for (final BooleanFunction f : functions)
            arity += f.arity();
        return arity;
    }
    
    private final int maxArity(final List<BooleanFunction> functions) {
        assert(functions != null);
        assert(!Misc.containsNulls(functions));
        int arity = 0;
        for (final BooleanFunction f : functions)
            arity = Math.max(arity, f.arity());
        return arity;
    }
    
    private final int totalOutputs(final List<BooleanFunction> functions) {
        assert(functions != null);
        assert(!Misc.containsNulls(functions));
        int numOutputs = 0;
        for (final BooleanFunction f : functions)
            numOutputs += f.numOutputs();
        return numOutputs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConcatenatedBooleanFunction))
            return false;
        final ConcatenatedBooleanFunction ref = (ConcatenatedBooleanFunction)o;
        return arity == ref.arity
                && numOutputs == ref.numOutputs
                && sharedInputs == ref.sharedInputs
                && functions.equals(ref.functions);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.sharedInputs ? 1 : 0);
        hash = 89 * hash + Objects.hashCode(this.functions);
        hash = 89 * hash + this.arity;
        hash = 89 * hash + this.numOutputs;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%s]", this.getClass().getSimpleName(),
                P_SHARED_INPUTS, sharedInputs,
                P_FUNCTIONS, functions);
    }
    // </editor-fold>
}
