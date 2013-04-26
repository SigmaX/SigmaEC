package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public interface PopulationMetric<T extends Individual>
{
    /** Gathers some information about a population and does something with it. */
    public abstract void measurePopulation(List<T> population);
}
