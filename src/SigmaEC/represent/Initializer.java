package SigmaEC.represent;

import SigmaEC.BuilderT;
import SigmaEC.ContractObject;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public abstract class Initializer<T extends Individual> extends ContractObject {
    public abstract List<T> generateInitialPopulation();
    
    public interface InitializerBuilder<T extends Individual> extends BuilderT<Initializer<T>> {
        InitializerBuilder<T> random(final Random random);
    }
}
