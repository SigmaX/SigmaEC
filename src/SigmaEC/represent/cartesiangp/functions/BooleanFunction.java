package SigmaEC.represent.cartesiangp.functions;

import SigmaEC.represent.Executable;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface BooleanFunction extends Executable<boolean[], boolean[]> {
    @Override
    public boolean[] execute(boolean[] input);
}
