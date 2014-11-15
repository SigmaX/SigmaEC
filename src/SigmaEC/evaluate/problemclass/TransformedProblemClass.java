package SigmaEC.evaluate.problemclass;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.evaluate.transform.TranslatedDoubleObjective;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Random;

/**
 * An problem class that produces new problem instances by applying a
 * random transformation to a prototype function.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TransformedProblemClass extends ProblemClass<TranslatedDoubleObjective, DoubleVectorIndividual> {
    public final static String P_TYPE = "transformationType";
    public final static String V_RANDOM = "randomOffset";
    public final static String P_PROTOTYPE = "prototype";
    
    private final ObjectiveFunction<DoubleVectorIndividual> prototype;
    private final Strategy.TransformationStrategy strategy;
    
    public TransformedProblemClass(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        prototype = parameters.getInstanceFromParameter(Parameters.push(base, P_PROTOTYPE), ObjectiveFunction.class);
        final String tValue = parameters.getStringParameter(Parameters.push(base, P_TYPE));
        if (tValue.equals(V_RANDOM)) {
            strategy = new Strategy.RandomOffset(parameters, Parameters.push(base, P_TYPE));
        }
        else
            throw new IllegalStateException(String.format("%s: unrecognized %s, '%s'.", this.getClass().getSimpleName(), P_TYPE, tValue));
        assert(repOK());
    }
    
    public TransformedProblemClass(final ObjectiveFunction<DoubleVectorIndividual> prototype, final Strategy.TransformationStrategy strategy) {
        if (prototype == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": prototype was null.");
        this.prototype = prototype;
        this.strategy = strategy;
        assert(repOK());
    }
    
    @Override
    public TranslatedDoubleObjective getNewInstance() {
        return strategy.getNewInstance(prototype);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return prototype != null
                && strategy != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TransformedProblemClass))
            return false;
        final TransformedProblemClass ref = (TransformedProblemClass) o;
        return strategy.equals(ref.strategy)
                && prototype.equals(ref.prototype);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.prototype != null ? this.prototype.hashCode() : 0);
        hash = 67 * hash + (this.strategy != null ? this.strategy.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: prototype=%s, strategy=%s]", this.getClass().getSimpleName(), prototype.toString(), strategy.toString());
    }
    // </editor-fold>
    
    public static class Strategy {
        public static abstract class TransformationStrategy {
            public abstract TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorIndividual> objective);
            public abstract boolean repOK();
            @Override public abstract boolean equals(final Object o);
            @Override public abstract int hashCode();
            @Override public abstract String toString();
        }
        
        public static class RandomOffset extends TransformationStrategy {
            public final static String P_MAX_OFFSET = "maxOffset";
            public final static String P_RANDOM = "random";
            private final double boundsWidth;
            private final Random random;
            
            public RandomOffset(final Parameters parameters, final String base) {
                assert(parameters != null);
                assert(base != null);
                boundsWidth = parameters.getDoubleParameter(Parameters.push(base, P_MAX_OFFSET));
                if (Double.isNaN(boundsWidth) || Double.isInfinite(boundsWidth))
                    throw new IllegalStateException(String.format("%s: boundsWidth is %f, must be finite.", this.getClass().getSimpleName(), boundsWidth));
                random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
                assert(repOK());
            }
            
            @Override public TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorIndividual> objective) {
                assert(objective != null);
                assert(random != null);
                final double[] offset = new double[objective.getNumDimensions()];
                for (int j = 0; j < offset.length; j++)
                    offset[j] = (2*random.nextDouble() -1)*boundsWidth;
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
                return random.equals(ref.random)
                        && Misc.doubleEquals(boundsWidth, ref.boundsWidth);
            }

            @Override
            public int hashCode() {
                int hash = 3;
                hash = 47 * hash + (int) (Double.doubleToLongBits(this.boundsWidth) ^ (Double.doubleToLongBits(this.boundsWidth) >>> 32));
                hash = 47 * hash + (this.random != null ? this.random.hashCode() : 0);
                return hash;
            }

            @Override
            public String toString() { return String.format("[%s: boundsWidth=%f, random=%s]", this.getClass().getSimpleName(), boundsWidth, random);  }

            @Override
            public final boolean repOK() {
                return !Double.isNaN(boundsWidth)
                        && !Double.isInfinite(boundsWidth)
                        && boundsWidth > 0
                        && random != null;
            }
            //</editor-fold>
        }
        
        /*
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
            public TranslatedDoubleObjective getNewInstance(final ObjectiveFunction<DoubleVectorIndividual> objective, final Random random) {
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
        */
    }
    
}
