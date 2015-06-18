package SigmaEC.measure;

import SigmaEC.represent.linear.BitGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitStringIndividualPopulationMetric<T extends LinearGenomeIndividual<BitGene>> extends PopulationMetric<T> {
    
    public BitStringIndividualPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public MultipleStringMeasurement measurePopulation(final int run, final int generation, final List<T> population)
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
    public void flush() { }
    
    @Override
    public void close() { }
    
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
        return o instanceof DoubleVectorIndividualPopulationMetric;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    //</editor-fold>

}
