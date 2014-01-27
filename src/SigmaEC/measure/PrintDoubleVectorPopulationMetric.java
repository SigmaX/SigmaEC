package SigmaEC.measure;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.represent.Individual;
import java.io.IOException;
import java.util.List;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintDoubleVectorPopulationMetric<T extends Individual> implements PopulationMetric<T>
{
    private final Decoder<T, DoubleVectorPhenotype> decoder;
    
    public PrintDoubleVectorPopulationMetric(final Decoder<T, DoubleVectorPhenotype> decoder) throws NullPointerException
    {
        if (decoder == null)
            throw new NullPointerException(this.getClass().getName() + ": decoder is null.");
        this.decoder = decoder;
        assert(repOK());
    }
    
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
        for(T ind : population)
        {
            final double[] phenotype = decoder.decode(ind).getVector();
            sb.append(run).append(", ").append(generation).append(", ").append(phenotype[0]);
            for (int i = 1; i < phenotype.length; i++)
                sb.append(", ").append(phenotype[i]);
            sb.append("\n");
        }
        assert(repOK());
        return sb.toString();
    }

    @Override
    public void flush() throws IOException { }
    
    @Override
    public void close() throws IOException { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return decoder != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[PrintDoubleVectorPopulationMetric: decoder=%s]", decoder);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof PrintDoubleVectorPopulationMetric))
            return false;
        final PrintDoubleVectorPopulationMetric ref = (PrintDoubleVectorPopulationMetric)o;
        return decoder.equals(ref.decoder);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        return hash;
    }
    //</editor-fold>

}
