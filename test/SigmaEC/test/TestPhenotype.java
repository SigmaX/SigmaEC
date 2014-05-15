package SigmaEC.test;

import SigmaEC.represent.Phenotype;

/**
 * A phenotype with a single double value.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestPhenotype implements Phenotype {
    private final double trait;
    public TestPhenotype(final double trait) { this.trait = trait; }
    public double getTrait() { return trait; }

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
