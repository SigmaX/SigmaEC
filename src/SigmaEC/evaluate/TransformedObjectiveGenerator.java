package SigmaEC.evaluate;

import SigmaEC.evaluate.decorate.TranslatedDoubleObjective;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.Random;

/**
 * An objective generator that produces new problem instances by applying a
 * random transformation to a prototype function.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TransformedObjectiveGenerator extends ObjectiveGenerator<TranslatedDoubleObjective, DoubleVectorPhenotype> {
    private final double bounds;
    private final ObjectiveFunction<DoubleVectorPhenotype> prototype;
    private final Strategy.TranformationStrategy strategy;
    private final Random random;
    
    public TransformedObjectiveGenerator(final ObjectiveFunction<DoubleVectorPhenotype> objective, final Strategy.TranformationStrategy strategy, final Random random, final double bounds) {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");
        this.prototype = objective;
        this.strategy = strategy;
        this.random = random;
        this.bounds = bounds;
        assert(repOK());
    }
    
    @Override
    public TranslatedDoubleObjective getNewInstance() {
        return strategy.getNewInstance(prototype, random);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return prototype != null
                && strategy != null
                && random != null
                && !Double.isNaN(bounds)
                && !Double.isInfinite(bounds);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransformedObjectiveGenerator))
            return false;
        final TransformedObjectiveGenerator ref = (TransformedObjectiveGenerator) o;
        return Misc.doubleEquals(bounds, ref.bounds)
                && strategy.equals(ref.strategy)
                && random.equals(ref.random)
                && prototype.equals(ref.prototype);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.bounds) ^ (Double.doubleToLongBits(this.bounds) >>> 32));
        hash = 67 * hash + (this.prototype != null ? this.prototype.hashCode() : 0);
        hash = 67 * hash + (this.strategy != null ? this.strategy.hashCode() : 0);
        hash = 67 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: prototype=%s, strategy=%s,bounds=%f,  random=%s]", this.getClass().getSimpleName(), prototype.toString(), strategy.toString(), bounds, random.toString());
    }
    // </editor-fold>
    
    public static class Strategy {
        public static abstract class TranformationStrategy {
            public abstract TranslatedDoubleObjective getNewInstance(ObjectiveFunction<DoubleVectorPhenotype> objective, Random random);
            @Override public abstract boolean equals(Object o);
            @Override public abstract int hashCode();
            @Override public abstract String toString();
        }
        
        public static class RandomOffsetCubic extends TranformationStrategy {
            private final double bounds;
            
            public RandomOffsetCubic(final double bounds) { this.bounds = bounds; }
            
            @Override public TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorPhenotype> objective, final Random random) {
                final double[] offset = new double[objective.getNumDimensions()];
                for (int j = 0; j < offset.length; j++)
                    offset[j] = (2*random.nextDouble() -1)*bounds;
                return new TranslatedDoubleObjective(offset, objective);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                return (o instanceof RandomOffsetCubic);
            }

            @Override
            public int hashCode() { return 5; }

            @Override
            public String toString() { return String.format("[%s]", this.getClass().getSimpleName());  }
        }
        
        private TranformationStrategy strategy;
        private Strategy(final TranformationStrategy strategy) { this.strategy = strategy; };
        public TranformationStrategy get() { return strategy; }
    }
    
}
