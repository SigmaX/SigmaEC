package SigmaEC.test;

import SigmaEC.represent.Individual;
import java.util.Arrays;

/**
 * A simple individual with a real-vector genome.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorIndividual implements Individual
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
}
