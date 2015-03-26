package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.IDoublePoint;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 * Inverted form of Shekel's "foxholes" function, which contains n
 * (traditionally 25) local optima in R^2.  Traditionally, each variable is
 * bounded between -65.536 and 65.536 (inclusive).
 * 
 * @author Eric 'Siggy' Scott
 */
public class ShekelObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    final private static IDoublePoint[] optima = new IDoublePoint[] {
        new IDoublePoint(-32, -32),
        new IDoublePoint(-32, -16),
        new IDoublePoint(-32, -0),
        new IDoublePoint(-32, 16),
        new IDoublePoint(-32, 32),
        
        new IDoublePoint(-16, -32),
        new IDoublePoint(-16, -16),
        new IDoublePoint(-16, -0),
        new IDoublePoint(-16, 16),
        new IDoublePoint(-16, 32),
        
        new IDoublePoint(0, -32),
        new IDoublePoint(0, -16),
        new IDoublePoint(0, -0),
        new IDoublePoint(0, 16),
        new IDoublePoint(0, 32),
        
        new IDoublePoint(16, -32),
        new IDoublePoint(16, -16),
        new IDoublePoint(16, -0),
        new IDoublePoint(16, 16),
        new IDoublePoint(16, 32),
        
        new IDoublePoint(32, -32),
        new IDoublePoint(32, -16),
        new IDoublePoint(32, -0),
        new IDoublePoint(32, 16),
        new IDoublePoint(32, 32),
    };
    
    
    
    /** @param optima The (x,y) coordinates of the local optima. */
    public ShekelObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }

    @Override
    public int getNumDimensions() { return 2; }

    @Override
    public double fitness(final DoubleVectorIndividual ind)
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
    final public boolean repOK() {
        return optima != null
            && !Misc.containsNulls(optima);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: optima=%s]", this.getClass().getSimpleName(), Arrays.toString(optima));
    }
    
    @Override
    public boolean equals(final Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof ShekelObjective))
            return false;
        
        final ShekelObjective cRef = (ShekelObjective) o;
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
