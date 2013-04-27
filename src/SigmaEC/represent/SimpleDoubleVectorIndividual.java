package SigmaEC.represent;

import java.util.Arrays;

/**
 * Simply wraps a double array.
 * 
 * @author Eric 'Siggy' Scott
 */
public class SimpleDoubleVectorIndividual implements Individual, DoubleVectorIndividual
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

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return vector != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[SimpleDoubleVectorIndividual: Vector=%s", vector);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof SimpleDoubleVectorIndividual))
            return false;
        SimpleDoubleVectorIndividual cRef = (SimpleDoubleVectorIndividual) o;
        return vector.equals(cRef.vector);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Arrays.hashCode(this.vector);
        return hash;
    }
    //</editor-fold>
}
