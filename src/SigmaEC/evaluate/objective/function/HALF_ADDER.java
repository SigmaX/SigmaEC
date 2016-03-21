package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class HALF_ADDER extends ContractObject implements BooleanFunction {

    public HALF_ADDER(final Parameters parameters, final String base) { };

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public int numOutputs() {
        return 2;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        assert(input.length >= arity());
        if (input[0] && input[1])
            return new boolean[] { true, false };
        else if (input[0] || input[1])
            return new boolean[] {false, true};
        else
            return new boolean[] {false, false};
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof HALF_ADDER);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
