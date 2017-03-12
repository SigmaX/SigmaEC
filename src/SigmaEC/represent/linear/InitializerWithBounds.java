package SigmaEC.represent.linear;

import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;

/**
 *
 * @author Eric O. Scott
 */
public abstract class InitializerWithBounds<T extends Individual, B> extends Initializer<T> {
    public abstract B getMaxBounds();
    public abstract B getMinBounds();
}
