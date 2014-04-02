package SigmaEC.test;

import SigmaEC.represent.Individual;
import java.util.Arrays;

/**
 * A simple individual with a real-vector genome.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorIndividual extends Individual
{
    private final double[] vector;
    public TestVectorIndividual(final double[] vector)
    {
        this.vector = vector;
    }

    public double[] getVector() {
        return Arrays.copyOf(vector, vector.length);
    }

    @Override
    public boolean repOK() { return true; }

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
