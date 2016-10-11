package SigmaEC.evaluate.objective.function;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.ConcatenatedBooleanFunction;
import SigmaEC.evaluate.objective.function.TruthTableObjective;
import SigmaEC.util.Parameters;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A special objective that helps us compare part of an individual's phenotype to
 * a particular sub-function in a concatenated objective.
 * 
 * This is less prone to error than using TruthTableObjective's "startInput" and "startOutput" parameters.
 * 
 * @author Eric O. Scott
 */
public class ConcatenatedTruthTableObjective extends ObjectiveFunction<BooleanFunction> {
    public final static String P_TARGET_FUNCTION = "targetFunction";
    public final static String P_SUBFUNCTION = "subFunction";
    public final static String P_PARTIAL_MATCHES = "partialMatches";
    
    private final ConcatenatedBooleanFunction targetFunction;
    private final BooleanFunction subFunction;
    private final int subFunctionID;
    private final int subFunctionOutputOffset;
    private final int subFunctionInputOffset;
    private final boolean partialMatches;
    
    public ConcatenatedTruthTableObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), BooleanFunction.class);
        subFunctionID = parameters.getOptionalIntParameter(Parameters.push(base, P_SUBFUNCTION), 0);
        if (subFunctionID < 0 || subFunctionID >= targetFunction.getNumFunctions())
            throw new IllegalStateException(String.format("%s: parameter '%s' must be non-negative and less than the number of functions (viz., %d).", this.getClass().getSimpleName(), Parameters.push(base, P_SUBFUNCTION), targetFunction.getNumFunctions()));
        subFunction = targetFunction.getFunction(subFunctionID);
        subFunctionOutputOffset = getOutputOffset(targetFunction, subFunctionID);
        subFunctionInputOffset = getInputOffset(targetFunction, subFunctionID);
        partialMatches = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PARTIAL_MATCHES), true);
        assert(repOK());
    }
    
    public ConcatenatedTruthTableObjective(final ConcatenatedBooleanFunction targetFunction, final int subFunctionID, final boolean partialMatches) {
        assert(targetFunction != null);
        assert(subFunctionID >= 0);
        assert(subFunctionID < targetFunction.getNumFunctions());
        this.targetFunction = targetFunction;
        this.subFunctionID = subFunctionID;
        this.partialMatches = partialMatches;
        subFunction = targetFunction.getFunction(subFunctionID);
        subFunctionOutputOffset = getOutputOffset(targetFunction, subFunctionID);
        subFunctionInputOffset = getInputOffset(targetFunction, subFunctionID);
        assert(repOK());
    }
    
    private static int getOutputOffset(final ConcatenatedBooleanFunction targetFunction, final int subFunctionID) {
        assert(targetFunction != null);
        assert(subFunctionID >= 0);
        assert(subFunctionID < targetFunction.getNumFunctions());
        int subFunctionOutputOffset = 0;
        for (int i = 0; i < subFunctionID; i++)
            subFunctionOutputOffset += targetFunction.getFunction(i).numOutputs();
        return subFunctionOutputOffset;
    }
    
    private static int getInputOffset(final ConcatenatedBooleanFunction targetFunction, final int subFunctionID) {
        assert(targetFunction != null);
        assert(subFunctionID >= 0);
        assert(subFunctionID < targetFunction.getNumFunctions());
        if (targetFunction.isSharedInputs())
            return 0;
        int subFunctionInputOffset = 0;
        for (int i = 0; i < subFunctionID; i++)
            subFunctionInputOffset += targetFunction.getFunction(i).arity();
        return subFunctionInputOffset;
    }
    
    @Override
    public double fitness(final BooleanFunction ind) {
        assert(ind != null);
        assert(ind.numOutputs() == targetFunction.numOutputs());
        final List<boolean[]> inputs = TruthTableObjective.bitStringPermutations(ind.arity());
        double matches = 0;
        for (final boolean[] input : inputs) {
            // Execute the individual on all of the input variables, and only look at the outputs that correspond to the subFunction we care about.
            final boolean[] indResult = Arrays.copyOfRange(ind.execute(input), subFunctionOutputOffset, subFunctionOutputOffset + subFunction.numOutputs());
            
            // Now look specifically at the inputs that are supposed to control the outputs for the subFunction.
            final boolean[] subFunInput = Arrays.copyOfRange(input, subFunctionInputOffset, subFunctionInputOffset + subFunction.arity());
            final boolean[] subFunResult = subFunction.execute(subFunInput); // See what outputs they are supposed to produce.
            
            if (partialMatches)
                matches += (double) TruthTableObjective.countMatchingElements(indResult, subFunResult)/subFunction.numOutputs();
            else if (Arrays.equals(indResult, subFunResult))
                matches++;
        }
        assert(repOK());
        return (double) matches / inputs.size();
    }

    @Override
    public void setStep(int i) {
        // do nothing
    }

    @Override
    public int getNumDimensions() {
        return 0;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_PARTIAL_MATCHES != null
                && !P_PARTIAL_MATCHES.isEmpty()
                && P_SUBFUNCTION != null
                && !P_SUBFUNCTION.isEmpty()
                && P_TARGET_FUNCTION != null
                && !P_TARGET_FUNCTION.isEmpty()
                && targetFunction != null
                && subFunction != null
                && subFunctionID >= 0
                && subFunctionID < targetFunction.getNumFunctions()
                && subFunctionOutputOffset >= 0
                && subFunctionOutputOffset < targetFunction.numOutputs()
                && subFunctionOutputOffset == getOutputOffset(targetFunction, subFunctionID)
                && subFunctionInputOffset >= 0
                && subFunctionInputOffset < targetFunction.arity()
                && subFunctionInputOffset == getInputOffset(targetFunction, subFunctionID);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConcatenatedTruthTableObjective))
            return false;
        final ConcatenatedTruthTableObjective ref = (ConcatenatedTruthTableObjective)o;
        return partialMatches == ref.partialMatches
                && subFunctionID == ref.subFunctionID
                && targetFunction.equals(ref.targetFunction);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.targetFunction);
        hash = 67 * hash + this.subFunctionID;
        hash = 67 * hash + (this.partialMatches ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%d, %s=%s]", this.getClass().getSimpleName(),
                P_PARTIAL_MATCHES, partialMatches,
                P_SUBFUNCTION, subFunctionID,
                P_TARGET_FUNCTION, targetFunction);
    }
    // </editor-fold>
}
