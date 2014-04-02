package SigmaEC.test;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;

/**
 * Simple fitness function for R^n, the sum of all the elements.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        double sum = 0;
        for(double d : ind.getVector())
            sum += d;
        return sum;
    }

    @Override
    public boolean repOK() { return true; }
    @Override
    public String toString() { return "[TestVectorObjective]"; } 
    public int getNumDimensions() { return 3;}

    @Override
    public void setGeneration(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
