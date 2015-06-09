package SigmaEC.test;

import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import java.util.Arrays;
import java.util.List;

/**
 * A simple individual with a real-vector genome.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestVectorIndividual extends Individual
{
    private final long id;
    private static long nextId = 0;
    private final double[] vector;
    private final Option<Double> fitness;
    
    public TestVectorIndividual(final double[] vector)
    {
        this.vector = vector;
        this.id = nextId++;
        fitness = Option.NONE;
    }
    
    private TestVectorIndividual(final TestVectorIndividual ref, final double fitness) {
        assert(ref != null);
        vector = Arrays.copyOf(ref.vector, ref.vector.length);
        id = ref.id;
        this.fitness = new Option<Double>(fitness);
    }

    public double[] getVector() {
        return Arrays.copyOf(vector, vector.length);
    }

    @Override
    public boolean repOK() { return true; }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getID() { return id; }

    @Override
    public double getFitness() {
        if (fitness.isDefined())
            return fitness.get();
        else
            throw new IllegalStateException(String.format("%s: attempted to read the fitness of an individual whose fitness has not been evaluated.", this.getClass().getSimpleName()));
    }

    @Override
    public TestVectorIndividual setFitness(double fitness) {
        return new TestVectorIndividual(this, fitness);
    }

    @Override
    public boolean hasParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Option<List<Individual>> getParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual setParents(List<? extends Individual> parents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual clearParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
