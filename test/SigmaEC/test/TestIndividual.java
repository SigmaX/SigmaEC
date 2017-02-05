package SigmaEC.test;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.represent.Individual;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple individual with 1 continuous fitness.  Intended for use with unit
 * tests.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestIndividual extends LinearGenomeIndividual<TestGene, ScalarFitness>
{
    final private long id;
    private static long nextId = 0;
    
    final private List<TestGene> genome;
    final private ScalarFitness fitness;
    public double getTrait() { return fitness.asScalar(); }

    public TestIndividual(double trait) { this.fitness = new ScalarFitness(trait); this.genome = null; this.id = nextId++; }
    public TestIndividual(double trait, List<TestGene> genome) { this.fitness = new ScalarFitness(trait); this.genome = genome; this.id = nextId++; }
    
    @Override
    public boolean repOK() { return true; }

    @Override
    public LinearGenomeIndividual create(List<TestGene> genome, final List<? extends Individual> parents) {
        return new TestIndividual(0, genome);
    }

    @Override
    public List<TestGene> getGenome() {
        return new ArrayList(genome);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TestIndividual))
            return false;
        final TestIndividual ref = (TestIndividual)o;
        return fitness.equals(ref.fitness)
                && (genome == null ? ref.genome == null : genome.equals(ref.genome));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.genome);
        hash = 19 * hash + Objects.hashCode(this.fitness);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: id=%d, trait=%f, genome=%s]", this.getClass().getSimpleName(), this.getID(), this.getClass().getSimpleName(), genome.toString());
    }

    @Override
    public long getID() { return id; }

    @Override
    public int size() {
        return genome.size();
    }

    @Override
    public ScalarFitness getFitness() {
        return fitness;
    }

    @Override
    public Individual setFitness(ScalarFitness fitness) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Option<List<Individual<ScalarFitness>>> getParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual<ScalarFitness> setParents(List<? extends Individual<ScalarFitness>> parents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual clearParents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual clearFitness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEvaluated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
