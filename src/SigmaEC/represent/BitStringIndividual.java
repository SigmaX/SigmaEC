package SigmaEC.represent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitStringIndividual extends LinearGenomeIndividual<BitGene> {
    private final List<BitGene> genome;
    
    
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
        assert(repOK());
    }
    
    public BitStringIndividual(final List<BitGene> genome) {
        assert(genome != null);
        this.genome = new ArrayList<BitGene>(genome);
        assert(repOK());
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
        return genome != null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitStringIndividual))
            return false;
        final BitStringIndividual ref = (BitStringIndividual) o;
        return this.genome.equals(ref.genome);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: genome=%s]", this.getClass().getSimpleName(), genome.toString());
    }
    
    // </editor-fold>
    
}
