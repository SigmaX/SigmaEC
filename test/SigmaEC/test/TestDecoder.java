package SigmaEC.test;

import SigmaEC.represent.Decoder;

/**
 * Decoder for TestIndividual -- the phenotype is the value of the trait
 * (phenotype is genotype).
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestDecoder extends Decoder<TestIndividual, TestPhenotype> {
    @Override
    public TestPhenotype decode(final TestIndividual individual) {
        return new TestPhenotype(individual.getTrait());
    }

    @Override
    public boolean repOK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
