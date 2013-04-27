package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface PopulationMetric<T extends Individual>
{
    /** Gathers some information about a population and does something with it. */
    public abstract String measurePopulation(List<T> population) throws IOException;
    
    /** Flush any I/O buffers. */
    public abstract void flush() throws IOException;
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
