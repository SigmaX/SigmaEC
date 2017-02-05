package SigmaEC.operate;

import SigmaEC.SRandom;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.DoubleGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A mutator that adds uniform noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneUniformMutator extends Mutator<LinearGenomeIndividual<DoubleGene>> {
    public final static String P_RANDOM = "random";
    public final static String P_MUTATION_RATE = "mutationRate";
    public final static String P_DEFAULT_WIDTH = "defaultWidth";
    public final static String P_WIDTHS = "widths";
    public final static String P_NUM_DIMENSIONS = "dimensions";
    
    private final double mutationRate;
    private final Random random;
    private final double[] widths;
    
    public DoubleGeneUniformMutator(final Parameters parameters, final String base) {
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        mutationRate = parameters.getDoubleParameter(Parameters.push(base, P_MUTATION_RATE));
        if (Double.isInfinite(mutationRate) || Double.isNaN(mutationRate))
            throw new IllegalStateException(String.format("%s: %s is %f, must be finite.", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (mutationRate < 0 || mutationRate > 1.0)
            throw new IllegalStateException(String.format("%s: %s is %f, must be in the range [0, 1.0].", this.getClass().getSimpleName(), P_MUTATION_RATE, mutationRate));
        if (random == null)
            throw new IllegalStateException(String.format("%s: %s is null.", this.getClass().getSimpleName(), P_RANDOM));
        
        final Option<Integer> numDimensions = parameters.getOptionalIntParameter(Parameters.push(base, P_NUM_DIMENSIONS));
        final Option<Double> defaultWidth = parameters.getOptionalDoubleParameter(Parameters.push(base, P_DEFAULT_WIDTH));
        final Option<double[]> widthsOpt = parameters.getOptionalDoubleArrayParameter(Parameters.push(base, P_WIDTHS));
        if (!(defaultWidth.isDefined() ^ widthsOpt.isDefined()))
            throw new IllegalStateException(String.format("%s: either '%s' or '%s' must be defined, and not both.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_WIDTH), Parameters.push(base, P_WIDTHS)));
        if (defaultWidth.isDefined()) {
            if (!numDimensions.isDefined())
                            throw new IllegalStateException(String.format("%s: when using '%s', '%s' must be defined.", this.getClass().getSimpleName(), Parameters.push(base, P_DEFAULT_WIDTH), Parameters.push(base, P_NUM_DIMENSIONS)));
            widths = Misc.repeatValue(defaultWidth.get(), numDimensions.get());
        }
        else
            widths = widthsOpt.get();
        assert(repOK());
    }

    /** Takes an individual and produces a version of that individual with zero or more mutated genes.
     * 
     * The new individual has  uniform mutation applied to each gene with
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
    public LinearGenomeIndividual<DoubleGene> mutate(final LinearGenomeIndividual<DoubleGene> ind, final int step) {
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
    
    private DoubleGene mutate(final DoubleGene gene, final int i) {
        return new DoubleGene(gene.value + random.nextDouble()*widths[i] - widths[i]/2);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return P_MUTATION_RATE != null
                && !P_MUTATION_RATE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_DEFAULT_WIDTH != null
                && !P_DEFAULT_WIDTH.isEmpty()
                && P_NUM_DIMENSIONS != null
                && !P_NUM_DIMENSIONS.isEmpty()
                && P_WIDTHS != null
                && !P_WIDTHS.isEmpty()
                && random != null
                && !Double.isInfinite(mutationRate)
                && !Double.isNaN(mutationRate)
                && mutationRate >= 0.0
                && mutationRate <= 1.0
                && widths != null
                && !Misc.containsNaNs(widths);
    }
    
    @Override
    final public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneUniformMutator))
            return false;
        final DoubleGeneUniformMutator ref = (DoubleGeneUniformMutator) o;
        return Misc.doubleArrayEquals(widths, ref.widths)
                && Misc.doubleEquals(mutationRate, ref.mutationRate)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 31 * hash + Objects.hashCode(this.random);
        hash = 31 * hash + Arrays.hashCode(this.widths);
        return hash;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s: %s=%f, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_MUTATION_RATE, mutationRate,
                P_WIDTHS, Arrays.toString(widths),
                P_RANDOM, random);
    }
    //</editor-fold>
}
