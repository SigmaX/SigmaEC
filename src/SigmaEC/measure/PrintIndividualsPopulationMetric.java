package SigmaEC.measure;

import java.io.IOException;
import java.util.List;

/**
 * Prints a population by calling toString() on every individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintIndividualsPopulationMetric implements PopulationMetric
{
    public PrintIndividualsPopulationMetric()
    {
    }
    
    @Override
    public String measurePopulation(int run, int generation, List population) throws IOException
    {
        return String.format("%d, %d, %s%n", run, generation, population.toString());
    }

    @Override
    public void flush() throws IOException { }

    @Override
    public void close() throws IOException { }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "[PrintIndividualsPopulationMetric]";
    }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof PrintIndividualsPopulationMetric);
    }

    @Override
    public int hashCode()
    {
        return 7;
    }
    //</editor-fold>
}
