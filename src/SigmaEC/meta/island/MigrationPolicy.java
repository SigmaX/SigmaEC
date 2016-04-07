package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public abstract class MigrationPolicy<T extends Individual> extends ContractObject {
    /** Migrate individuals without making any use of island-specific information
     *  This simply moves individuals from one sub-population to another.  It does not,
     *  for instance, re-evaluate their fitness in the new environment.
     */
    public abstract void migrateAll(final int generation, final Population<T> population, final Topology topology);
    /** Migrate individuals, optionally making use of island-specific information.
     *  This, for instance, may re-evaluate the fitness of an individual in its
     *  new environment, post-migration.
     */
    public abstract void migrateAll(final int generation, final Population<T> population, final Topology topology, final Option<List<IslandConfiguration>> islands);
}
