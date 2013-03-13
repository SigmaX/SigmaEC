package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorIndividual;
import java.util.Random;

/**
 * The function \sum_i^n{i&x_i^4 + G(0,1)}, where G(0,1) is Gaussian noise with 
 * mean 0 and std 1. From the De Jong suite.  Traditionally, each variable is
 * bounded between -1.28 and 1.28 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class NoiseyQuarticObjective extends VectorObjectiveFunction<DoubleVectorIndividual>
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
        this.random = random;
        this.numDimensions = numDimensions;
        assert(repOK());
    }
    
    @Override
    public double fitness(DoubleVectorIndividual ind)
    {
        assert(ind.size() == numDimensions);
        double sum = 0;
        for (int i = 0; i < ind.size(); i++)
            sum += i*Math.pow(ind.getElement(i), 4) + random.nextGaussian();
        assert(repOK());
        return sum;
    }

    @Override
    public boolean repOK() {
        return random != null
                && numDimensions > 0;
    }

}
