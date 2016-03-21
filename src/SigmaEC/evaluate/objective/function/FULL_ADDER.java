package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Parameters;

/**
 *
 * @author eric
 */
public class FULL_ADDER extends ContractObject implements BooleanFunction {

    public FULL_ADDER(final Parameters parameters, final String base) { };

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public int numOutputs() {
        return 2;
    }

    @Override
    public boolean[] execute(boolean[] input) {
        assert(input != null);
        assert(input.length >= arity());
        int sum = 0;
        sum += (input[0] ? 1 : 0);
        sum += (input[1] ? 1 : 0);
        sum += (input[2] ? 1 : 0);
        switch (sum)
        {
            default:
            case 0:
                return new boolean[] {false, false};
            case 1:
                return new boolean[] {false, true};
            case 2:
                return new boolean[] {true, false};
            case 3:
                return new boolean[] {true, true};
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof FULL_ADDER);
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
