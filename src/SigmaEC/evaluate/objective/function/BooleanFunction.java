package SigmaEC.evaluate.objective.function;

import SigmaEC.represent.Executable;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface BooleanFunction extends Executable<boolean[], boolean[]> {
    public int arity();
    public int numOutputs();
    @Override
    public boolean[] execute(boolean[] input);
}
