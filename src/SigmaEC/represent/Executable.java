package SigmaEC.represent;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface Executable<I, O> {
    public O execute(final I input);
}
