package SigmaEC.measure;

import SigmaEC.represent.BitGene;
import SigmaEC.represent.LinearGenomeIndividual;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintBitStringIndividualPopulationMetric<T extends LinearGenomeIndividual<BitGene>> implements PopulationMetric<T>
{
    
    public PrintBitStringIndividualPopulationMetric() {
        assert(repOK());
    }
    
    @Override
    public MultipleStringMeasurement measurePopulation(final int run, final int generation, final List<T> population) throws IOException
    {
        final List<String> arrays = new ArrayList<String>() {{
            for(T ind : population) {
                final StringBuilder sb = new StringBuilder().append(run).append(",").append(generation).append(",").append(Long.toString(ind.getID()));
                for(final BitGene g : ind.getGenome())
                    sb.append(",").append(g.value);
                add(sb.toString());
            }
        }};
        assert(repOK());
        return new MultipleStringMeasurement(run, generation, arrays);
    }

    @Override
    public void reset() { }

    @Override
    public void flush() throws IOException { }
    
    @Override
    public void close() throws IOException { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof PrintDoubleVectorPopulationMetric;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    //</editor-fold>

}
