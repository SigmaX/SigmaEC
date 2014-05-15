/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SigmaEC.represent;

import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 *
 * @author eric
 */
public class DoubleVectorInitializer extends Initializer<DoubleVectorIndividual> {
    
    final int populationSize;
    final int numDimensions;
    final double[] maxValues;
    final double[] minValues;
    final Random random;
    
    private DoubleVectorInitializer(final Builder builder) {
        assert(builder != null);
        this.populationSize = builder.populationSize;
        this.numDimensions = builder.numDimensions;
        this.maxValues = builder.maxValues;
        this.minValues = builder.minValues;
        this.random = builder.random;
        assert(repOK());
    }
    
    public static class Builder implements InitializerBuilder<DoubleVectorIndividual> {
        private final static String P_POPULATION_SIZE = "populationSize";
        private final static String P_NUM_DIMENSIONS = "numDimensions";
        private final static String P_DEFAULT_MAX_VALUE = "defaultMaxValue";
        private final static String P_DEFAULT_MIN_VALUE = "defaultMinValue";
        private final static String P_MAX_VALUES = "maxValues";
        private final static String P_MIN_VALUES = "minValues";
        
        private int populationSize;
        private int numDimensions;
        private double[] maxValues;
        private double[] minValues;
        private Random random;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            this.populationSize = Parameters.getIntParameter(properties, Parameters.push(base, P_POPULATION_SIZE));
            this.numDimensions = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_DIMENSIONS));
            
            if (properties.containsKey(Parameters.push(base, P_DEFAULT_MAX_VALUE)))
                this.maxValues = repeatValue(Parameters.getDoubleParameter(properties, Parameters.push(base, P_DEFAULT_MAX_VALUE)), numDimensions);
            else
                this.maxValues = Parameters.getDoubleArrayParameter(properties, Parameters.push(base, P_MAX_VALUES));
            
            if (properties.containsKey(Parameters.push(base, P_DEFAULT_MIN_VALUE)))
                this.minValues = repeatValue(Parameters.getDoubleParameter(properties, Parameters.push(base, P_DEFAULT_MIN_VALUE)), numDimensions);
            else
                this.minValues = Parameters.getDoubleArrayParameter(properties, Parameters.push(base, P_MIN_VALUES));
            
            // random needs set by the caller before build(); is invoked.
                
        }
        
        private double[] repeatValue(final double val, final int times) {
            assert(times >= 0);
            final double[] array = new double[times];
            for (int i = 0; i < array.length; i++)
                array[i] = val;
            return array;
        }
        
        @Override
        public DoubleVectorInitializer build() {
            return new DoubleVectorInitializer(this);
        }

        @Override
        public Builder random(final Random random) {
            this.random = random;
            return this;
        }
        
        public Builder populationSize(final int populationSize) {
            assert(populationSize > 1);
            this.populationSize = populationSize;
            return this;
        }
        
        public Builder numDimensions(final int numDimensions) {
            assert(numDimensions > 0);
            this.numDimensions = numDimensions;
            return this;
        }
        
        public Builder maxValues(final double[] maxValues) {
            assert(maxValues.length == numDimensions);
            this.maxValues = Arrays.copyOf(maxValues, maxValues.length);
            return this;
        }
        
        public Builder minValues(final double[] minValues) {
            assert(minValues.length == numDimensions);
            this.minValues = Arrays.copyOf(minValues, minValues.length);
            return this;
        }
    }

    @Override
    public List<DoubleVectorIndividual> generateInitialPopulation() {
        return new ArrayList<DoubleVectorIndividual>(populationSize) {{
            for (int i = 0; i < populationSize; i++)
                add(new DoubleVectorIndividual(random, numDimensions, minValues, maxValues));
        }};
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
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
