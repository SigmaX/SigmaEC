package SigmaEC.represent;

import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class LinearDoubleDecoder implements Decoder<LinearGenomeIndividual<DoubleGene>, DoubleVectorPhenotype> {

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
    
}
