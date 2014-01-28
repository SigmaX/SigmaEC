package SigmaEC.test;

import SigmaEC.represent.Decoder;

/**
 * Decoder for TestIndividual -- the phenotype is the value of the trait
 * (phenotype is genotype).
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestDecoder implements Decoder<TestIndividual, TestPhenotype> {
    @Override
    public TestPhenotype decode(final TestIndividual individual) {
        return new TestPhenotype(individual.getTrait());
    }
    
}
