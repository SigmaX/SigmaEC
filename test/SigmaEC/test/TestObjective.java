package SigmaEC.test;

import SigmaEC.function.ObjectiveFunction;

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
}
