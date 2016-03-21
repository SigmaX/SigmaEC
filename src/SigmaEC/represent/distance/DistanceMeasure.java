package SigmaEC.represent.distance;

import SigmaEC.ContractObject;

/**
 *
 * @author Eric O. Scott
 */
public abstract class DistanceMeasure<T> extends ContractObject {
    public abstract double distance(final T a, final T b);
}
