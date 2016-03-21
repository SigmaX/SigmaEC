package SigmaEC.represent.linear;

import SigmaEC.represent.Individual;
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
    private final Option<List<Individual>> parents;
    
     // <editor-fold defaultstate="collapsed" desc="Producers and Consumers">
    @Override
    public boolean hasParents() {
        return parents.isDefined();
    }
    
    @Override
    public long getID() { return id; }
    
    @Override
    public int size() { return genome.size(); }
    
    @Override
    public Option<List<Individual>> getParents() {
        if (!parents.isDefined())
            return Option.NONE;
        return new Option<>((List<Individual>)new ArrayList<>(parents.get())); // Defensive copy
    }

    @Override
    public List<IntGene> getGenome() {
        return new ArrayList<>(genome); // Defensive copy
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
    public Individual setParents(List<? extends Individual> parents) {
        return new Builder(this).setParents(parents).build();
    }

    @Override
    public IntVectorIndividual setFitness(double fitness) {
        return new Builder(this).setFitness(fitness).build();
    }
    
    @Override
    public IntVectorIndividual create(final List<IntGene> genome, final List<? extends Individual> parents) {
        assert(genome != null);
        if (parents.isEmpty())
            return new Builder(genome).build();
        return new Builder(genome).setParents(parents).build();
    }

    @Override
    public Individual clearParents() {
        return new Builder(this).clearParents().build();
    }
    // </editor-fold>
    
    public static class Builder {
        private final List<IntGene> genome;
        private Option<Double> fitness = Option.NONE;
        private Option<List<Individual>> parents = Option.NONE;
        
        public IntVectorIndividual build() {
            return new IntVectorIndividual(genome, fitness, parents);
        }
        
        public Builder(final List<IntGene> genome) {
            assert(genome != null);
            this.genome = genome;
        }
        
        public Builder(final int[] genome) {
            assert(genome != null);
            this.genome = new ArrayList<>(genome.length);
            for (int i = 0; i < genome.length; i++)
                this.genome.add(new IntGene(genome[i]));
        }
        
        public Builder(final IntVectorIndividual ref) {
            assert(ref != null);
            genome = ref.genome;
            parents = ref.parents;
            fitness = ref.fitness;
        }
        
        public Builder setFitness(final double fitness) {
            this.fitness = new Option<>(fitness);
            return this;
        }
        
        public Builder setParents(final List<? extends Individual> parents) {
            assert(parents != null);
            assert(!Misc.containsNulls(parents));
            this.parents = new Option<>((List<Individual>)new ArrayList<Individual>(parents));
            return this;
        }
        
        public Builder clearParents() {
            parents = Option.NONE;
            return this;
        }
        
        public Builder clearFitness() {
            fitness = Option.NONE;
            return this;
        }
    }
    
    
    /** Construct a random int vector. */
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
               final int roll = (delta == 0) ? minValues[i] : minValues[i] + (random.nextInt(delta));
               add(new IntGene(roll));
           } 
        }};
        this.id = nextId++;
        fitness = Option.NONE;
        parents = Option.NONE;
        assert(repOK());
    }
    
    public IntVectorIndividual(final Random random, final int numDimensions, final int defaultMinValue, final int defaultMaxValue) {
        assert(random != null);
        assert(numDimensions > 0);
        this.genome = new ArrayList<IntGene>(numDimensions) {{
           for (int i = 0; i < numDimensions; i++) {
               final int delta = defaultMaxValue - defaultMinValue;
               assert(delta >= 0);
               final int roll = defaultMinValue + (random.nextInt(delta));
               add(new IntGene(roll));
           } 
        }};
        this.id = nextId++;
        fitness = Option.NONE;
        parents = Option.NONE;
        assert(repOK());
    }
    
    /** Private constructor for use with the Builder pattern. Does not make defensive copies! */
    private IntVectorIndividual(final List<IntGene> genome, final Option<Double> fitness, final Option<List<Individual>> parents) {
        assert(genome != null);
        assert(!Misc.containsNulls(genome));
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
                && genome != null
                && fitness != null
                && !(parents.isDefined() && parents.get().isEmpty())
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
