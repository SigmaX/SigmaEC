package SigmaEC.test;

import SigmaEC.evaluate.ObjectiveFunction;

/**
 * A simple objective for use with TestIndividual -- fitness is equal to the
 * value of the trait.  Intended for use with unit tests.
 * 
 * @author Eric 'Siggy' Scott
 */ 
public class TestObjective implements ObjectiveFunction<TestPhenotype>
{
    @Override
    public double fitness(final TestPhenotype ind) { return ind.getTrait(); }

    @Override
    public boolean repOK() { return true; }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof TestObjective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public void setGeneration(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumDimensions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
