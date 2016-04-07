package SigmaEC.represent.linear;

import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public List<DoubleGene> getGenome() {
        return new ArrayList<>(genome); // Defensive copy
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
    public boolean isEvaluated() {
        return fitness.isDefined();
    }

    @Override
    public Individual setParents(List<? extends Individual> parents) {
        return new Builder(this).setParents(parents).build();
    }

    @Override
    public DoubleVectorIndividual setFitness(double fitness) {
        return new Builder(this).setFitness(fitness).build();
    }
    
    @Override
    public DoubleVectorIndividual clearFitness() {
        return new Builder(this).clearFitness().build();
    }
    
    @Override
    public DoubleVectorIndividual create(final List<DoubleGene> genome, final List<? extends Individual> parents) {
        assert(genome != null);
        return new Builder(genome).setParents(parents).build();
    }

    @Override
    public Individual clearParents() {
        return new Builder(this).clearParents().build();
    }
    // </editor-fold>
    
    public static class Builder {
        private final List<DoubleGene> genome;
        private Option<Double> fitness = Option.NONE;
        private Option<List<Individual>> parents = Option.NONE;
        
        public DoubleVectorIndividual build() {
            return new DoubleVectorIndividual(genome, fitness, parents);
        }
        
        public Builder(final List<DoubleGene> genome) {
            assert(genome != null);
            this.genome = genome;
        }
        
        public Builder(final double[] genome) {
            assert(genome != null);
            assert(Misc.allFinite(genome));
            this.genome = new ArrayList<>(genome.length);
            for (int i = 0; i < genome.length; i++)
                this.genome.add(new DoubleGene(genome[i]));
        }
        
        public Builder(final DoubleVectorIndividual ref) {
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
    
    /** Construct a random double vector. */
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
        parents = Option.NONE;
        assert(repOK());
    }
    
    /** Construct a random double vector. */
    public DoubleVectorIndividual(final Random random, final int numDimensions, final double defaultMinValue, final double defaultMaxValue) {
        assert(random != null);
        assert(numDimensions > 0);
        this.genome = new ArrayList<DoubleGene>(numDimensions) {{
           for (int i = 0; i < numDimensions; i++) {
               final double delta = defaultMaxValue - defaultMinValue;
               assert(delta >= 0);
               final double roll = defaultMinValue + (random.nextDouble()*delta);
               add(new DoubleGene(roll));
           } 
        }};
        this.id = nextId++;
        fitness = Option.NONE;
        parents = Option.NONE;
        assert(repOK());
    }
    
    /** Private constructor for use with the Builder pattern. Does not make defensive copies! */
    private DoubleVectorIndividual(final List<DoubleGene> genome, final Option<Double> fitness, final Option<List<Individual>> parents) {
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
                && genome != null
                && fitness != null
                && !(parents.isDefined() && parents.get().isEmpty())
                && !(fitness.isDefined() && Double.isNaN(fitness.get()))
                && !Misc.containsNulls(genome);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DoubleVectorIndividual))
            return false;
        final DoubleVectorIndividual ref = (DoubleVectorIndividual) o;
        return genome.equals(ref.genome)
                && fitness.equals(ref.fitness)
                && parents.equals(ref.parents);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.genome);
        hash = 53 * hash + Objects.hashCode(this.fitness);
        hash = 53 * hash + Objects.hashCode(this.parents);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: id=%s, fitness=%s, parents=%s, genome=%s]", this.getClass().getSimpleName(), this.getID(), fitness, parents, genome.toString());
    }
    
    // </editor-fold>
}
