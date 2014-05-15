package SigmaEC.operate;

import SigmaEC.operate.Mutator.MutatorBuilder;
import SigmaEC.represent.Gene;
import SigmaEC.represent.Individual;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Takes a population of LinearGenomeIndividuals and mutates them according to
 * some per-gene mutation rate.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MutatingGenerator<T extends LinearGenomeIndividual<G>, G extends Gene> extends Generator<T>
{
    private final double mutationRate;
    private final Mutator<G> mutator;
    private final Random random;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    private MutatingGenerator(final Builder<T, G> builder)
    {
        this.mutationRate = builder.mutationRate;
        this.mutator = builder.mutatorBuilder.build();
        this.random = builder.random;
        assert(repOK());
    }
    
    public static class Builder<T extends LinearGenomeIndividual<G>, G extends Gene> implements GeneratorBuilder<T> {
        private final static String P_MUTATION_RATE = "mutationRate";
        private final static String P_MUTATOR = "mutator";
        
        private double mutationRate;
        private MutatorBuilder<G> mutatorBuilder;
        private Random random;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            mutationRate = Parameters.getDoubleParameter(properties, Parameters.push(base, P_MUTATION_RATE));
            mutatorBuilder = Parameters.getBuilderFromParameter(properties, Parameters.push(base, P_MUTATOR), Mutator.class);
        }
        
        @Override
        public Builder<T, G> random(final Random random) {
            assert(random != null);
            this.random = random;
            mutatorBuilder = mutatorBuilder.random(random);
            return this;
        }

        @Override
        public MutatingGenerator<T, G> build() {
            if (random == null)
                throw new IllegalStateException(this.getClass().getSimpleName() + ": trying to build before random has been initialized.");
            return new MutatingGenerator<T, G>(this);
        }
        
    }

    @Override
    public List<T> produceGeneration(final List<T> parentPopulation) {
        final List<T> newPopulation = new ArrayList<T>(parentPopulation.size());
        for(int i = 0; i < parentPopulation.size(); i++) {
            final T individual = parentSelector.selectIndividual(parentPopulation);
            final List<G> genome = individual.getGenome();
            final List<G> newGenome = new ArrayList<G>(genome.size());
            for (final G g : genome) {
                double roll = random.nextDouble();
                newGenome.add((roll < mutationRate) ? mutator.mutate(g) : g);
            }
            newPopulation.add((T) (parentPopulation.get(0).create(newGenome)));
        }
        return newPopulation;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return mutationRate >= 0.0
                && mutationRate <= 1.0
                && random != null
                && mutator != null
                && parentSelector != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: mutationRate=%.2f, random=%s, mutator=%s", this.getClass().getSimpleName(), mutationRate, random, mutator);
    }
    
    @Override
    public boolean equals(final Object o)
    {
        if (!(o instanceof MutatingGenerator))
            return false;
        
        MutatingGenerator cRef = (MutatingGenerator) o;
        return Misc.doubleEquals(mutationRate, cRef.mutationRate)
                && random.equals(cRef.random)
                && mutator.equals(cRef.mutator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.mutationRate) ^ (Double.doubleToLongBits(this.mutationRate) >>> 32));
        hash = 83 * hash + (this.mutator != null ? this.mutator.hashCode() : 0);
        hash = 83 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
