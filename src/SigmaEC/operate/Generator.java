package SigmaEC.operate;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import SigmaEC.select.Selector;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a population of individuals and creates a new generation, using some
 * Procreator.
 * 
 * @see Procreator
 * @author Eric 'Siggy' Scott
 */
public class Generator<T extends Individual>
{
    private final Selector<T> selector;
    private final Procreator<T> procreator;
    private final int numOffspringPerMating;
    
    public Generator(Selector<T> selector, Procreator<T> procreator, int numOffspringPerMating) throws IllegalArgumentException
    {
        if (selector == null)
            throw new IllegalArgumentException("Generator: selector is null.");
        if (procreator == null)
            throw new IllegalArgumentException("Generator: procreator is null.");
        if (numOffspringPerMating < 1)
            throw new IllegalArgumentException("Generator: numOffspringPerMating is " + numOffspringPerMating + ", but must be > 0.");
        
        this.selector = selector;
        this.procreator = procreator;
        this.numOffspringPerMating = numOffspringPerMating;
    }

    public Selector<T> getSelector()
    {
        return selector;
    }

    public int getNumOffspringPerMating()
    {
        return numOffspringPerMating;
    }
    
    /**
     * @return A generation of offspring.  Behavior depends on
     * produceOffspring() and this.selector, which chooses the parents.
     * 
     * @see #produceOffspring(java.util.List, SigmaEC.function.ObjectiveFunction) 
     */
    public List<T> produceGeneration(List<T> population)
    {
        assert(numOffspringPerMating > 0);
        
        List<T> parents = new ArrayList<T>();
        for (int i = 0; i < procreator.getNumParents(); i++)
            parents.add(selector.selectIndividual(population));
        List<T> offspring = new ArrayList(numOffspringPerMating);
        for (int i = 0; i < numOffspringPerMating; i++)
            offspring.add(procreator.produceOffspring(parents));
        return offspring;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    public boolean repOK()
    {
        return selector != null
                && procreator != null
                && numOffspringPerMating > 0
                && selector.repOK()
                && procreator.repOK();
    }
    
    @Override
    public String toString()
    {
        return String.format("[Generator: numOffspringPerMating=%d, Selector=%s, Procreator=%s]", numOffspringPerMating, selector.toString(), procreator.toString());
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof Generator))
            return false;
        Generator cRef = (Generator) ref;
        return numOffspringPerMating == cRef.numOffspringPerMating
                && selector.equals(cRef.selector)
                && procreator.equals(cRef.procreator);
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 31 * hash + (this.selector != null ? this.selector.hashCode() : 0);
        hash = 31 * hash + (this.procreator != null ? this.procreator.hashCode() : 0);
        hash = 31 * hash + this.numOffspringPerMating;
        return hash;
    }
    //</editor-fold>
}
