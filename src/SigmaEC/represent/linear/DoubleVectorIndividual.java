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
public class DoubleVectorIndividual extends LinearGenomeIndividual<DoubleGene> {
    private final List<DoubleGene> genome;
    private final long id;
    private static long nextId;
    private final Option<Double> fitness;
    
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
        fitness = Option.NONE;
        assert(repOK());
    }
    
    public DoubleVectorIndividual(final List<DoubleGene> genome) {
        if (genome == null)
            throw new NullPointerException(String.format("%s: genome is null.", this.getClass().getSimpleName()));
        if (Misc.containsNulls(genome))
            throw new IllegalArgumentException(String.format("%s: genome contains null values.", this.getClass().getSimpleName()));
        this.genome = new ArrayList<DoubleGene>(genome);
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }
    
    public DoubleVectorIndividual(final double[] genome) {
        if (genome == null)
            throw new NullPointerException(String.format("%s: genome is null.", this.getClass().getSimpleName()));
        if (!Misc.allFinite(genome))
            throw new IllegalArgumentException(String.format("%s: genome contains non-finite values.", this.getClass().getSimpleName()));
        this.genome = new ArrayList<DoubleGene>(genome.length);
        for (int i = 0; i < genome.length; i++)
            this.genome.add(new DoubleGene(genome[i]));
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }
    
    private DoubleVectorIndividual(final DoubleVectorIndividual ref, final double fitness) {
        assert(ref != null);
        genome = new ArrayList<DoubleGene>(ref.genome);
        id = ref.id;
        this.fitness = new Option<Double>(fitness);
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
    
    public double[] getGenomeArray() {
        final double[] array = new double[genome.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = genome.get(i).value;
        return array;
    }
    
    public double getElement(final int i) {
        assert(i >= 0);
        assert(i < genome.size());
        return genome.get(i).value;
    }

    @Override
    public double getFitness() {
        if (fitness.isDefined())
            return fitness.get();
        else
            throw new IllegalStateException(String.format("%s: attempted to read the fitness of an individual whose fitness has not been evaluated.", this.getClass().getSimpleName()));
    }

    @Override
    public DoubleVectorIndividual setFitness(double fitness) {
        return new DoubleVectorIndividual(this, fitness);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return id >= 0
                && genome != null
                && fitness != null
                && !(fitness.isDefined() && Double.isNaN(fitness.get()))
                && !Misc.containsNulls(genome);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorIndividual))
            return false;
        final DoubleVectorIndividual ref = (DoubleVectorIndividual) o;
        return id == ref.id
                && genome.equals(ref.genome)
                && fitness.equals(ref.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 97 * hash + (this.fitness != null ? this.fitness.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: id=%s, fitness=%s, genome=%s]", this.getID(), this.getClass().getSimpleName(), fitness, genome.toString());
    }
    
    // </editor-fold>
}
