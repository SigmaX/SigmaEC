package SigmaEC.test;

import SigmaEC.evaluate.ObjectiveFunction;

/**
 * A simple objective for use with TestIndividual -- fitness is equal to the
 * value of the trait.  Intended for use with unit tests.
 * 
 * @author Eric 'Siggy' Scott
 */ 
public class TestObjective implements ObjectiveFunction<TestIndividual>
{
    @Override
    public double fitness(TestIndividual ind) { return ind.getTrait(); }

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
}
