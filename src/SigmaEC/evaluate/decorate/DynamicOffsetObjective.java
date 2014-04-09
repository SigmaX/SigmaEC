package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class DynamicOffsetObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    final private double bounds;
    final private ObjectiveFunction<DoubleVectorPhenotype> originalObjective;
    final private Random random;
    
    private ObjectiveFunction<DoubleVectorPhenotype> currentObjective;
    
    public DynamicOffsetObjective(ObjectiveFunction<DoubleVectorPhenotype> objective, final Random random, final double bounds)
    {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");
        this.originalObjective = objective;
        this.currentObjective = objective;
        this.random = random;
        this.bounds = bounds;
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions() {
        return originalObjective.getNumDimensions();
    }
    
    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        return currentObjective.fitness(ind);
    }

    @Override
    public void setGeneration(int i) {
        final double[] offset = new double[originalObjective.getNumDimensions()];
        for (int j = 0; j < offset.length; j++)
            offset[j] = (2*random.nextDouble() -1)*bounds;
        currentObjective = new TranslatedDoubleObjective(offset, originalObjective);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return originalObjective != null
                && !Double.isNaN(bounds)
                && !Double.isInfinite(bounds);
    }
    
    @Override
    public String toString()
    {
        return String.format("[%s: bounds=%f, originalObjective=%s, currentObjective=%s, random=%s]", this.getClass().getSimpleName(), bounds, originalObjective.toString(), currentObjective.toString(), random.toString());
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof DynamicOffsetObjective))
            return false;
        
        DynamicOffsetObjective cRef = (DynamicOffsetObjective) o;
        return Misc.doubleEquals(bounds, cRef.bounds)
                && random.equals(cRef.random)
                && originalObjective.equals(cRef.originalObjective)
                && currentObjective.equals(cRef.currentObjective);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.bounds) ^ (Double.doubleToLongBits(this.bounds) >>> 32));
        hash = 79 * hash + (this.originalObjective != null ? this.originalObjective.hashCode() : 0);
        hash = 79 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 79 * hash + (this.currentObjective != null ? this.currentObjective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
