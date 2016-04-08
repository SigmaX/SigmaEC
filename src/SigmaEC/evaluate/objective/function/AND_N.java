package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class AND_N extends ContractObject implements BooleanFunction {
        
    public AND_N(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public int numOutputs() {
        return 1;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        assert(input.length >= 2);
        return new boolean[] { (input[0] && !input[1]) };
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof AND_N);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
