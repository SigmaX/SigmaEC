package SigmaEC.meta;

import SigmaEC.ContractObject;

/**
 * An object that stores fitness information for an individual.
 * 
 * At it's simplest, this just wraps a double.  But Fitness objects may also
 * contain more complex structure, such as multi-objective or
 * multi-task fitness information.
 * 
 * @author Eric O. Scott
 */
public abstract class Fitness extends ContractObject {
    public abstract double asScalar();
}
