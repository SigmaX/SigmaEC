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
    private final long id;
    private static long nextId;
    
    /** Construct a random double vector.
     * 
     * @param random PRNG
     * @param numDimensions Length of the double vector.
     * @param pTrue Probability that any given bit is assigned a value of T (as opposed to F).
     */
    public DoubleVectorIndividual(final Random random, final int numDimensions, final double[] minValues, final double[] maxValues) {
        assert(random != null);
        assert(numDimensions > 0);
        assert(minValues != null);
        assert(maxValues != null);
        assert(minValues.length == numDimensions);
        assert(maxValues.length == numDimensions);
        this.genome = new ArrayList<DoubleGene>(numDimensions) {{
           for (int i = 0; i < numDimensions; i++) {
               final double delta = maxValues[i] - minValues[i];
               assert(delta >= 0);
               final double roll = minValues[i] + (random.nextDouble()*delta);
               add(new DoubleGene(roll));
           } 
        }};
        this.id = nextId++;
        assert(repOK());
    }
    
    public DoubleVectorIndividual(final List<DoubleGene> genome) {
        assert(genome != null);
        this.genome = new ArrayList<DoubleGene>(genome);
        this.id = nextId++;
        assert(repOK());
    }

    @Override
    public long getID() { return id; }
    
    @Override
    public int size() { return genome.size(); }
    
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
        return String.format("[%s: id=%s, genome=%s]", this.getID(), this.getClass().getSimpleName(), genome.toString());
    }
    
    // </editor-fold>
    
}
