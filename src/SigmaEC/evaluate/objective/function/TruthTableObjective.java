package SigmaEC.evaluate.objective.function;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An objective function that compares a Boolean function to some target function
 * by counting the number of truth table entries that agree.
 * 
 * @author Eric O. Scott
 */
public class TruthTableObjective extends ObjectiveFunction<BooleanFunction> {
    public final static String P_TARGET_FUNCTION = "targetFunction";
    public final static String P_START_INPUT = "startInput";
    public final static String P_START_OUTPUT = "startOutput";
    public final static String P_PARTIAL_MATCHES = "partialMatches";
    
    private final BooleanFunction targetFunction;
    private final int startInput;
    private final int startOutput;
    private final boolean partialMatches;
    
    public TruthTableObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), BooleanFunction.class);
        startInput = parameters.getOptionalIntParameter(Parameters.push(base, P_START_INPUT), 0);
        startOutput = parameters.getOptionalIntParameter(Parameters.push(base, P_START_OUTPUT), 0);
        partialMatches = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PARTIAL_MATCHES), true);
        assert(repOK());
    }
    
    public TruthTableObjective(final BooleanFunction targetFunction) {
        assert(targetFunction != null);
        this.targetFunction = targetFunction;
        startInput = 0;
        startOutput = 0;
        partialMatches = true;
        assert(repOK());
    }
    
    @Override
    public double fitness(final BooleanFunction ind) {
        assert(ind != null);
        assert(startInput < ind.arity());
        assert(ind.arity() - startInput >= targetFunction.arity());
        final List<boolean[]> inputs = bitStringPermutations(ind.arity());
        double matches = 0;
        for (final boolean[] wholeInput : inputs) {
            // Calculuate the phenotype of the individual
            final int minNumOutputs = Math.min(ind.numOutputs(), targetFunction.numOutputs());
            final boolean[] indResult = Arrays.copyOfRange(ind.execute(wholeInput), startOutput, startOutput + minNumOutputs); // Ignore the first startOutput output bits
            
            // Calculate the goal phenotype
            final boolean[] input = Arrays.copyOfRange(wholeInput, startInput, wholeInput.length); // Ignore the first startInput input bits
            final boolean[] targetResult = Arrays.copyOf(targetFunction.execute(input), minNumOutputs);
            
            if (partialMatches)
                matches += (double) countMatchingElements(indResult, targetResult)/minNumOutputs;
            else if (Arrays.equals(indResult, targetResult))
                matches++;
        }
        assert(repOK());
        return (double) matches / inputs.size();
    }
    
    public static int countMatchingElements(final boolean[] a, final boolean[] b) {
        assert(a != null);
        assert(b != null);
        assert(a.length == b.length);
        int matches = 0;
        for (int i = 0; i < a.length; i++)
            matches += (a[i] == b[i]) ? 1 : 0;
        return matches;
    }
    
    public static List<boolean[]> bitStringPermutations(final int length) {
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
                && P_START_INPUT != null
                && !P_START_INPUT.isEmpty()
                && P_START_OUTPUT != null
                && !P_START_OUTPUT.isEmpty()
                && P_PARTIAL_MATCHES != null
                && !P_PARTIAL_MATCHES.isEmpty()
                && targetFunction != null
                && startInput >= 0
                && startOutput >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TruthTableObjective))
            return false;
        final TruthTableObjective ref = (TruthTableObjective)o;
        return startInput == ref.startInput
                && startOutput == ref.startOutput
                && targetFunction.equals(ref.targetFunction);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.targetFunction);
        hash = 11 * hash + this.startInput;
        hash = 11 * hash + this.startOutput;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%B]", this.getClass().getSimpleName(),
                P_START_INPUT, startInput,
                P_START_OUTPUT, startOutput,
                P_TARGET_FUNCTION, targetFunction,
                P_PARTIAL_MATCHES, partialMatches);
    }
    // </editor-fold>
}
