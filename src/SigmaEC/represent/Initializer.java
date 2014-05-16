package SigmaEC.represent;

import SigmaEC.ContractObject;
import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Initializer<T extends Individual> extends ContractObject {
    public abstract List<T> generateInitialPopulation();
}
