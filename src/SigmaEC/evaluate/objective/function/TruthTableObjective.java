package SigmaEC.evaluate.objective.function;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class TruthTableObjective extends ObjectiveFunction<BooleanFunction> {
    public final static String P_TARGET_FUNCTION = "targetFunction";
    
    private final BooleanFunction targetFunction;
    
    public TruthTableObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), BooleanFunction.class);
        assert(repOK());
    }
    
    @Override
    public double fitness(final BooleanFunction ind) {
        assert(ind != null);
        assert(ind.arity() == targetFunction.arity());
        assert(ind.numOutputs() == targetFunction.numOutputs());
        final List<boolean[]> inputs = bitStringPermutations(targetFunction.arity());
        int matches = 0;
        for (final boolean[] input : inputs) {
            if (Arrays.equals(ind.execute(input), targetFunction.execute(input)))
                matches++;
        }
        assert(repOK());
        return (double) matches / inputs.size();
    }
    
    private static List<boolean[]> bitStringPermutations(final int length) {
        assert(length >= 0);
        if (length == 0)
            return new ArrayList<boolean[]>() {{
                    add(new boolean[] {});
            }};
        final List<boolean[]> minusOnePermutations = bitStringPermutations(length - 1);
        return new ArrayList<boolean[]>() {{
                for (final boolean[] bitString : minusOnePermutations)
                    add(Misc.prepend(false, bitString));
                for (final boolean[] bitString : minusOnePermutations)
                    add(Misc.prepend(true, bitString));
        }};
    }

    @Override
    public void setStep(final int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() {
        return 0;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_TARGET_FUNCTION != null
                && !P_TARGET_FUNCTION.isEmpty()
                && targetFunction != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TruthTableObjective))
            return false;
        final TruthTableObjective ref = (TruthTableObjective)o;
        return targetFunction.equals(ref.targetFunction);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.targetFunction);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_TARGET_FUNCTION, targetFunction);
    }
    // </editor-fold>
}
