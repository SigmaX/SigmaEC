package SigmaEC.measure;

import java.util.List;

/**
 * Prints a population to standard out.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintingPopulationMetric implements PopulationMetric
{
    @Override
    public void measurePopulation(List population)
    {
        System.out.println(population.toString());
    }
    
}
