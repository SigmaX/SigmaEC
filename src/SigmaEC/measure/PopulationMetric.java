package SigmaEC.measure;

import SigmaEC.ContractObject;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;

/**
 * A PopulationMetric measures some property of a population of individuals,
 * such as its fitness, diversity, or phenotype, and creates an appropriate
 * Measurement object to store it.  A Decorator pattern is typically used
 * to create PopulationMetrics that do something with their measurements, such
 * as write them to a file or standard out.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class PopulationMetric<T extends Individual<F>, F extends Fitness> extends ContractObject
{
    /** Gathers some information about a population, and possibly also does
     * something with that information (such as write it to a file).
     * 
     * Implementation of measurePopulation() must ensure that ping() is also
     * called, and that any wrapped metrics also have either their
     * measurePopulation() or ping() method called.
     * 
     * @param run The run the sample is coming from
     * @param step The generation the sample is coming from
     * @return A Measurement is returned.
     */
    public abstract Measurement measurePopulation(int run, int step, Population<T, F> population);
    
    /** Notifies this PopulationMetric that the population has advanced to the next step.
     * This must be called at every step (or else an exception is thrown), and
     * because it may be used, for instance, to update the metric's memory of
     * the best-so-far individual.
     * 
     * @param step The current step the EA is on
     * @param population The population at the current step
     */
    public abstract void ping(int step, Population<T, F> population);
    
    /** Returns a comma-separated list of attribute names that this metric takes measurements of. */
    public abstract String csvHeader();
    
    /** Reset any mutable state to prepare for a fresh run. */
    public abstract void reset();
    
    /** Flush any I/O buffers. */
    public abstract void flush();
    
    /** Close any file resources used by this metric.  Once this is called,
     * the metric can no longer be used. */
    public abstract void close();
}
