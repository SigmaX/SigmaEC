package SigmaEC.represent;

import java.util.List;

/**
 * Transform a genotype of DoubleGenes into a DoubleVectorPhenotype.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearDoubleDecoder extends Decoder<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype> {

    @Override
    public DoubleVectorPhenotype decode(final LinearGenomeIndividual<DoubleGene> individual) {
        return new DoubleVectorPhenotype(listToDoubleArray(individual.getGenome()));
    }
    
    private static double[] listToDoubleArray(final List<DoubleGene> list) {
        final double[] array = new double[list.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = list.get(i).value;
        return array;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof LinearDoubleDecoder);
    }

    @Override
    public int hashCode() {
        int hash = 29;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
