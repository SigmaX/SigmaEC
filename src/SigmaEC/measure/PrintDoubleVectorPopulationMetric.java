package SigmaEC.measure;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import java.io.IOException;
import java.util.ArrayList;
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
    
    @Override
    public MultipleDoubleArrayMeasurement measurePopulation(int run, int generation, final List<T> population) throws IOException
    {
        final List<double[]> arrays = new ArrayList<double[]>() {{
            for(T ind : population)
                add(Misc.prepend(ind.getID(), decoder.decode(ind).getVector()));
        }};
        assert(repOK());
        return new MultipleDoubleArrayMeasurement(run, generation, arrays);
    }

    @Override
    public void reset() { }

    @Override
    public void flush() throws IOException { }
    
    @Override
    public void close() throws IOException { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK()
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
