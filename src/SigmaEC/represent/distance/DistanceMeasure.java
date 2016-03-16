package SigmaEC.represent.distance;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public abstract class DistanceMeasure<T extends Individual> extends ContractObject {
    public abstract double distance(final T a, final T b);
}
