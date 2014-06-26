package SigmaEC.measure;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintDoubleVectorPopulationMetric<T extends Individual> extends PopulationMetric<T>
{
    private final Decoder<T, DoubleVectorIndividual> decoder;
    
    public PrintDoubleVectorPopulationMetric(final Decoder<T, DoubleVectorIndividual> decoder) throws NullPointerException
    {
        if (decoder == null)
            throw new NullPointerException(this.getClass().getName() + ": decoder is null.");
        this.decoder = decoder;
        assert(repOK());
    }
    
    @Override
    public MultipleDoubleArrayMeasurement measurePopulation(int run, int generation, final List<T> population)
    {
        final List<double[]> arrays = new ArrayList<double[]>() {{
            for(T ind : population)
                add(Misc.prepend(ind.getID(), decoder.decode(ind).getGenomeArray()));
        }};
        assert(repOK());
        return new MultipleDoubleArrayMeasurement(run, generation, arrays);
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }
    
    @Override
    public void close() { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK()
    {
        return decoder != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%s: decoder=%s]", this.getClass().getSimpleName(), decoder);
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
