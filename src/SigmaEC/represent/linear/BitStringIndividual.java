package SigmaEC.represent.linear;

import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringIndividual extends LinearGenomeIndividual<BitGene> {
    private final List<BitGene> genome;
    private final long id;
    private static long nextId = 0;
    private final Option<Double> fitness;
    private final Option<List<Individual>> parents;
    
    // <editor-fold defaultstate="collapsed" desc="Producers and Consumers">
    @Override
    public long getID() { return id; }
    
    @Override
    public int size() { return genome.size(); }

    @Override
    public double getFitness() {
        if (fitness.isDefined())
            return fitness.get();
        else
            throw new IllegalStateException(String.format("%s: attempted to read the fitness of an individual whose fitness has not been evaluated.", this.getClass().getSimpleName()));
    }
    
    @Override
    public BitStringIndividual setFitness(double fitness) {
        return new Builder(this).setFitness(fitness).build();
    }
    
    @Override
    public LinearGenomeIndividual<BitGene> create(final List<BitGene> genome, final List<? extends Individual> parents) {
        assert(genome != null);
        return new Builder(genome).setParents(parents).build();
    }

    @Override
    public List<BitGene> getGenome() {
        return new ArrayList<BitGene>(genome); // Defensive copy
    }
    
    public boolean getElement(final int i) {
        assert(i >= 0);
        assert(i < genome.size());
        return genome.get(i).value;
    }

    @Override
    public boolean hasParents() {
        return parents.isDefined();
    }

    @Override
    public Option<List<Individual>> getParents() {
        if (!parents.isDefined())
            return Option.NONE;
        return new Option<>((List<Individual>)new ArrayList<>(parents.get())); // Defensive copy
    }

    @Override
    public Individual setParents(List<? extends Individual> parents) {
       return new Builder(this).setParents(parents).build();
    }

    @Override
    public Individual clearParents() {
        return new Builder(this).clearParents().build();
    }
    // </editor-fold>
    
    public static class Builder {
        private List<BitGene> genome;
        private Option<Double> fitness = Option.NONE;
        private Option<List<Individual>> parents = Option.NONE;
        
        public BitStringIndividual build() {
            return new BitStringIndividual(genome, fitness, parents);
        }
        
        public Builder(final List<BitGene> genome) {
            assert(genome != null);
            assert(!Misc.containsNulls(genome));
            this.genome = new ArrayList<>(genome); /// Shallow copy okay because genes are immutable
        }
        
        public Builder(final boolean[] genome) {
            assert(genome != null);
            this.genome = new ArrayList<>(genome.length);
            for (int i = 0; i < genome.length; i++)
                this.genome.add(new BitGene(genome[i]));
        }
        
        public Builder(final BitStringIndividual ref) {
            assert(ref != null);
            genome = ref.genome;
            fitness = ref.fitness;
            parents = ref.parents;
        }
        
        public Builder setFitness(final double fitness) {
            this.fitness = new Option<>(fitness);
            return this;
        }
        
        public Builder setParents(final List<? extends Individual> parents) {
            assert(parents != null);
            assert(!Misc.containsNulls(parents));
            this.parents = new Option<>((List<Individual>)new ArrayList<>(parents));
            return this;
        }
        
        public Builder clearFitness() {
            fitness = Option.NONE;
            return this;
        }
        
        public Builder clearParents() {
            parents = Option.NONE;
            return this;
        }
    }
    
    /** Construct a random bitstring with an equal probability that any given
     * bit is T or F.
     * 
     * @param random PRNG
     * @param numBits Length of the bitstring.
     */
    public BitStringIndividual(final Random random, final int numBits) {
        this(random, numBits, 0.5);
    }
    
    /** Construct a random bitstring.
     * 
     * @param random PRNG
     * @param numBits Length of the bitstring.
     * @param pTrue Probability that any given bit is assigned a value of T (as opposed to F).
     */
    public BitStringIndividual(final Random random, final int numBits, final double pTrue) {
        this.genome = new ArrayList<BitGene>(numBits) {{
           for (int i = 0; i < numBits; i++) {
               final double roll = random.nextDouble();
               add(new BitGene(roll < pTrue));
           } 
        }};
        this.id = nextId++;
        fitness = Option.NONE;
        parents = Option.NONE;
        assert(repOK());
    }
    
    
    /** Private constructor for use with the Builder pattern. Does not make defensive copies! */
    private BitStringIndividual(final List<BitGene> genome, final Option<Double> fitness, final Option<List<Individual>> parents) {
        assert(genome != null);
        assert(!Misc.containsNulls(genome));
        assert(fitness != null);
        assert(parents != null);
        assert(!(parents.isDefined() && Misc.containsNulls(parents.get())));
        this.genome = new ArrayList<>(genome);
        this.id = nextId++;
        this.fitness = fitness;
        this.parents = parents;
        assert(repOK());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return id >= 0
                && fitness != null
                && !(parents.isDefined() && parents.get().isEmpty())
                && !(fitness.isDefined() && Double.isNaN(fitness.get()))
                && genome != null
                && !Misc.containsNulls(genome);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitStringIndividual))
            return false;
        final BitStringIndividual ref = (BitStringIndividual) o;
        return id == ref.id
                && genome.equals(ref.genome)
                && fitness.equals(ref.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 17 * hash + (this.fitness != null ? this.fitness.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: id=%d, fitness=%s, genome=%s]", this.getClass().getSimpleName(), this.getID(), fitness, genome.toString());
    }
    
    // </editor-fold>
    
}
