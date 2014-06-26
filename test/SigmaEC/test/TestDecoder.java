package SigmaEC.test;

import SigmaEC.represent.Decoder;

/**
 * Decoder for TestIndividual -- the phenotype is the value of the trait
 * (phenotype is genotype).
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestDecoder extends Decoder<TestIndividual, Double> {
    @Override
    public Double decode(final TestIndividual individual) {
        return individual.getTrait();
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
