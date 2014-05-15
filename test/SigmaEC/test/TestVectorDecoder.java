package SigmaEC.test;

import SigmaEC.represent.Decoder;
import SigmaEC.represent.DoubleVectorPhenotype;

/**
 * Decoder for use with TestVectorIndividual -- the phenotype vector is
 * identical to the genotype vector.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorDecoder extends Decoder<TestVectorIndividual, DoubleVectorPhenotype> {
    @Override
    public DoubleVectorPhenotype decode(final TestVectorIndividual individual) {
        return new DoubleVectorPhenotype(individual.getVector());
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
