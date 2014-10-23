package SigmaEC.represent;

import SigmaEC.ContractObject;
import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Initializer<T extends Individual> extends ContractObject {
    /** Generate a whole population of random individuals. */
    public abstract List<T> generatePopulation();
}
