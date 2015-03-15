package SigmaEC.represent.linear;

import SigmaEC.Generator;
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
    private final static String P_EVALUATOR = "evaluator";
    
    private final int populationSize;
    private final int numDimensions;
    private final double[] maxValues;
    private final double[] minValues;
    private final Random random;
    private final Generator<DoubleVectorIndividual> evaluator;
    
    public DoubleVectorInitializer(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.populationSize = parameters.getIntParameter(Parameters.push(base, P_POPULATION_SIZE));
        this.numDimensions = parameters.getIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        this.evaluator = parameters.getInstanceFromParameter(Parameters.push(base, P_EVALUATOR), Generator.class);
        
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
        return evaluator.produceGeneration(new ArrayList<DoubleVectorIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(new DoubleVectorIndividual(random, numDimensions, minValues, maxValues));
        }});
    }

    @Override
    public DoubleVectorIndividual generateIndividual() {
        return evaluator.produceGeneration(new ArrayList<DoubleVectorIndividual>() {{
            add(new DoubleVectorIndividual(random, numDimensions, minValues, maxValues)); }})
                .get(0);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_DEFAULT_MAX_VALUE != null
                && !P_DEFAULT_MAX_VALUE.isEmpty()
                && P_DEFAULT_MIN_VALUE != null
                && !P_DEFAULT_MIN_VALUE.isEmpty()
                && P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
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
                && maxValues.length == numDimensions
                && minValues.length == numDimensions
                && random != null
                && !Misc.containsNaNs(maxValues)
                && !Misc.containsNaNs(minValues)
                && evaluator != null;
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
                && Misc.doubleArrayEquals(minValues, ref.minValues)
                && evaluator.equals(ref.evaluator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.populationSize;
        hash = 29 * hash + this.numDimensions;
        hash = 29 * hash + Arrays.hashCode(this.maxValues);
        hash = 29 * hash + Arrays.hashCode(this.minValues);
        hash = 29 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 29 * hash + (this.evaluator != null ? this.evaluator.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%d, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_POPULATION_SIZE, populationSize,
                P_NUM_DIMENSIONS, numDimensions,
                P_RANDOM, random,
                P_MAX_VALUES, Arrays.toString(maxValues),
                P_MIN_VALUES, Arrays.toString(minValues),
                P_EVALUATOR, evaluator);
    }
    // </editor-fold>
}
