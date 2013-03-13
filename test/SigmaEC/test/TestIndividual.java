package SigmaEC.test;

import SigmaEC.represent.Individual;

/**
 * A simple individual with 1 continuous trait.  Intended for use with unit
 * tests.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestIndividual implements Individual
{
    private double trait;
    public double getTrait() { return trait; }

    public TestIndividual(double trait) { this.trait = trait; }
    
    @Override
    public boolean repOK() { return true; }
}
