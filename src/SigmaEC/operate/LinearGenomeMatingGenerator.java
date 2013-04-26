package SigmaEC.operate;

import SigmaEC.represent.Gene;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a population of individuals represented by linear genomes and creates
 * a new generation.
 * 
 * @see LinearGenomeMator
 * @author Eric 'Siggy' Scott
 */
public class LinearGenomeMatingGenerator<T extends LinearGenomeIndividual<G>, G extends Gene> implements Generator<T>
{
    private final Selector<T> parentSelector;
    private final Mator<T> mator;
    
    public LinearGenomeMatingGenerator(Selector<T> selector, Mator<T> mator) throws IllegalArgumentException
    {
        if (selector == null)
            throw new IllegalArgumentException("LinearGenomeMatingGenerator: selector is null.");
        if (mator == null)
            throw new IllegalArgumentException("LinearGenomeMatingGenerator: procreator is null.");
        if (mator.getNumChildren() != mator.getNumParents())
            throw new IllegalArgumentException("LinearGenomeMatingGenerator: mator.getNumChildren() must equal mator.getNumParents()");
        
        this.parentSelector = selector;
        this.mator = mator;
    }

    @Override
    public Selector<T> getParentSelector()
    {
        return parentSelector;
    }
    
    /**
     * @return A generation of offspring.  Behavior depends on
     * produceOffspring() and this.selector, which chooses the parents.
     * 
     * @see #produceOffspring(java.util.List, SigmaEC.function.ObjectiveFunction) 
     */
    @Override
    public List<T> produceGeneration(List<T> population)
    {
        assert(population.size() >= mator.getNumParents());
        assert(population.size()%mator.getNumChildren() == 0);
        assert(Misc.containsOnlyClass(population, population.get(0).getClass()));
        
        List<T> offspring = new ArrayList();
        for(int totalChildren = 0; totalChildren < population.size(); totalChildren += mator.getNumChildren())
        {
            List<T> parents = new ArrayList<T>();
            for (int i = 0; i < mator.getNumParents(); i++)
                parents.add(parentSelector.selectIndividual(population));
            offspring.addAll(mator.mate(parents));
        }
        return offspring;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return parentSelector != null
                && mator != null
                && parentSelector.repOK()
                && mator.repOK();
    }
    
    @Override
    public String toString()
    {
        return String.format("[LinearGenomeGenerator: Selector=%s, Mator=%s]", parentSelector.toString(), mator.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof LinearGenomeMatingGenerator))
            return false;
        LinearGenomeMatingGenerator cRef = (LinearGenomeMatingGenerator) ref;
        return parentSelector.equals(cRef.parentSelector)
                && mator.equals(cRef.mator)
                && mator.getNumChildren() == mator.getNumParents();
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 31 * hash + (this.parentSelector != null ? this.parentSelector.hashCode() : 0);
        hash = 31 * hash + (this.mator != null ? this.mator.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
