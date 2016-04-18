package SigmaEC.represent.linear;

import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private final Option<Integer> populationSize;
    private final int numDimensions;
    private final Option<Double> defaultMaxValue;
    private final Option<Double> defaultMinValue;
    private final Option<double[]> maxValues;
    private final Option<double[]> minValues;
    private final Random random;
    
    public DoubleVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        populationSize = parameters.getOptionalIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        if (!populationSize.isDefined())
            Logger.getLogger(this.getClass().toString()).log(Level.INFO, String.format("Parameter '%s' is not defined.  This initializer will only be able to generate single individuals.", Parameters.push(base, P_POPULATION_SIZE)));
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        
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
        if (((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) && defaultMaxValue.get() < defaultMinValue.get()))
            throw new IllegalStateException(String.format("%s: Max must be greater than or equal to min!.", this.getClass().getSimpleName()));

        
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }

    @Override
    public List<DoubleVectorIndividual> generatePopulation() {
        if (!populationSize.isDefined())
            throw new IllegalStateException("Attempted to generate a population, but population size is not defined.");
        final List<DoubleVectorIndividual> population = new ArrayList<DoubleVectorIndividual>(populationSize.get()) {{
            for (int i = 0; i < populationSize.get(); i++)
                add(generateIndividual());
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
                && populationSize != null
                && !(populationSize.isDefined() && populationSize.get() <= 0)
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
        return numDimensions == ref.numDimensions
                && populationSize.equals(ref.populationSize)
                && random.equals(ref.random)
                && maxValues.equals(ref.maxValues)
                && minValues.equals(ref.minValues)
                && defaultMaxValue.equals(ref.defaultMaxValue)
                && defaultMinValue.equals(ref.defaultMinValue);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.populationSize);
        hash = 61 * hash + this.numDimensions;
        hash = 61 * hash + Objects.hashCode(this.defaultMaxValue);
        hash = 61 * hash + Objects.hashCode(this.defaultMinValue);
        hash = 61 * hash + Objects.hashCode(this.maxValues);
        hash = 61 * hash + Objects.hashCode(this.minValues);
        hash = 61 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
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
