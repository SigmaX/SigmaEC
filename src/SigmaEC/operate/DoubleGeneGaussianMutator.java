package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.operate.constraint.Constraint;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.DoubleGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
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
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneGaussianMutator extends Mutator<LinearGenomeIndividual<DoubleGene, ?>> {
    public final static String P_GAUSSIAN_STD = "gaussianStd";
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    public final static String P_CONSTRAINT = "constraint";
    public final static String P_DEFAULT_MAX_VALUE = "defaultMaxValue";
    public final static String P_DEFAULT_MIN_VALUE = "defaultMinValue";
    public final static String P_MAX_VALUES = "maxValues";
    public final static String P_MIN_VALUES = "minValues";
    public final static String P_NUM_DIMENSIONS = "dimensions";
    public final static String P_STOP_ON_CONSTRAINT_VIOLATION = "stopOnConstraintViolation";
    public final static int HARDBOUND_ATTEMPTS = 10000;
    
    private final MutationRate mutationRate;
    private final Random random;
    private final double gaussianStd;
    private final Option<double[]> maxValues;
    private final Option<double[]> minValues;
    private final Option<Constraint<LinearGenomeIndividual<DoubleGene, ?>>> constraint;
    private final boolean stopOnConstraintViolation;
    
    public DoubleGeneGaussianMutator(final Parameters parameters, final String base) {
        this.gaussianStd = parameters.getDoubleParameter(Parameters.push(base, P_GAUSSIAN_STD));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        constraint = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_CONSTRAINT), Constraint.class);
        stopOnConstraintViolation = parameters.getOptionalBooleanParameter(Parameters.push(base, P_STOP_ON_CONSTRAINT_VIOLATION), true);
        mutationRate = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATION_RATE), MutationRate.class);
        if (Double.isInfinite(gaussianStd) || Double.isNaN(gaussianStd))
            throw new IllegalStateException(String.format("%s: %s is infinite, must be finite.", this.getClass().getSimpleName(), P_GAUSSIAN_STD));
        if (gaussianStd <= 0)
            throw new IllegalStateException(String.format("%s: %s is <= 0, must be positive.", this.getClass().getSimpleName(), P_GAUSSIAN_STD));
        if (random == null)
            throw new IllegalStateException(String.format("%s: %s is null.", this.getClass().getSimpleName(), P_RANDOM));
        
        final Option<Integer> numDimensions = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        final boolean hardBounds = parameters.isDefined(Parameters.push(base, P_DEFAULT_MAX_VALUE))
                || parameters.isDefined(Parameters.push(base, P_DEFAULT_MIN_VALUE))
                || parameters.isDefined(Parameters.push(base, P_MIN_VALUES))
                || parameters.isDefined(Parameters.push(base, P_MAX_VALUES));
        if (hardBounds) {

            if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MAX_VALUE))) {
                if (!numDimensions.isDefined())
                    throw new IllegalStateException(String.format("%s: When using default hard bounds (%s and/or %s), %s must be defined.", this.getClass().getSimpleName(), P_MAX_VALUES, P_MIN_VALUES, P_DEFAULT_MAX_VALUE, P_DEFAULT_MIN_VALUE, P_NUM_DIMENSIONS));
                maxValues = new Option<double[]>(Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MAX_VALUE)), numDimensions.get()));
            }
            else
                maxValues = new Option<double[]>(parameters.getDoubleArrayParameter(Parameters.push(base, P_MAX_VALUES)));

            if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MIN_VALUE))) {
                
            if (!numDimensions.isDefined())
                throw new IllegalStateException(String.format("%s: When using default hard bounds (%s and/or %s), %s must be defined.", this.getClass().getSimpleName(), P_MAX_VALUES, P_MIN_VALUES, P_DEFAULT_MAX_VALUE, P_DEFAULT_MIN_VALUE, P_NUM_DIMENSIONS));
                minValues = new Option<double[]>(Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MIN_VALUE)), numDimensions.get()));
            }
            else
                minValues = new Option<double[]>(parameters.getDoubleArrayParameter(Parameters.push(base, P_MIN_VALUES)));
        }
        else {
            maxValues = Option.NONE;
            minValues = Option.NONE;
        }
        assert(repOK());
    }
    
    private boolean usingHardBounds() {
        return maxValues.isDefined();
    }

    /** Takes an individual and produces a version of that individual with zero or more mutated genes.
     * 
     * The new individual has  Gaussian mutation applied to each gene with
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
    public LinearGenomeIndividual<DoubleGene, ?> mutate(final LinearGenomeIndividual<DoubleGene, ?> ind, final int step) {
        assert(ind != null);
        final List<DoubleGene> genome = ind.getGenome();
        final List<DoubleGene> newGenome = new ArrayList<>();
        for (int i = 0; i < genome.size(); i++) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate.getRateForGene(i, step, ind)) ? mutate(ind, genome.get(i), i) : genome.get(i));
        }
        assert(repOK());
        final List<? extends Individual> parents = ind.hasParents() ?
                ind.getParents().get() :
                new ArrayList<Individual>() {{ add(ind); }};
        return ind.create(newGenome, parents);
    }
    
    private DoubleGene mutate(final LinearGenomeIndividual<DoubleGene, ?> ind, final DoubleGene gene, final int i) {
        if (!usingHardBounds() && !constraint.isDefined())
            return new DoubleGene(gene.value + Misc.gaussianSample(random)*gaussianStd);
        for (int attempt = 0; attempt < HARDBOUND_ATTEMPTS; attempt++) {
            final double newValue = gene.value + Misc.gaussianSample(random)*gaussianStd;
            if (withinBoundsForGene(newValue, i) && !constraintIsViolated(ind, newValue, i)) {
                if (attempt > 0)
                    Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, String.format("Valid individual generated after %d mutation attempts.", attempt));
                return new DoubleGene(newValue);
            }
        }
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,
                String.format("Failed to find a valid gene value after attempting %d Gaussian mutations on a single gene.", HARDBOUND_ATTEMPTS));
        if (stopOnConstraintViolation)
            throw new IllegalStateException(String.format("%s: unexpected constraint violation.", this.getClass().getSimpleName()));
        return gene; // Give up
    }
    
    private boolean withinBoundsForGene(final double value, final int i) {
        assert(i >= 0);
        return !usingHardBounds() || 
                (value <= maxValues.get()[i] && value >= minValues.get()[i]);
    }
    
    private boolean constraintIsViolated(final LinearGenomeIndividual<DoubleGene, ?> ind, final double gene, final int i) {
        assert(ind != null);
        assert(i >= 0);
        return constraint.isDefined()
                && constraint.get().isViolated(buildIndividualWithGene(ind, new DoubleGene(gene), i));
    }
    
    private LinearGenomeIndividual<DoubleGene, ?> buildIndividualWithGene(final LinearGenomeIndividual<DoubleGene, ?> ind, final DoubleGene gene, final int i) {
        assert(ind != null);
        assert(gene != null);
        assert(i >= 0);
        assert(i < ind.size());
        final List<DoubleGene> newGenome = ind.getGenome();
        newGenome.set(i, gene);
        return ind.create(newGenome, new ArrayList());
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_MUTATION_RATE != null
                && !P_MUTATION_RATE.isEmpty()
                && P_GAUSSIAN_STD != null
                && !P_GAUSSIAN_STD.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_CONSTRAINT != null
                && !P_CONSTRAINT.isEmpty()
                && P_STOP_ON_CONSTRAINT_VIOLATION != null
                && !P_STOP_ON_CONSTRAINT_VIOLATION.isEmpty()
                && random != null
                && mutationRate != null
                && !Double.isNaN(gaussianStd)
                && !Double.isInfinite(gaussianStd)
                && gaussianStd > 0;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%s, %s=%f, %s=%B, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MUTATION_RATE, mutationRate,
                P_GAUSSIAN_STD, gaussianStd,
                P_STOP_ON_CONSTRAINT_VIOLATION, stopOnConstraintViolation,
                P_CONSTRAINT, constraint,
                P_RANDOM, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneGaussianMutator))
            return false;
        
        DoubleGeneGaussianMutator ref = (DoubleGeneGaussianMutator) o;
        return Misc.doubleEquals(gaussianStd, ref.gaussianStd)
                && stopOnConstraintViolation == ref.stopOnConstraintViolation
                && constraint.equals(ref.constraint)
                && mutationRate.equals(ref.mutationRate)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.mutationRate);
        hash = 47 * hash + Objects.hashCode(this.random);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.gaussianStd) ^ (Double.doubleToLongBits(this.gaussianStd) >>> 32));
        hash = 47 * hash + Objects.hashCode(this.constraint);
        hash = 47 * hash + (this.stopOnConstraintViolation ? 1 : 0);
        return hash;
    }

    //</editor-fold>
}
