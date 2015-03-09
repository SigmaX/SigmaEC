package SigmaEC.represent.linear;

import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class DoubleVectorInitializer extends Initializer<DoubleVectorIndividual> {
    public final static String P_POPULATION_SIZE = "populationSize";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_DEFAULT_MAX_VALUE = "defaultMaxValue";
    public final static String P_DEFAULT_MIN_VALUE = "defaultMinValue";
    public final static String P_MAX_VALUES = "maxValues";
    public final static String P_MIN_VALUES = "minValues";
    public final static String P_RANDOM = "random";
    
    private final int populationSize;
    private final int numDimensions;
    private final double[] maxValues;
    private final double[] minValues;
    private final Random random;
    
    public DoubleVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        
        if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MAX_VALUE)))
            this.maxValues = Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MAX_VALUE)), numDimensions);
        else
            this.maxValues = parameters.getDoubleArrayParameter(Parameters.push(base, P_MAX_VALUES));
            
        if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MIN_VALUE)))
            this.minValues = Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MIN_VALUE)), numDimensions);
        else
            this.minValues = parameters.getDoubleArrayParameter(Parameters.push(base, P_MIN_VALUES));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }

    @Override
    public List<DoubleVectorIndividual> generatePopulation() {
        return new ArrayList<DoubleVectorIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(new DoubleVectorIndividual(random, numDimensions, minValues, maxValues));
        }};
    }

    @Override
    public DoubleVectorIndividual generationIndividual() {
        return new DoubleVectorIndividual(random, numDimensions, minValues, maxValues);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return populationSize > 0
                && numDimensions > 0
                && maxValues.length == numDimensions
                && minValues.length == numDimensions
                && random != null
                && !Misc.containsNaNs(maxValues)
                && !Misc.containsNaNs(minValues);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorInitializer))
            return false;
        final DoubleVectorInitializer ref = (DoubleVectorInitializer) o;
        return populationSize == ref.populationSize
                && numDimensions == ref.numDimensions
                && random.equals(ref.random)
                && Misc.doubleArrayEquals(maxValues, ref.maxValues)
                && Misc.doubleArrayEquals(minValues, ref.minValues);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.populationSize;
        hash = 29 * hash + this.numDimensions;
        hash = 29 * hash + Arrays.hashCode(this.maxValues);
        hash = 29 * hash + Arrays.hashCode(this.minValues);
        hash = 29 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: populationSize=%d, numDimensions=%d, random=%s, maxValues=%s, minValues=%s]", this.getClass().getSimpleName(), populationSize, numDimensions, random.toString(), Arrays.toString(maxValues), Arrays.toString(minValues));
    }
    // </editor-fold>
}
