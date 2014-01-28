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
}
