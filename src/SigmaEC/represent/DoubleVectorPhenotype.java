package SigmaEC.represent;

import java.util.Arrays;

/**
 * Methods to associate a vector of doubles with the state of a class.  This is
 * used, for instance, to provide a phenotype to an individual that can be
 * represented in R^n.
 * 
 * @author Eric 'Siggy' Scott
 * @author Jeff Bassett
 */
public class DoubleVectorPhenotype implements Phenotype
{
    private final double[] phenotype;
    
    public DoubleVectorPhenotype(final double[] phenotype)
    {
        assert(phenotype != null);
        assert(phenotype.length > 0);
        this.phenotype = Arrays.copyOf(phenotype, phenotype.length);
    }
    
    public int size()
    {
        return phenotype.length;
    }
    
    public double getElement(int i) throws IndexOutOfBoundsException
    {
        assert(i >= 0);
        return phenotype[i];
    }
    
    /** Returns a defensive copy of the phenotype vector. */
    public double[] getVector()
    {
        return Arrays.copyOf(phenotype, phenotype.length);
    }
}