package SigmaEC.test;

import SigmaEC.represent.LinearGenomeIndividual;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple individual with 1 continuous trait.  Intended for use with unit
 * tests.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TestIndividual implements LinearGenomeIndividual<TestGene>
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
}
