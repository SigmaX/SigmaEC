package SigmaEC.represent.linear;

import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Initializer;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class IntVectorInitializer extends Initializer<IntVectorIndividual> {
    public final static String P_POPULATION_SIZE = "populationSize";
    public final static String P_NUM_DIMENSIONS = "numDimensions";
    public final static String P_DEFAULT_MAX = "defaultMax";
    public final static String P_DEFAULT_MIN = "defaultMin";
    public final static String P_MAXES = "maxes";
    public final static String P_MINS = "mins";
    public final static String P_RANDOM = "random";
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_STOP_ON_CONSTRAINT_VIOLATION = "stopOnConstraintViolation";
    public final static int HARDBOUND_ATTEMPTS = 10000;
    
    private final int populationSize;
    private final int numDimensions;
    private final Option<Integer> defaultMaxValue;
    private final Option<Integer> defaultMinValue;
    private final Option<int[]> maxes;
    private final Option<int[]> mins;
    private final Random random;
    private final Option<Constraint<LinearGenomeIndividual<IntGene>>> constraint;
    private final boolean stopOnConstraintViolation;

    public IntVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        stopOnConstraintViolation = parameters.getOptionalBooleanParameter(Parameters.push(base, P_STOP_ON_CONSTRAINT_VIOLATION), false);
        
        defaultMaxValue = parameters.getOptionalIntParameter(Parameters.push(base, P_DEFAULT_MAX));
        maxes = parameters.getOptionalIntArrayParameter(Parameters.push(base, P_MAXES));
            
        defaultMinValue = parameters.getOptionalIntParameter(Parameters.push(base, P_DEFAULT_MIN));
        mins = parameters.getOptionalIntArrayParameter(Parameters.push(base, P_MINS));
        
        if (!(defaultMaxValue.isDefined() ^ maxes.isDefined()))
            throw new IllegalStateException(String.format("%s: One of %s and %s must be defined, and not both.", this.getClass().getSimpleName(), P_DEFAULT_MAX, P_MAXES));
        
        if (!(defaultMinValue.isDefined() ^ mins.isDefined()))
            throw new IllegalStateException(String.format("%s: One of %s and %s must be defined, and not both.", this.getClass().getSimpleName(), P_DEFAULT_MIN, P_MINS));
        
        if (!((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) || (maxes.isDefined() && mins.isDefined())))
            throw new IllegalStateException(String.format("%s: Must use either default values or vectors for both max and min.", this.getClass().getSimpleName()));

        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        
        assert(repOK());
    }
    
    public IntVectorInitializer(final int populationSize, final int numDimensions, final int[] mins, final int[] maxes, final Random random, final Option<Constraint<LinearGenomeIndividual<IntGene>>> constraint, final boolean stopOnConstraintViolation) {
        assert(populationSize > 0);
        assert(numDimensions > 0);
        assert(mins != null);
        assert(maxes != null);
        assert(Misc.arrayLessThanOrEqualTo(mins, maxes));
        this.populationSize = populationSize;
        this.numDimensions = numDimensions;
        this.mins = new Option<>(Arrays.copyOf(mins, mins.length));
        this.maxes = new Option<>(Arrays.copyOf(maxes, maxes.length));
        defaultMaxValue = Option.NONE;
        defaultMinValue = Option.NONE;
        this.random = random;
        this.constraint = constraint;
        this.stopOnConstraintViolation = stopOnConstraintViolation;
        assert(repOK());
    }
    
    @Override
    public List<IntVectorIndividual> generatePopulation() {
        final List<IntVectorIndividual> population = new ArrayList<IntVectorIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(generateIndividual());
        }};
        assert(repOK());
        return population;
    }

    @Override
    public IntVectorIndividual generateIndividual() {
        assert(repOK());
        for (int i = 0; i < HARDBOUND_ATTEMPTS; i++) {
            final IntVectorIndividual newInd = defaultMaxValue.isDefined() ?
                    new IntVectorIndividual(random, numDimensions, defaultMinValue.get(), defaultMaxValue.get())
                    : new IntVectorIndividual(random, numDimensions, mins.get(), maxes.get());
            if (!constraint.isDefined() || !constraint.get().isViolated(newInd))
                return newInd;
            else if (stopOnConstraintViolation)
                throw new IllegalStateException(String.format("%s: unexpected constraint violation.", this.getClass().getSimpleName()));
        }
        throw new IllegalStateException(String.format("%s: maximum number of attempts reached (%d) while trying to generate and individual that did not violate the specified constraints.", this.getClass().getSimpleName(), HARDBOUND_ATTEMPTS));
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DEFAULT_MAX != null
                && !P_DEFAULT_MAX.isEmpty()
                && P_DEFAULT_MIN != null
                && !P_DEFAULT_MIN.isEmpty()
                && P_MAXES != null
                && !P_MAXES.isEmpty()
                && P_MINS != null
                && !P_MAXES.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_POPULATION_SIZE != null
                && !P_POPULATION_SIZE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && populationSize > 0
                && numDimensions > 0
                && ((defaultMaxValue.isDefined() && defaultMinValue.isDefined()) ^ (maxes.isDefined() && mins.isDefined()))
                && !(maxes.isDefined() && maxes.get().length != numDimensions)
                && !(mins.isDefined() && mins.get().length != numDimensions)
                && random != null
                && !(stopOnConstraintViolation && !constraint.isDefined());
        
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntVectorInitializer))
            return false;
        final IntVectorInitializer ref = (IntVectorInitializer) o;
        return populationSize == ref.populationSize
                && numDimensions == ref.numDimensions
                && random.equals(ref.random)
                && maxes.equals(ref.maxes)
                && mins.equals(ref.mins)
                && defaultMaxValue.equals(ref.defaultMaxValue)
                && defaultMinValue.equals(ref.defaultMinValue);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.populationSize;
        hash = 53 * hash + this.numDimensions;
        hash = 53 * hash + Objects.hashCode(this.defaultMaxValue);
        hash = 53 * hash + Objects.hashCode(this.defaultMinValue);
        hash = 53 * hash + Objects.hashCode(this.maxes);
        hash = 53 * hash + Objects.hashCode(this.mins);
        hash = 53 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_POPULATION_SIZE, populationSize,
                P_NUM_DIMENSIONS, numDimensions,
                P_RANDOM, random,
                P_DEFAULT_MIN, defaultMinValue,
                P_DEFAULT_MAX, defaultMaxValue,
                P_MINS, mins,
                P_MAXES, maxes);
    }
    // </editor-fold>
}
