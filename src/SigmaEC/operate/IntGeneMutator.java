package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.IntGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric O. Scott
 */
public class IntGeneMutator extends Mutator<LinearGenomeIndividual<IntGene>, IntGene> {
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_DEFAULT_MIN = "defaultMin";
    public final static String P_DEFAULT_MAX = "defaultMax";
    public final static String P_MAXES = "maxes";
    public final static String P_MINS = "mins";
    public final static String P_NUM_DIMENSIONS = "dimensions";
    public final static String P_STOP_ON_CONSTRAINT_VIOLATION = "stopOnConstraintViolation";
    public final static int HARDBOUND_ATTEMPTS = 10000;

    private final double mutationRate;
    private final Random random;
    private final int[] maxes;
    private final int[] mins;
    private final Option<Constraint<LinearGenomeIndividual<IntGene>>> constraint;
    private final boolean stopOnConstraintViolation;
    
    public IntGeneMutator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        stopOnConstraintViolation = parameters.getOptionalBooleanParameter(Parameters.push(base, P_STOP_ON_CONSTRAINT_VIOLATION), false);
        mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        if (Double.isInfinite(mutationRate) || Double.isNaN(mutationRate))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (mutationRate < 0 || mutationRate > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be in the range [0, 1.0].", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (random == null)
            throw new IllegalStateException(String.format("%s: %s is null.", this.getClass().getSimpleName(), P_RANDOM));
        
        final Option<Integer> numDimensions = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        
        final Option<Integer> defaultMin = parameters.getOptionalIntParameter(Parameters.push(base, P_DEFAULT_MIN));
        final Option<int[]> minsOpt = parameters.getOptionalIntArrayParameter(Parameters.push(base, P_MINS));
        if (!(defaultMin.isDefined() ^ minsOpt.isDefined()))
            throw new IllegalStateException(String.format("%s: either '%s' or '%s' must be defined, and not both.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_MIN), Parameters.push(base, P_MINS)));
        if (defaultMin.isDefined()) {
            if (!numDimensions.isDefined())
                            throw new IllegalStateException(String.format("%s: when using '%s', '%s' must be defined.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_MIN), Parameters.push(base, P_NUM_DIMENSIONS)));
            mins = Misc.repeatValue(defaultMin.get(), numDimensions.get());
        }
        else
            mins = minsOpt.get();
        
        
        final Option<Integer> defaultMax = parameters.getOptionalIntParameter(Parameters.push(base, P_DEFAULT_MAX));
        final Option<int[]> maxesOpt = parameters.getOptionalIntArrayParameter(Parameters.push(base, P_MAXES));
        if (!(defaultMax.isDefined() ^ maxesOpt.isDefined()))
            throw new IllegalStateException(String.format("%s: either '%s' or '%s' must be defined, and not both.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_MAX), Parameters.push(base, P_MAXES)));
        if (defaultMax.isDefined()) {
            if (!numDimensions.isDefined())
                            throw new IllegalStateException(String.format("%s: when using '%s', '%s' must be defined.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_MAX), Parameters.push(base, P_NUM_DIMENSIONS)));
            maxes = Misc.repeatValue(defaultMax.get(), numDimensions.get());
        }
        else
            maxes = maxesOpt.get();
        
        assert(repOK());
    }

    public IntGeneMutator(final double mutationRate, final Random random, final int[] mins, final int[] maxes, final Option<Constraint<LinearGenomeIndividual<IntGene>>> constraint, final boolean stopOnConstraintViolation) {
        assert(Double.isFinite(mutationRate));
        assert(random != null);
        assert(mins != null);
        assert(maxes != null);
        assert(mins.length == maxes.length);
        assert(Misc.arrayLessThanOrEqualTo(mins, maxes));
        assert(constraint != null);
        this.mutationRate = mutationRate;
        this.random = random;
        this.mins = Arrays.copyOf(mins, mins.length);
        this.maxes = Arrays.copyOf(maxes, maxes.length);
        this.constraint = constraint;
        this.stopOnConstraintViolation = stopOnConstraintViolation;
        assert(repOK());
    }
    
    /** Takes an individual and produces a version of that individual with zero or more mutated genes.
     * 
     * The new individual has integer reset mutation applied to each gene with
     * probability P_MUTATION_RATE.
     * 
     * If the old individual has parents, then
     * the new individual's parents are the same the original's parents---that
     * is, ind is not considered the parent of the mutated individual.
     * 
     * If ind's parents attribute is empty, however, then the new individual
     * considered ind to be its parent.
     * 
     * This way, an offspring individual has its parents assigned by the first
     * reproductive operator that is applied to its parents.
     * 
     * @param ind The original individual.
     * @return The newly mutated individual.
     */
    @Override
    public LinearGenomeIndividual<IntGene> mutate(final LinearGenomeIndividual<IntGene> ind) {
        assert(ind != null);
        final List<IntGene> genome = ind.getGenome();
        final List<IntGene> newGenome = new ArrayList<IntGene>();
        for (int i = 0; i < genome.size(); i++) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate) ? mutate(genome.get(i), i, ind) : genome.get(i));
        }
        final List<Individual> parents = ind.hasParents() ?
                ind.getParents().get() :
                new ArrayList<Individual>() {{ add(ind); }};
        final LinearGenomeIndividual<IntGene> result = ind.create(newGenome, parents);
        assert(repOK());
        assert(!(constraint.isDefined() && constraint.get().isViolated(result)));
        return result;
    }
    
    private IntGene mutate(final IntGene gene, int i, final LinearGenomeIndividual<IntGene> ind) {
        assert(gene != null);
        assert(i >= 0);
        assert(i < maxes.length);
        assert(i < mins.length);
        if (maxes[i] - mins[i] == 0)
            return gene;
        if (!constraint.isDefined())
            return new IntGene(random.nextInt(maxes[i] - mins[i]) + mins[i]);
        for (int attempt = 0; attempt < HARDBOUND_ATTEMPTS; attempt++) {
            final IntGene newGene = new IntGene(random.nextInt(maxes[i] - mins[i]) + mins[i]);
            if (!constraint.get().isViolated(buildIndividualWithGene(ind, newGene, i)))
                return newGene;
            else if (stopOnConstraintViolation)
                throw new IllegalStateException(String.format("%s: unexpected constraint violation.", this.getClass().getSimpleName()));
        }
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,
                String.format("After attempting %d integer reset mutations the range [%d, %d] for the %dth gene, no valid gene value was found satisfying the specified constraints.",
                HARDBOUND_ATTEMPTS, mins[i], maxes[i], i));
        return gene; // Give up
    }
    
    private LinearGenomeIndividual<IntGene> buildIndividualWithGene(final LinearGenomeIndividual<IntGene> ind, IntGene gene, final int i) {
        assert(ind != null);
        assert(gene != null);
        assert(i >= 0);
        assert(i < ind.size());
        final List<IntGene> newGenome = ind.getGenome();
        newGenome.set(i, gene);
        return ind.create(newGenome, new ArrayList());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_CONSTRAINT != null
                && !P_CONSTRAINT.isEmpty()
                && P_DEFAULT_MAX != null
                && !P_DEFAULT_MAX.isEmpty()
                && P_DEFAULT_MIN != null
                && !P_DEFAULT_MIN.isEmpty()
                && P_MAXES != null
                && !P_MAXES.isEmpty()
                && P_MINS != null
                && !P_MINS.isEmpty()
                && P_MUTATION_RATE != null
                && !P_MUTATION_RATE.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && mutationRate >= 0
                && !Double.isInfinite(mutationRate)
                && !Double.isNaN(mutationRate)
                && random != null
                && maxes != null
                && mins != null
                && maxes.length == mins.length
                && Misc.arrayLessThanOrEqualTo(mins, maxes)
                && constraint != null
                && !(stopOnConstraintViolation && !constraint.isDefined());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IntGeneMutator))
            return false;
        final IntGeneMutator ref = (IntGeneMutator)o;
        return Misc.doubleEquals(mutationRate, ref.mutationRate)
                && random.equals(ref.random)
                && Arrays.equals(maxes, ref.maxes)
                && Arrays.equals(mins, ref.mins)
                && constraint.equals(ref.constraint);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 53 * hash + Objects.hashCode(this.random);
        hash = 53 * hash + Arrays.hashCode(this.maxes);
        hash = 53 * hash + Arrays.hashCode(this.mins);
        hash = 53 * hash + Objects.hashCode(this.constraint);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%f, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MUTATION_RATE, mutationRate,
                P_RANDOM, random,
                P_MAXES, maxes,
                P_MINS, mins,
                P_CONSTRAINT, constraint);
    }
    // </editor-fold>
}
