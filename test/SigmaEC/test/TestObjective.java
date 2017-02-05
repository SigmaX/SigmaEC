package SigmaEC.test;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.util.Parameters;

/**
 * A simple objective for use with TestIndividual -- fitness is equal to the
 * value of the trait.  Intended for use with unit tests.
 * 
 * @author Eric 'Siggy' Scott
 */ 
public class TestObjective extends ObjectiveFunction<TestIndividual, ScalarFitness> {
    
    public TestObjective(final Parameters parameters, final String base) { }
    
    @Override
    public ScalarFitness fitness(final TestIndividual ind) { return new ScalarFitness(ind.getTrait()); }

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
    public void setStep(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumDimensions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
