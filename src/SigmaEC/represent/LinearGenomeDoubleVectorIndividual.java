package SigmaEC.represent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Basic real-vector representation of an individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LinearGenomeDoubleVectorIndividual implements LinearGenomeIndividual<DoubleGene>, DoubleVectorIndividual
{
    final private List<DoubleGene> genome;
    
    public LinearGenomeDoubleVectorIndividual(List<DoubleGene> genome)
    {
        this.genome = new ArrayList<DoubleGene>(genome); // Shallow copy okay because genes are immutable
        assert(repOK());
    }
    
    /** Generate an individual with a random genome, where each gene takes on
     * a value between -max and max.
     */
    public LinearGenomeDoubleVectorIndividual(final double max, final int numGenes, final Random random)
    {
        this.genome = new ArrayList<DoubleGene>(numGenes) {{
           for (int i = 0; i < numGenes; i++)
               add(new DoubleGene(random.nextDouble()*2*max - max));
        }};
        assert(repOK());
    }
    
    @Override
    public LinearGenomeIndividual<DoubleGene> create(List<DoubleGene> genome)
    {
        assert(genome != null);
        return new LinearGenomeDoubleVectorIndividual(genome);
    }

    @Override
    public List<DoubleGene> getGenome()
    {
        return new ArrayList<DoubleGene>(genome);
    }

    @Override
    public int size()
    {
        return genome.size();
    }

    @Override
    public double getElement(int i)
    {
        return genome.get(i).value;
    }

    @Override
    public double[] getVector()
    {
        double[] vector = new double[size()];
        for (int i = 0; i < size(); i++)
            vector[i] = genome.get(i).value;
        return vector;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return genome != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[LinearGenomeDoubleVectorIndividual: Genome=%s]", genome.toString());
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof LinearGenomeDoubleVectorIndividual))
            return false;
        
        LinearGenomeDoubleVectorIndividual cRef = (LinearGenomeDoubleVectorIndividual) o;
        return genome.equals(cRef.genome);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.genome != null ? this.genome.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
}
