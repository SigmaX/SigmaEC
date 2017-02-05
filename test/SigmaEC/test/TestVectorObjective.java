package SigmaEC.test;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;

/**
 * Simple fitness function for R^n, the sum of all the elements.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorObjective extends ObjectiveFunction<DoubleVectorIndividual<ScalarFitness>, ScalarFitness>
{
    @Override
    public ScalarFitness fitness(DoubleVectorIndividual ind)
    {
        double sum = 0;
        for(double d : ind.getGenomeArray())
            sum += d;
        return new ScalarFitness(sum);
    }

    @Override
    public boolean repOK() { return true; }
    
    @Override
    public String toString() { return String.format("[%s]", this.getClass().getSimpleName()); } 
    
    @Override
    public int getNumDimensions() { return 3;}

    @Override
    public void setStep(final int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof TestVectorObjective);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
