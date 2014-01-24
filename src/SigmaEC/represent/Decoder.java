package SigmaEC.represent;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface Decoder<T extends Individual, P> {
    P decode(T individual);
}
