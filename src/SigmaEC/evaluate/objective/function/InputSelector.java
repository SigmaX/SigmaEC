package SigmaEC.evaluate.objective.function;

import SigmaEC.ContractObject;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class InputSelector extends ContractObject implements BooleanFunction {
    public final static String P_NUM_CHANNELS = "numChannels";
    public final static String P_OBJECTIVE = "objective";
    
    private final int numChannels;
    private final Option<Integer> channel;
    private final ConcatenatedBooleanFunction objective;
    
    public InputSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numChannels = parameters.getIntParameter(Parameters.push(base, P_NUM_CHANNELS));
        channel = Option.NONE;
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ConcatenatedBooleanFunction.class);
        assert(repOK());
    }
    
    private InputSelector(final InputSelector ref, final int channel) {
        assert(ref != null);
        assert(channel >= 0);
        assert(channel < ref.numChannels);
        numChannels = ref.numChannels;
        objective = ref.objective;
        this.channel = new Option<>(channel);
    }
    
    public InputSelector setChannel(final int channel) {
        assert(channel >= 0);
        assert(channel < numChannels);
        assert(repOK());
        return new InputSelector(this, channel);
    }
    
    /** Given an output index, determine which subfunction in the objective (and thus channel for the InputSelector) that output belongs to. */
    public int outputToChannel(final int output) {
        assert(output >= 0);
        assert(output < objective.numOutputs());
        assert(repOK());
        int startForThisChannel = 0;
        for (int i = 0; i < objective.numFunctions(); i++) {
            if (output >= startForThisChannel && output < startForThisChannel + objective.getFunction(i).arity())
                return i;
            startForThisChannel += objective.getFunction(i).arity();
        }
        throw new IllegalStateException("Reached an invalid control state.");
    }
    
    @Override
    public int arity() {
        return numChannels;
    }

    @Override
    public int numOutputs() {
        return 1;
    }

    @Override
    public boolean[] execute(final boolean[] input) {
        assert(input != null);
        assert(input.length == arity());
        if (!channel.isDefined())
            throw new IllegalStateException(String.format("%s: attempted to execute before selecting inputs.", this.getClass().getSimpleName()));
        return new boolean[] { input[channel.get()] };
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_CHANNELS != null
                && !P_NUM_CHANNELS.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && numChannels > 0
                && objective != null
                && numChannels == objective.numFunctions()
                && channel != null
                && !(channel.isDefined() && channel.get() < 1)
                && !(channel.isDefined() && channel.get() > numChannels);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof InputSelector))
            return false;
        final InputSelector ref = (InputSelector)o;
        return numChannels == ref.numChannels
                && channel.equals(ref.channel)
                && objective.equals(ref.objective);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + this.numChannels;
        hash = 23 * hash + Objects.hashCode(this.channel);
        hash = 23 * hash + Objects.hashCode(this.objective);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, channel=%s]", this.getClass().getSimpleName(),
                P_NUM_CHANNELS, numChannels,
                P_OBJECTIVE, objective,
                channel);
    }
    // </editor-fold>
}
