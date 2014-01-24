package SigmaEC.measure;

import SigmaEC.represent.DoubleVectorIndividual;
import java.io.IOException;
import java.util.List;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintDoubleVectorPopulationMetric<T extends DoubleVectorIndividual> implements PopulationMetric<T>
{
    /** Prints one line for each individual in the population, with a column for
     * each value in the vector.  For instance, if the population contains the
     * individuals <1, 2, 3, 4> and <5, 6, 7, 8> at generation 5, then this will
     * return:
     * 
     * 5, 1, 2, 3, 4
     * 5, 5, 6, 7, 8
     * 
     * @throws IOException 
     */
    @Override
    public String measurePopulation(int run, int generation, List<T> population) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        for(DoubleVectorIndividual ind : population)
        {
            sb.append(run).append(", ").append(generation).append(", ").append(ind.getVector()[0]);
            for (int i = 1; i < ind.size(); i++)
                sb.append(", ").append(ind.getVector()[i]);
            sb.append("\n");
        }
        assert(repOK());
        return sb.toString();
    }

    @Override
    public void flush() throws IOException { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "[PrintIndividuPrintDoubleVectorPopulationMetricalsPopulationMetric]";
    }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof PrintIndividualsPopulationMetric);
    }

    @Override
    public int hashCode()
    {
        return 21;
    }
    //</editor-fold>
}
