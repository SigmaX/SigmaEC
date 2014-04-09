package SigmaEC.evaluate;

import SigmaEC.evaluate.decorate.TranslatedDoubleObjective;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import SigmaEC.util.math.Vector;
import java.util.Arrays;
import java.util.Random;

/**
 * An objective generator that produces new problem instances by applying a
 * random transformation to a prototype function.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TransformedObjectiveGenerator extends ObjectiveGenerator<TranslatedDoubleObjective, DoubleVectorPhenotype> {
    private final ObjectiveFunction<DoubleVectorPhenotype> prototype;
    private final Strategy.TransformationStrategy strategy;
    private final Random random;
    
    public TransformedObjectiveGenerator(final ObjectiveFunction<DoubleVectorPhenotype> objective, final Strategy.TransformationStrategy strategy, final Random random) {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective was null.");
        this.prototype = objective;
        this.strategy = strategy;
        this.random = random;
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
                && random != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransformedObjectiveGenerator))
            return false;
        final TransformedObjectiveGenerator ref = (TransformedObjectiveGenerator) o;
        return strategy.equals(ref.strategy)
                && random.equals(ref.random)
                && prototype.equals(ref.prototype);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.prototype != null ? this.prototype.hashCode() : 0);
        hash = 67 * hash + (this.strategy != null ? this.strategy.hashCode() : 0);
        hash = 67 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: prototype=%s, strategy=%s,  random=%s]", this.getClass().getSimpleName(), prototype.toString(), strategy.toString(), random.toString());
    }
    // </editor-fold>
    
    public static class Strategy {
        public static abstract class TransformationStrategy {
            public abstract TranslatedDoubleObjective getNewInstance(ObjectiveFunction<DoubleVectorPhenotype> objective, Random random);
            public abstract boolean repOK();
            @Override public abstract boolean equals(Object o);
            @Override public abstract int hashCode();
            @Override public abstract String toString();
        }
        
        public static class RandomOffset extends TransformationStrategy {
            private final double bounds;
            
            public RandomOffset(final double bounds) { this.bounds = bounds; assert(repOK()); }
            
            @Override public TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorPhenotype> objective, final Random random) {
                assert(objective != null);
                assert(random != null);
                final double[] offset = new double[objective.getNumDimensions()];
                for (int j = 0; j < offset.length; j++)
                    offset[j] = (2*random.nextDouble() -1)*bounds;
                return new TranslatedDoubleObjective(offset, objective);
            }
            
            // <editor-fold defaultstate="collapsed" desc="Standard Methods">
            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof RandomOffset))
                    return false;
                final RandomOffset ref = (RandomOffset) o;
                return Misc.doubleEquals(bounds, ref.bounds);
            }

            @Override
            public int hashCode() {
                int hash = 5;
                hash = 43 * hash + (int) (Double.doubleToLongBits(this.bounds) ^ (Double.doubleToLongBits(this.bounds) >>> 32));
                return hash;
            }

            @Override
            public String toString() { return String.format("[%s]", this.getClass().getSimpleName());  }

            @Override
            public final boolean repOK() {
                return !Double.isNaN(bounds) && !Double.isInfinite(bounds);
            }
            //</editor-fold>
        }
        
        public static class RandomLinearOffset extends TransformationStrategy {
            private final double[] offsetVector;
            private final double bounds;
            
            public RandomLinearOffset( final double bounds, final double[] offsetVector) {
                if (Double.isNaN(bounds) || Double.isInfinite(bounds))
                    throw new IllegalArgumentException(this.getClass().getSimpleName() + ": bounds isn't finite.");
                if (offsetVector == null)
                    throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offsetVector is null.");
                if (!Misc.allFinite(offsetVector))
                    throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offsetVector contains a non-finite value.");
                if (!Misc.doubleEquals(1.0, Vector.euclideanNorm(offsetVector)))
                    throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offsetVector is not of unit length.");
                this.bounds = bounds;
                this.offsetVector = Arrays.copyOf(offsetVector, offsetVector.length);
                assert(repOK());
            }

            @Override
            public TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorPhenotype> objective, final Random random) {
                assert(objective != null);
                assert(random != null);
                final double magnitude = (2*random.nextDouble() - 1)*bounds;
                final double[] offset = Vector.scalarTimesVector(magnitude, offsetVector);
                return new TranslatedDoubleObjective(offset, objective);
            }
            
            // <editor-fold defaultstate="collapsed" desc="Standard Methods">
            @Override
            public final boolean repOK() {
                return !Double.isNaN(bounds)
                        && !Double.isInfinite(bounds)
                        && offsetVector != null
                        && Misc.allFinite(offsetVector)
                        && Misc.doubleEquals(1.0, Vector.euclideanNorm(offsetVector));
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof RandomLinearOffset))
                    return false;
                final RandomLinearOffset ref = (RandomLinearOffset) o;
                return Misc.doubleEquals(bounds, ref.bounds)
                        && Misc.doubleArrayEquals(offsetVector, ref.offsetVector);
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 59 * hash + Arrays.hashCode(this.offsetVector);
                hash = 59 * hash + (int) (Double.doubleToLongBits(this.bounds) ^ (Double.doubleToLongBits(this.bounds) >>> 32));
                return hash;
            }

            @Override
            public String toString() {
                return String.format("[%s: offsetVector=%s, bounds=%f]", this.getClass().getSimpleName(), Arrays.toString(offsetVector), bounds);
            }
            //</editor-fold>
        }
    }
    
}
