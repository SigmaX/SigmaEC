package SigmaEC.representation;

import java.util.Arrays;

/**
 * Simply wraps a double array.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleDoubleVectorIndividual extends DoubleVectorIndividual
{
    private double[] vector;
    
    /** Creates a copy of the specified vector. */
    public SimpleDoubleVectorIndividual(double[] vector)
    {
        this.vector = Arrays.copyOf(vector, vector.length);
    }
    
    @Override
    public int size()
    {
        return vector.length;
    }

    @Override
    public double getElement(int i)
    {
        return vector[i];
    }

    @Override
    public double[] getVector()
    {
        return Arrays.copyOf(vector, vector.length);
    }

    @Override
    public boolean repOK()
    {
        return vector != null;
    }

}
