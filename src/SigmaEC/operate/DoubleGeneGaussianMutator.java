package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.DoubleGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneGaussianMutator extends Mutator<LinearGenomeIndividual<DoubleGene>, DoubleGene> {
    public final static String P_GAUSSIAN_STD = "gaussianStd";
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    public final static String P_DEFAULT_MAX_VALUE = "defaultMaxValue";
    public final static String P_DEFAULT_MIN_VALUE = "defaultMinValue";
    public final static String P_MAX_VALUES = "maxValues";
    public final static String P_MIN_VALUES = "minValues";
    public final static String P_NUM_DIMENSIONS = "dimensions";
    public final static int HARDBOUND_ATTEMPTS = 10000;
    
    private final double mutationRate;
    private final Random random;
    private final double gaussianStd;
    private final Option<double[]> maxValues;
    private final Option<double[]> minValues;
    
    public DoubleGeneGaussianMutator(final Parameters parameters, final String base) {
        this.gaussianStd = parameters.getDoubleParameter(Parameters.push(base, P_GAUSSIAN_STD));
        this.random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        if (Double.isInfinite(mutationRate) || Double.isNaN(mutationRate))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (mutationRate < 0 || mutationRate > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be in the range [0, 1.0].", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
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
            if (!numDimensions.isDefined())
                throw new IllegalStateException(String.format("%s: Using hard bounds (%s, %s, %s and/or %s), but %s is not defined.", this.getClass().getSimpleName(), P_MAX_VALUES, P_MIN_VALUES, P_DEFAULT_MAX_VALUE, P_DEFAULT_MIN_VALUE, P_NUM_DIMENSIONS));

            if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MAX_VALUE)))
                maxValues = new Option<double[]>(Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MAX_VALUE)), numDimensions.get()));
            else
                maxValues = new Option<double[]>(parameters.getDoubleArrayParameter(Parameters.push(base, P_MAX_VALUES)));

            if (parameters.isDefined(Parameters.push(base, P_DEFAULT_MIN_VALUE)))
                minValues = new Option<double[]>(Misc.repeatValue(parameters.getDoubleParameter(Parameters.push(base, P_DEFAULT_MIN_VALUE)), numDimensions.get()));
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
    
    private DoubleGene mutate(final DoubleGene gene, final int i) {
        if (!usingHardBounds())
            return new DoubleGene(gene.value + Misc.gaussianSample(random)*gaussianStd);
        for (int attempt = 0; attempt < HARDBOUND_ATTEMPTS; attempt++) {
            final double newValue = gene.value + Misc.gaussianSample(random)*gaussianStd;
            if (newValue <= maxValues.get()[i] && newValue >= minValues.get()[i])
                return new DoubleGene(newValue);
        }
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING,
                String.format("Failed to find a valid gene value (i.e. on the range [%f, %f]) after attempting %d Gaussian mutations on a single gene.",
                minValues.get()[i], maxValues.get()[i], HARDBOUND_ATTEMPTS));
        return gene; // Give up
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
    public LinearGenomeIndividual<DoubleGene> mutate(final LinearGenomeIndividual<DoubleGene> ind) {
        assert(ind != null);
        final List<DoubleGene> genome = ind.getGenome();
        final List<DoubleGene> newGenome = new ArrayList<DoubleGene>();
        for (int i = 0; i < genome.size(); i++) {
            double roll = random.nextDouble();
            newGenome.add((roll < mutationRate) ? mutate(genome.get(i), i) : genome.get(i));
        }
        assert(repOK());
        final List<Individual> parents = ind.hasParents() ?
                ind.getParents().get() :
                new ArrayList<Individual>() {{ add(ind); }};
        return ind.create(newGenome, parents);
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
                && random != null
                && !Double.isInfinite(mutationRate)
                && !Double.isNaN(mutationRate)
                && mutationRate >= 0.0
                && mutationRate <= 1.0
                && !Double.isNaN(gaussianStd)
                && !Double.isInfinite(gaussianStd)
                && gaussianStd > 0;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%f, %s=%f, %s=%s]", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate, P_GAUSSIAN_STD, gaussianStd, P_RANDOM, random);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneGaussianMutator))
            return false;
        
        DoubleGeneGaussianMutator ref = (DoubleGeneGaussianMutator) o;
        return Misc.doubleEquals(gaussianStd, ref.gaussianStd)
                && Misc.doubleEquals(mutationRate, ref.mutationRate)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 23 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.gaussianStd) ^ (Double.doubleToLongBits(this.gaussianStd) >>> 32));
        return hash;
    }
    //</editor-fold>
}
