package SigmaEC.represent.linear;

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
    
    /** Construct a random bitstring with an equal probability that any given
     * bit is T or F.
     * 
     * @param random PRNG
     * @param numBits Length of the bitstring.
     */
    public BitStringIndividual(final Random random, final int numBits) {
        this(random, numBits, 0.5);
    }
    
    private BitStringIndividual(final BitStringIndividual ref, final double fitness) {
        assert(ref != null);
        genome = new ArrayList<BitGene>(ref.genome);
        id = ref.id;
        this.fitness = new Option<Double>(fitness);
        assert(repOK());
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
        assert(repOK());
    }
    
    public BitStringIndividual(final List<BitGene> genome) {
        assert(genome != null);
        this.genome = new ArrayList<BitGene>(genome);
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }

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
        return new BitStringIndividual(this, fitness);
    }
    
    @Override
    public LinearGenomeIndividual<BitGene> create(final List<BitGene> genome) {
        assert(genome != null);
        return new BitStringIndividual(genome);
    }

    @Override
    public List<BitGene> getGenome() {
        return new ArrayList<BitGene>(genome); // Defensive copy
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return id >= 0
                && fitness != null
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
