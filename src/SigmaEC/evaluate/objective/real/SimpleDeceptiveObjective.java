package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.transform.MaxObjective;
import SigmaEC.evaluate.transform.TranslatedDoubleObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SimpleDeceptiveObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_BASIN_STD = "basinStd";
    
    private final int numDimensions;
    private final double basinStd;
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    
    public SimpleDeceptiveObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        
        basinStd = parameters.getDoubleParameter(Parameters.push(base, P_BASIN_STD));
        if (basinStd <= 0.0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": basinStd is <= 0, must be positive.");
        if (Double.isNaN(basinStd) || Double.isInfinite(basinStd))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": basinStd is NaN or infinite, must be finite.");
        objective = initObjective(numDimensions, basinStd);
        
        assert(repOK());
    }

    private static ObjectiveFunction<DoubleVectorIndividual> initObjective(final int numDimensions, final double basinStd) throws IllegalArgumentException {
        final double optimumHeight = 1.0;
        final ObjectiveFunction<DoubleVectorIndividual> basin, optimum, offsetOptimum;
        basin = new GaussianObjective(numDimensions, optimumHeight/3, basinStd);
        optimum = new GaussianObjective(numDimensions, optimumHeight, basinStd/10.0);
        final double[] offset = new double[numDimensions];
        offset[0] = 2*basinStd;
        offsetOptimum = new TranslatedDoubleObjective(offset, optimum);
        return new MaxObjective<DoubleVectorIndividual>(new ArrayList<ObjectiveFunction<DoubleVectorIndividual>>() {{
            add(basin);
            add(offsetOptimum);
        }});
    }
    
    public SimpleDeceptiveObjective(final int numDimensions, final double basinStd) {
        if (numDimensions < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": numDimensions is < 1.");
        this.basinStd = basinStd;
        if (basinStd <= 0.0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": basinStd is <= 0, must be positive.");
        if (Double.isNaN(basinStd) || Double.isInfinite(basinStd))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": basinStd is NaN or infinite, must be finite.");
        this.numDimensions = numDimensions;
        objective = initObjective(numDimensions, basinStd);
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        return objective.fitness(ind);
    }

    @Override
    public void setStep(int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() {
        return numDimensions;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return numDimensions > 0
                && !Double.isNaN(basinStd)
                && Double.isFinite(basinStd)
                && basinStd > 0.0
                && objective != null
                && objective.getNumDimensions() == numDimensions
                && P_BASIN_STD != null
                && !P_BASIN_STD.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof SimpleDeceptiveObjective))
            return false;
        final SimpleDeceptiveObjective ref = (SimpleDeceptiveObjective) o;
        return numDimensions == ref.numDimensions;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.numDimensions;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%f]", this.getClass().getSimpleName(),
                P_NUM_DIMENSIONS, numDimensions,
                P_BASIN_STD, basinStd);
    }
    // </editor-fold>
}
