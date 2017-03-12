package SigmaEC.represent.linear;

import SigmaEC.operate.constraint.Constraint;
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
public class DoubleVectorInitializer extends InitializerWithBounds<DoubleVectorIndividual, double[]> {
    public final static String P_POPULATION_SIZE = "populationSize";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_DEFAULT_MAX_VALUE = "defaultMaxValue";
    public final static String P_DEFAULT_MIN_VALUE = "defaultMinValue";
    public final static String P_MAX_VALUES = "maxValues";
    public final static String P_MIN_VALUES = "minValues";
    public final static String P_RANDOM = "random";
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_MAX_ATTEMPTS = "maxAttempts";
    public final static int DEFAULT_CONSTRAINT_ATTEMPTS = 10000;
    
    private final Option<Integer> populationSize;
    private final int numDimensions;
    private final Option<Double> defaultMaxValue;
    private final Option<Double> defaultMinValue;
    private final Option<double[]> maxValues;
    private final Option<double[]> minValues;
    private final Random random;
    private final Option<Constraint<DoubleVectorIndividual>> constraint;
    private final int maxAttempts;
    
    @Override
    public double[] getMinBounds() {
        if (!minValues.isDefined())
            throw new IllegalStateException(this.getClass().getSimpleName() + "No minimum bounds defined.");
        return minValues.get();
    }
    
    @Override
    public double[] getMaxBounds() { 
        if (!maxValues.isDefined())
            throw new IllegalStateException(this.getClass().getSimpleName() + "No maximum bounds defined.");
        return maxValues.get();
    }
    
    public DoubleVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        populationSize = parameters.getOptionalIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        if (!populationSize.isDefined())
            Logger.getLogger(this.getClass().toString()).log(Level.INFO, String.format("Parameter '%s' is not defined.  This initializer will only be able to generate single individuals.", Parameters.push(base, P_POPULATION_SIZE)));
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        maxAttempts = parameters.getOptionalIntParameter(Parameters.push(base, P_MAX_ATTEMPTS), DEFAULT_CONSTRAINT_ATTEMPTS);
        
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
    
    public DoubleVectorInitializer(final Option<Integer> populationSize, final int numDimensions, final Option<Double> defaultMaxValue, final Option<Double> defaultMinValue, final Option<double[]> maxValues, final Option<double[]> minValues, final Random random, final Option<Constraint<DoubleVectorIndividual>> constraint, final int maxAttempts) {
        assert(populationSize != null);
        assert(numDimensions > 0);
        assert(defaultMaxValue != null);
        assert(defaultMinValue != null);
        assert(maxValues != null);
        assert(minValues != null);
        assert(!(populationSize.isDefined() && populationSize.get() <= 0));
        assert(!(defaultMaxValue.isDefined() ^ defaultMinValue.isDefined()));
        assert(!(maxValues.isDefined() ^ minValues.isDefined()));
        assert(defaultMaxValue.isDefined() ^ maxValues.isDefined());
        assert(!(maxValues.isDefined() && maxValues.get().length != numDimensions));
        assert(!(minValues.isDefined() && minValues.get().length != maxValues.get().length));
        assert(random != null);
        assert(constraint != null);
        this.populationSize = populationSize;
        this.numDimensions = numDimensions;
        this.defaultMaxValue = defaultMaxValue;
        this.defaultMinValue = defaultMinValue;
        this.maxValues = maxValues;
        this.minValues = minValues;
        this.random = random;
        this.constraint = constraint;
        this.maxAttempts = maxAttempts;
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
        assert(repOK());
        for (int i = 0; i < DEFAULT_CONSTRAINT_ATTEMPTS; i++) {
            final DoubleVectorIndividual newInd = defaultMaxValue.isDefined() ?
                    new DoubleVectorIndividual(random, numDimensions, defaultMinValue.get(), defaultMaxValue.get())
                    : new DoubleVectorIndividual(random, numDimensions, minValues.get(), maxValues.get());
            if (!constraint.isDefined() || !constraint.get().isViolated(newInd)) {
                if (i > 0)
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, String.format("Individual generated after %d attempts.", i));
                return newInd;
            }
        }
        throw new IllegalStateException(String.format("%s: maximum number of attempts reached (%d) while trying to generate and individual that did not violate the specified constraints.", this.getClass().getSimpleName(), DEFAULT_CONSTRAINT_ATTEMPTS));

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
                && P_MAX_ATTEMPTS != null
                && !P_MAX_ATTEMPTS.isEmpty()
                && P_CONSTRAINT != null
                && !P_CONSTRAINT.isEmpty()
                && populationSize != null
                && !(populationSize.isDefined() && populationSize.get() <= 0)
                && numDimensions > 0
                && ((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) ^ (maxValues.isDefined() && minValues.isDefined()))
                && !(maxValues.isDefined() && maxValues.get().length != numDimensions)
                && !(minValues.isDefined() && minValues.get().length != numDimensions)
                && random != null
                && !(maxValues.isDefined() && Misc.containsNaNs(maxValues.get()))
                && !(maxValues.isDefined() && Misc.containsNaNs(minValues.get()))
                && constraint != null
                && maxAttempts > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorInitializer))
            return false;
        final DoubleVectorInitializer ref = (DoubleVectorInitializer) o;
        return numDimensions == ref.numDimensions
                && maxAttempts == ref.maxAttempts
                && constraint.equals(ref.constraint)
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
        hash = 61 * hash + this.maxAttempts;
        hash = 61 * hash + Objects.hashCode(this.defaultMaxValue);
        hash = 61 * hash + Objects.hashCode(this.defaultMinValue);
        hash = 61 * hash + Objects.hashCode(this.maxValues);
        hash = 61 * hash + Objects.hashCode(this.minValues);
        hash = 61 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s, %s=%d]", this.getClass().getSimpleName(),
                P_POPULATION_SIZE, populationSize,
                P_NUM_DIMENSIONS, numDimensions,
                P_RANDOM, random,
                P_DEFAULT_MIN_VALUE, defaultMinValue,
                P_DEFAULT_MAX_VALUE, defaultMaxValue,
                P_MIN_VALUES, minValues,
                P_MAX_VALUES, maxValues,
                P_CONSTRAINT, constraint,
                P_MAX_ATTEMPTS, maxAttempts);
    }
    // </editor-fold>
}
