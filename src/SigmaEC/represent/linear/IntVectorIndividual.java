package SigmaEC.represent.linear;

import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Eric O. Scott
 */
public class IntVectorIndividual extends LinearGenomeIndividual<IntGene> {
    private final List<IntGene> genome;
    private final long id;
    private static long nextId;
    private final Option<Double> fitness;
    
    /** Construct a random double vector.
     */
    public IntVectorIndividual(final Random random, final int numDimensions, final int[] minValues, final int[] maxValues) {
        assert(random != null);
        assert(numDimensions > 0);
        assert(minValues != null);
        assert(maxValues != null);
        assert(minValues.length == numDimensions);
        assert(maxValues.length == numDimensions);
        this.genome = new ArrayList<IntGene>(numDimensions) {{
           for (int i = 0; i < numDimensions; i++) {
               final int delta = maxValues[i] - minValues[i];
               assert(delta >= 0);
               final int roll = minValues[i] + (random.nextInt()*delta);
               add(new IntGene(roll));
           } 
        }};
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }
    
    public IntVectorIndividual(final List<IntGene> genome) {
        if (genome == null)
            throw new NullPointerException(String.format("%s: genome is null.", this.getClass().getSimpleName()));
        if (Misc.containsNulls(genome))
            throw new IllegalArgumentException(String.format("%s: genome contains null values.", this.getClass().getSimpleName()));
        this.genome = new ArrayList<IntGene>(genome);
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }
    
    public IntVectorIndividual(final int[] genome) {
        if (genome == null)
            throw new NullPointerException(String.format("%s: genome is null.", this.getClass().getSimpleName()));
        this.genome = new ArrayList<IntGene>(genome.length);
        for (int i = 0; i < genome.length; i++)
            this.genome.add(new IntGene(genome[i]));
        this.id = nextId++;
        fitness = Option.NONE;
        assert(repOK());
    }
    
    private IntVectorIndividual(final IntVectorIndividual ref, final double fitness) {
        assert(ref != null);
        genome = new ArrayList<IntGene>(ref.genome);
        id = ref.id;
        this.fitness = new Option<Double>(fitness);
        assert(repOK());
    }

    @Override
    public long getID() { return id; }
    
    @Override
    public int size() { return genome.size(); }
    
    @Override
    public LinearGenomeIndividual<IntGene> create(final List<IntGene> genome) {
        assert(genome != null);
        return new IntVectorIndividual(genome);
    }

    @Override
    public List<IntGene> getGenome() {
        return new ArrayList<IntGene>(genome); // Defensive copy
    }
    
    public int[] getGenomeArray() {
        final int[] array = new int[genome.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = genome.get(i).value;
        return array;
    }
    
    public int getElement(final int i) {
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
    public IntVectorIndividual setFitness(double fitness) {
        return new IntVectorIndividual(this, fitness);
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
        if (!(o instanceof IntVectorIndividual))
            return false;
        final IntVectorIndividual ref = (IntVectorIndividual) o;
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
        return String.format("[%s: id=%s, fitness=%s, genome=%s]", this.getClass().getSimpleName(), this.getID(), fitness, genome.toString());
    }
    
    // </editor-fold>
}
