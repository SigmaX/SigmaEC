package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public abstract class MigrationPolicy<T extends Individual> extends ContractObject {
    public abstract void migrateAll(final int generation, final Population<T> population, final Topology topology);
}
