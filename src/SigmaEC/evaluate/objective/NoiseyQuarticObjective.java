package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;
import java.util.Random;

/**
 * The function \sum_i^n{i&x_i^4 + G(0,1)}, where G(0,1) is Gaussian noise with 
 * mean 0 and std 1. From the De Jong suite.  Traditionally, each variable is
 * bounded between -1.28 and 1.28 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class NoiseyQuarticObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final Random random;
    private final int numDimensions;

    @Override
    public int getNumDimensions()
    {
        return numDimensions;
    }
    
    public NoiseyQuarticObjective(Random random, int numDimensions)
    {
        if (numDimensions < 1)
            throw new IllegalArgumentException("NoiseQuarticObjective: numDimensions is < 1.");
        if (numDimensions == Double.POSITIVE_INFINITY)
            throw new IllegalArgumentException("NoiseQuarticObjective: numDimensions is infinite, must be finite.");
        this.random = random;
        this.numDimensions = numDimensions;
        assert(repOK());
    }
    
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for (int i = 0; i < ind.size(); i++)
            sum += i*Math.pow(ind.getElement(i), 4) + random.nextGaussian();
        assert(repOK());
        return sum;
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() 
    {
        return random != null
                && numDimensions > 0
                && numDimensions != Double.POSITIVE_INFINITY;
    }
    
    @Override
    public String toString()
    {
        return String.format("[NoiseyQuarticObjective: NumDimensions=%d, Random=%s]", numDimensions, random);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof NoiseyQuarticObjective))
            return false;
        
        NoiseyQuarticObjective cRef = (NoiseyQuarticObjective) o;
        return numDimensions == cRef.numDimensions
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 43 * hash + this.numDimensions;
        return hash;
    }
    // </editor-fold>
}
