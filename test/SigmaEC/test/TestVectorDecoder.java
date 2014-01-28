package SigmaEC.test;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorPhenotype;

/**
 * Decoder for use with TestVectorIndividual -- the phenotype vector is
 * identical to the genotype vector.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorDecoder implements Decoder<TestVectorIndividual, DoubleVectorPhenotype> {
    @Override
    public DoubleVectorPhenotype decode(final TestVectorIndividual individual) {
        return new DoubleVectorPhenotype(individual.getVector());
    }
}
