package SigmaEC.util.math.dist;

import SigmaEC.ContractObject;

/**
 *
 * @author Eric O. Scott
 */
public abstract class Distribution extends ContractObject {
    public abstract double sample();
}
