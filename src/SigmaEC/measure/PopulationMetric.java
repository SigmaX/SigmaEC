package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
import java.util.List;

/**
 * A PopulationMetric measures some property of a population of individuals,
 * such as its fitness, diversity, or phenotype, and creates an appropriate
 * Measurement object to store it.  A Decorator pattern is typically used
 * to create PopulationMetrics that do something with their measurements, such
 * as write them to a file or standard out.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface PopulationMetric<T extends Individual>
{
    /** Gathers some information about a population and does something with it.
     * @param run The run the sample is coming from
     * @param generation The generation the sample is coming from
     */
    public abstract Measurement measurePopulation(int run, int generation, List<T> population) throws IOException;
    
    /** Reset any mutable state to prepare for a fresh run. */
    public abstract void reset();
    
    /** Flush any I/O buffers. */
    public abstract void flush() throws IOException;
    
    /** Close any file resources used by this metric.  Once this is called,
     * the metric can no longer be used.
     */
    public abstract void close() throws IOException;
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
