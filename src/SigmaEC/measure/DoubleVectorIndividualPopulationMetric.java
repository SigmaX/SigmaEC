package SigmaEC.measure;

import SigmaEC.represent.CloneDecoder;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints a population of DoubleVectorIndividuals.  If the population has
 * a different kind of genotype, but a phenotype that can be interpreted as a
 * DoubleVectorIndividual, a decoder may be used to print the phenotypes.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleVectorIndividualPopulationMetric extends PopulationMetric<Individual> {
    private final static String P_DECODER = "decoder";
    private final Decoder decoder;
    
    public DoubleVectorIndividualPopulationMetric(final Parameters parameters, final String base) {
        final Option<Decoder> decoderOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        if (decoderOpt.isDefined())
            decoder = decoderOpt.get();
        else
            decoder = new CloneDecoder<DoubleVectorIndividual>(parameters, base);
        assert(repOK());
    }
    
    @Override
    public MultipleDoubleArrayMeasurement measurePopulation(int run, int generation, final List<Individual> population)
    {
        final List<double[]> arrays = new ArrayList<double[]>() {{
            for(Individual ind : population)
                add(Misc.prepend(ind.getID(), ((DoubleVectorIndividual)decoder.decode(ind)).getGenomeArray()));
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
        if (!(o instanceof DoubleVectorIndividualPopulationMetric))
            return false;
        final DoubleVectorIndividualPopulationMetric ref = (DoubleVectorIndividualPopulationMetric)o;
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
