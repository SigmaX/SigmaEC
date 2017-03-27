package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.meta.Fitness;
import SigmaEC.represent.Individual;
import java.util.List;

/**
 * An IslandGenerator produces the island-specific configurations for a
 * heterogeneous island model.
 * 
 * @author Eric O. Scott
 */
public abstract class IslandGenerator<T extends Individual<F>, P, F extends Fitness> extends ContractObject{
    public abstract List<IslandConfiguration<T, P, F>> getIslands();
}
