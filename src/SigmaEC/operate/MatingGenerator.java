package SigmaEC.operate;

import SigmaEC.represent.Individual;
import SigmaEC.select.IterativeSelector;
import SigmaEC.select.Selector;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Takes a population of individuals represented by linear genomes and creates
 * a new generation.
 * 
 * @see LinearGenomeMator
 * @author Eric 'Siggy' Scott
 */
public class MatingGenerator<T extends Individual> extends Generator<T>
{
    private final Mator<T> mator;
    private final Selector<T> parentSelector = new IterativeSelector<T>();
    
    private MatingGenerator(final Builder<T> builder) throws IllegalArgumentException {        
        assert(builder != null);
        this.mator = builder.matorBuilder.build();
        
        if (mator == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": procreator is null.");
        if (mator.getNumChildren() != mator.getNumParents())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": mator.getNumChildren() must equal mator.getNumParents()");
        assert(repOK());
    }
    
    public static class Builder<T extends Individual> implements Generator.GeneratorBuilder<T> {
        private final static String P_MATOR = "mator";
        
        private Mator.MatorBuilder<T> matorBuilder;
        private Random random;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            matorBuilder = Parameters.getBuilderFromParameter(properties, Parameters.push(base, P_MATOR), Mator.class);
        }
        
        @Override
        public Builder<T> random(final Random random) {
            assert(random != null);
            this.random = random;
            matorBuilder = matorBuilder.random(random);
            return this;
        }

        @Override
        public MatingGenerator<T> build() {
            if (random == null)
                throw new IllegalStateException(this.getClass().getSimpleName() + ": trying to build before random has been initialized.");
            return new MatingGenerator(this);
        }
    }
    
    /**
     * @return A generation of offspring.  Behavior depends on
     * produceOffspring() and this.selector, which chooses the parents.
     * 
     * @see #produceOffspring(java.util.List, SigmaEC.function.ObjectiveFunction) 
     */
    @Override
    public List<T> produceGeneration(final List<T> population)
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
        if (!(ref instanceof MatingGenerator))
            return false;
        final MatingGenerator cRef = (MatingGenerator) ref;
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
