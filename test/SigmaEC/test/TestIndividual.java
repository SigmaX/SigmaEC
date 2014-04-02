package SigmaEC.test;

import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple individual with 1 continuous trait.  Intended for use with unit
 * tests.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestIndividual extends LinearGenomeIndividual<TestGene>
{
    final private List<TestGene> genome;
    final private double trait;
    public double getTrait() { return trait; }

    public TestIndividual(double trait) { this.trait = trait; this.genome = null; }
    public TestIndividual(double trait, List<TestGene> genome) { this.trait = trait; this.genome = genome; }
    
    @Override
    public boolean repOK() { return true; }

    @Override
    public LinearGenomeIndividual create(List<TestGene> genome) {
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
        return Misc.doubleEquals(trait, ref.trait)
                && (genome == null ? ref.genome == null : genome.equals(ref.genome));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.trait) ^ (Double.doubleToLongBits(this.trait) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: trait=%f, genome=%s]", this.getClass().getSimpleName(), genome.toString());
    }
}
