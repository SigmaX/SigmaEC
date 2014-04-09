package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 * Inverted form of Shekel's "foxholes" function, which contains n
 * (traditionally 25) local optima in R^2.  Traditionally, each variable is
 * bounded between -65.536 and 65.536 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class ShekelObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    IDoublePoint[] optima;
    
    /**
     * @param optima The (x,y) coordinates of the local optima.
     */
    public ShekelObjective(IDoublePoint[] optima)
    {
        if (optima == null)
            throw new IllegalArgumentException("ShekelObjective: optima array is null.");
        if (Misc.containsNulls(optima))
            throw new IllegalArgumentException("ShekelObjective: optima array is invalid (perhaps contains nulls).");
        this.optima = Arrays.copyOf(optima, optima.length); // Shallow copy okay because IDoublePoint is immutable.
        assert(repOK());
    }

    @Override
    public int getNumDimensions() { return 2; }

    @Override
    public double fitness(DoubleVectorPhenotype ind)
    {
        assert(ind.size() == 2);
        double sum = 0.002;
        for (int i = 0; i < optima.length; i++)
            sum += 1/(i + Math.pow(ind.getElement(0) - optima[i].x, 2) + Math.pow(ind.getElement(1) - optima[i].y, 2));
        assert(repOK());
        return sum;
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return optima != null
            && !Misc.containsNulls(optima);
    }
    
    @Override
    public String toString()
    {
        return String.format("[ShekelObjective: Optima=%s]", Arrays.toString(optima));
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof ShekelObjective))
            return false;
        
        ShekelObjective cRef = (ShekelObjective) o;
        return Arrays.deepEquals(optima, cRef.optima);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Arrays.deepHashCode(this.optima);
        return hash;
    }
    //</editor-fold>
}
