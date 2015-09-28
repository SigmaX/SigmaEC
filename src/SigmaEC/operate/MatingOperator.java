package SigmaEC.operate;

import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a population of individuals represented by linear genomes and creates
 * a new generation.
 * 
 * @see LinearGenomeMator
 * @author Eric 'Siggy' Scott
 */
public class MatingOperator<T extends Individual> extends Operator<T> {
    private final static String P_MATOR = "mator";
        
    private final Mator<T> mator;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    public MatingOperator(final Parameters parameters, final String base) throws IllegalArgumentException {        
        assert(parameters != null);
        assert(base != null);
        this.mator = parameters.getInstanceFromParameter(Parameters.push(base, P_MATOR), Mator.class);
        
        if (mator == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": procreator is null.");
        if (mator.getNumChildren() != mator.getNumParents())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": mator.getNumChildren() must equal mator.getNumParents()");
        assert(repOK());
    }
    
    /**
     * @return A generation of offspring.  Behavior depends on
     * produceOffspring() and this.selector, which chooses the parents.
     * 
     * @see #produceOffspring(java.util.List, SigmaEC.function.ObjectiveFunction) 
     */
    @Override
    public List<T> operate(final int run, final int generation, final List<T> population)
    {
        assert(population.size() >= mator.getNumParents());
        assert(population.size()%mator.getNumChildren() == 0);
        assert(Misc.containsOnlyClass(population, population.get(0).getClass()));
        
        final List<T> offspring = new ArrayList();
        for(int totalChildren = 0; totalChildren < population.size(); totalChildren += mator.getNumChildren())
        {
            List<T> parents = new ArrayList<T>();
            for (int i = 0; i < mator.getNumParents(); i++)
                parents.add(parentSelector.selectIndividual(population));
            offspring.addAll(mator.mate(parents));
        }
        assert(population.size() == offspring.size());
        return offspring;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return parentSelector != null
                && mator != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: Selector=%s, Mator=%s]", this.getClass().getSimpleName(), parentSelector.toString(), mator.toString());
    }
    
    @Override
    public boolean equals(final Object ref) {
        if (!(ref instanceof MatingOperator))
            return false;
        final MatingOperator cRef = (MatingOperator) ref;
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
