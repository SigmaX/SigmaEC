package SigmaEC.test;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorIndividual;

/**
 * Decoder for use with TestVectorIndividual -- the phenotype vector is
 * identical to the genotype vector.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorDecoder extends Decoder<TestVectorIndividual, DoubleVectorIndividual> {
    @Override
    public DoubleVectorIndividual decode(final TestVectorIndividual individual) {
        return new DoubleVectorIndividual(individual.getVector());
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
