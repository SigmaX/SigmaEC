package SigmaEC.represent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class DoubleVectorIndividual extends LinearGenomeIndividual<DoubleGene> {
    private final List<DoubleGene> genome;
    
    /** Construct a random double vector.
     * 
     * @param random PRNG
     * @param numBits Length of the double vector.
     * @param pTrue Probability that any given bit is assigned a value of T (as opposed to F).
     */
    public DoubleVectorIndividual(final Random random, final int numBits, final double bound) {
        this.genome = new ArrayList<DoubleGene>(numBits) {{
           for (int i = 0; i < numBits; i++) {
               final double roll = (2*random.nextDouble() - 1)*bound;
               add(new DoubleGene(roll));
           } 
        }};
        assert(repOK());
    }
    
    public DoubleVectorIndividual(final List<DoubleGene> genome) {
        assert(genome != null);
        this.genome = new ArrayList<DoubleGene>(genome);
        assert(repOK());
    }
    
    @Override
    public LinearGenomeIndividual<DoubleGene> create(final List<DoubleGene> genome) {
        assert(genome != null);
        return new DoubleVectorIndividual(genome);
    }

    @Override
    public List<DoubleGene> getGenome() {
        return new ArrayList<DoubleGene>(genome); // Defensive copy
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return genome != null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorIndividual))
            return false;
        final DoubleVectorIndividual ref = (DoubleVectorIndividual) o;
        return this.genome.equals(ref.genome);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: genome=%s]", this.getClass().getSimpleName(), genome.toString());
    }
    
    // </editor-fold>
    
}
