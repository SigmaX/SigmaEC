package SigmaEC.measure;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Prints a population to an OutputStream by calling toString() on every
 * individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintAllPopulationMetric implements PopulationMetric
{
    final private Writer writer;
    
    public PrintAllPopulationMetric(Writer writer)
    {
        this.writer = writer;
    }
    
    @Override
    public void measurePopulation(List population) throws IOException
    {
        writer.write(population.toString() + "\n");
    }

    @Override
    public boolean repOK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String toString()
    {
        return String.format("[PrintAllPopulationMetric: Writer=%s", writer);
    }
    
}
