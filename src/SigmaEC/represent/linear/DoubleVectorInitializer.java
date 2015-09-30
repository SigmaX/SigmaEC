package SigmaEC.represent.linear;

import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final Option<Double> defaultMaxValue;
    private final Option<Double> defaultMinValue;
    private final Option<double[]> maxValues;
    private final Option<double[]> minValues;
    private final Random random;
    
    public DoubleVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        
        defaultMaxValue = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DEFAULT_MAX_VALUE));
        maxValues = parameters.getOptionalDoubleArrayParameter(Parameters.push(base, P_MAX_VALUES));
            
        defaultMinValue = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DEFAULT_MIN_VALUE));
        minValues = parameters.getOptionalDoubleArrayParameter(Parameters.push(base, P_MIN_VALUES));
        
        if (!(defaultMaxValue.isDefined() ^ maxValues.isDefined()))
            throw new IllegalStateException(String.format("%s: One of %s and %s must be defined, and not both.", this.getClass().getSimpleName(), P_DEFAULT_MAX_VALUE, P_MAX_VALUES));
        
        if (!(defaultMinValue.isDefined() ^ minValues.isDefined()))
            throw new IllegalStateException(String.format("%s: One of %s and %s must be defined, and not both.", this.getClass().getSimpleName(), P_DEFAULT_MIN_VALUE, P_MIN_VALUES));
        
        if (!((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) || (maxValues.isDefined() && minValues.isDefined())))
            throw new IllegalStateException(String.format("%s: Must use either default values or vectors for both max and min.", this.getClass().getSimpleName()));

        
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }
    
    private double getMaxValue(final int gene) {
        assert(gene > 0);
        assert(gene < numDimensions);
        if (defaultMaxValue.isDefined())
            return defaultMaxValue.get();
        return maxValues.get()[gene];
    }
    
    private double getMinValue(final int gene) {
        assert(gene > 0);
        assert(gene < numDimensions);
        if (defaultMinValue.isDefined())
            return defaultMinValue.get();
        return minValues.get()[gene];
    }

    @Override
    public List<DoubleVectorIndividual> generatePopulation() {
        final List<DoubleVectorIndividual> population = new ArrayList<DoubleVectorIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++) {
                if (defaultMaxValue.isDefined())
                    add(new DoubleVectorIndividual(random, numDimensions, defaultMinValue.get(), defaultMaxValue.get()));
                else
                    add(new DoubleVectorIndividual(random, numDimensions, minValues.get(), maxValues.get()));
            }
        }};
        assert(repOK());
        return population;
    }

    @Override
    public DoubleVectorIndividual generateIndividual() {
        final DoubleVectorIndividual newInd = defaultMaxValue.isDefined() ?
                new DoubleVectorIndividual(random, numDimensions, defaultMinValue.get(), defaultMaxValue.get())
                : new DoubleVectorIndividual(random, numDimensions, minValues.get(), maxValues.get());
        assert(repOK());
        return newInd;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DEFAULT_MAX_VALUE != null
                && !P_DEFAULT_MAX_VALUE.isEmpty()
                && P_DEFAULT_MIN_VALUE != null
                && !P_DEFAULT_MIN_VALUE.isEmpty()
                && P_MAX_VALUES != null
                && !P_MAX_VALUES.isEmpty()
                && P_MIN_VALUES != null
                && !P_MAX_VALUES.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_POPULATION_SIZE != null
                && !P_POPULATION_SIZE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && populationSize > 0
                && numDimensions > 0
                && ((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) ^ (maxValues.isDefined() && minValues.isDefined()))
                && !(maxValues.isDefined() && maxValues.get().length != numDimensions)
                && !(minValues.isDefined() && minValues.get().length != numDimensions)
                && random != null
                && !(maxValues.isDefined() && Misc.containsNaNs(maxValues.get()))
                && !(maxValues.isDefined() && Misc.containsNaNs(minValues.get()));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorInitializer))
            return false;
        final DoubleVectorInitializer ref = (DoubleVectorInitializer) o;
        return populationSize == ref.populationSize
                && numDimensions == ref.numDimensions
                && random.equals(ref.random)
                && maxValues.equals(ref.maxValues)
                && minValues.equals(ref.minValues)
                && defaultMaxValue.equals(ref.defaultMaxValue)
                && defaultMinValue.equals(ref.defaultMinValue);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.populationSize;
        hash = 23 * hash + this.numDimensions;
        hash = 23 * hash + Objects.hashCode(this.defaultMaxValue);
        hash = 23 * hash + Objects.hashCode(this.defaultMinValue);
        hash = 23 * hash + Objects.hashCode(this.maxValues);
        hash = 23 * hash + Objects.hashCode(this.minValues);
        hash = 23 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_POPULATION_SIZE, populationSize,
                P_NUM_DIMENSIONS, numDimensions,
                P_RANDOM, random,
                P_DEFAULT_MIN_VALUE, defaultMinValue,
                P_DEFAULT_MAX_VALUE, defaultMaxValue,
                P_MIN_VALUES, minValues,
                P_MAX_VALUES, maxValues);
    }
    // </editor-fold>
}
