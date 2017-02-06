package SigmaEC.evaluate.objective.function;

import SigmaEC.evaluate.VectorFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import SigmaEC.util.math.Vector;
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
public class ConcatenatedTruthTableObjective extends ObjectiveFunction<BooleanFunction, VectorFitness> {
    public final static String P_TARGET_FUNCTION = "targetFunction";
    public final static String P_PARTIAL_MATCHES = "partialMatches";
    
    private final ConcatenatedBooleanFunction targetFunction;
    private final boolean partialMatches;
    
    public ConcatenatedTruthTableObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        targetFunction = parameters.getInstanceFromParameter(Parameters.push(base, P_TARGET_FUNCTION), BooleanFunction.class);
        partialMatches = parameters.getOptionalBooleanParameter(Parameters.push(base, P_PARTIAL_MATCHES), true);
        assert(repOK());
    }
    
    public ConcatenatedTruthTableObjective(final ConcatenatedBooleanFunction targetFunction, final boolean partialMatches) {
        assert(targetFunction != null);
        this.targetFunction = targetFunction;
        this.partialMatches = partialMatches;
        assert(repOK());
    }
    
    @Override
    public VectorFitness fitness(final BooleanFunction ind) {
        assert(ind != null);
        assert(ind.numOutputs() == targetFunction.numOutputs());
        assert(ind.arity() == targetFunction.arity());
        
        double matches = 0;
        double[] subFunctionMatches = new double[targetFunction.numFunctions()];
        
        // Iterate over every row in the truth table
        final List<boolean[]> inputs = TruthTableObjective.bitStringPermutations(ind.arity());
        for (final boolean[] input : inputs) {
            final boolean[] circuitOutput = ind.execute(input);
            final boolean[] expectedOutput = targetFunction.execute(input);
            matches += match(circuitOutput, expectedOutput);
            subFunctionMatches = Vector.vectorSum(subFunctionMatches, subFunctionMatch(ind, input, circuitOutput, expectedOutput));
        }
        
        final double averageFitness = (double) matches / inputs.size();
        final double[] subFunctionFitnesses = matchesToFitness(subFunctionMatches, inputs.size());
        assert(repOK());
        return new VectorFitness(averageFitness, subFunctionFitnesses, getSubFunctionNames());
    }
    
    private double match(final boolean[] circuitOutput, final boolean[] expectedOutput) {
        assert(circuitOutput != null);
        assert(expectedOutput != null);
        assert(circuitOutput.length > 0);
        assert(circuitOutput.length == targetFunction.numOutputs());
        assert(circuitOutput.length == expectedOutput.length);
        
        if (partialMatches)
            return (double) TruthTableObjective.countMatchingElements(circuitOutput, expectedOutput)/circuitOutput.length;
        else if (Arrays.equals(circuitOutput, expectedOutput))
            return 1;
        return 0;
    }
    
    private double[] subFunctionMatch(final BooleanFunction ind, final boolean[] input, final boolean[] circuitOutput, final boolean[] expectedOutput) {
        assert(ind != null);
        assert(ind.numOutputs() == targetFunction.numOutputs());
        assert(ind.arity() == targetFunction.arity());
        assert(circuitOutput != null);
        assert(circuitOutput.length == targetFunction.numOutputs());
        assert(input != null);
        assert(input.length > 0);
        
        final double[] matches = new double[targetFunction.numFunctions()];
        
        for (int i = 0; i < targetFunction.numFunctions(); i++) {
            final BooleanFunction subFunction = targetFunction.getFunction(i);
            final int subOutputStart = getOutputOffset(i);
            
            final boolean[] expectedSubOutput = Arrays.copyOfRange(expectedOutput, subOutputStart, subOutputStart + subFunction.numOutputs());
            assert(Arrays.equals(expectedSubOutput, targetFunction.getFunction(i).execute(input)));
            final boolean[] subCircuitOutput = Arrays.copyOfRange(circuitOutput, subOutputStart, subOutputStart + subFunction.numOutputs());
            final double m;
            if (partialMatches)
                m = (double) TruthTableObjective.countMatchingElements(subCircuitOutput, expectedSubOutput)/subCircuitOutput.length;
            else if (Arrays.equals(subCircuitOutput, expectedSubOutput))
                m = 1;
            else
                m = 0;
            matches[i] = m;
        }
        return matches;
    }
    
    private int getOutputOffset(final int subFunctionID) {
        assert(targetFunction != null);
        assert(subFunctionID >= 0);
        assert(subFunctionID < targetFunction.numFunctions());
        int subFunctionOutputOffset = 0;
        for (int i = 0; i < subFunctionID; i++)
            subFunctionOutputOffset += targetFunction.getFunction(i).numOutputs();
        return subFunctionOutputOffset;
    }
    
    private double[] matchesToFitness(final double[] subFunctionMatches, final int numInputs) {
        assert(subFunctionMatches != null);
        assert(!Misc.containsNaNs(subFunctionMatches));
        assert(numInputs > 0);
        final double[] subFunctionFitnesses = new double[targetFunction.numFunctions()];
        for (int i = 0; i < subFunctionMatches.length; i++)
            subFunctionFitnesses[i] = (double) subFunctionMatches[i] / numInputs;
        return subFunctionFitnesses;
    }
    
    private String[] getSubFunctionNames() {
        final String[] names = new String[targetFunction.numFunctions()];
        for (int i = 0; i < targetFunction.numFunctions(); i++)
            names[i] = targetFunction.getFunction(i).getClass().getSimpleName();
        return names;
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
                && P_TARGET_FUNCTION != null
                && !P_TARGET_FUNCTION.isEmpty()
                && targetFunction != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ConcatenatedTruthTableObjective))
            return false;
        final ConcatenatedTruthTableObjective ref = (ConcatenatedTruthTableObjective)o;
        return partialMatches == ref.partialMatches
                && targetFunction.equals(ref.targetFunction);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.targetFunction);
        hash = 67 * hash + (this.partialMatches ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%B, %s=%s]", this.getClass().getSimpleName(),
                P_PARTIAL_MATCHES, partialMatches,
                P_TARGET_FUNCTION, targetFunction);
    }
    // </editor-fold>
}
