package SigmaEC.operate;

import SigmaEC.represent.Gene;
import SigmaEC.represent.LinearGenomeIndividual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Implements unbiased n-point crossover between 2 parents.
 * 
 * @author Eric 'Siggy' Scott
 */
public class NPointCrossoverMator<T extends LinearGenomeIndividual<G>, G extends Gene> extends Mator<T>
{
    final private int numCutPoints;
    final private Random random;
    final private boolean allowCloning;

    @Override
    public int getNumParents() { return 2; }
    
    @Override
    public int getNumChildren() { return 2; }

    public int getNumCutPoints() { return numCutPoints; }
    
    public NPointCrossoverMator(final int numCutPoints, final boolean allowCloning, final Random random) {
        if (numCutPoints < 1)
            throw new IllegalArgumentException(String.format("%s: numCutPoints is %d, but must be > 0.", this.getClass().getSimpleName(), numCutPoints));
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
        this.numCutPoints = numCutPoints;
        this.random = random;
        this.allowCloning = allowCloning;
        assert(repOK());
    }
    
    private NPointCrossoverMator(final Builder builder) {
        this(builder.numCutPoints, builder.allowCloning, builder.random);
    }
    
    public static class Builder<T extends LinearGenomeIndividual<G>, G extends Gene> implements MatorBuilder<T> {
        final private static String P_NUM_CUT_POINTS = "numCutPoints";
        final private static String P_ALLOW_CLONING = "allowCloning";
        
        private int numCutPoints;
        private boolean allowCloning = true;
        private Random random;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            numCutPoints = Parameters.getIntParameter(properties, Parameters.push(base, P_NUM_CUT_POINTS));
            final Option<Boolean> allowCloningOpt = Parameters.getOptionalBooleanParameter(properties, Parameters.push(base, P_ALLOW_CLONING));
            if (allowCloningOpt.isDefined())
                allowCloning = allowCloningOpt.get();
        }
        
        @Override
        public MatorBuilder<T> random(final Random random) {
            assert(random != null);
            this.random = random;
            return this;
        }

        @Override
        public Mator<T> build() {
            if (random == null)
                throw new IllegalStateException(this.getClass().getSimpleName() + ": trying to build before random has been initialized.");
            return new NPointCrossoverMator<T, G>(this);
        }
        
    }
    
    /**
     * Non-destructively recombine parent genomes into children using n-point
     * crossover.  Since cut-points can be chosen on the ends, this will
     * sometimes result in cloning.
     * 
     * PRECONDITIONS: All the parent genomes must be of the same length.
     * No exception is caught if this condition is not met.
     * 
     * @return The set of offspring, which will be the same size as the set of
     * parents.
     */
    @Override
    public List<T> mate(final List<T> parents)
    {
        assert(parents.size() == 2);
        List<List<G>> parentGenomes = new ArrayList<List<G>>() {{
           add(parents.get(0).getGenome());
           add(parents.get(1).getGenome());
        }};
        assert(genomesSameLength(parentGenomes));
        int[] cutPoints = getNewCutPoints(parentGenomes.get(0).size());
        
        final List<G> child1 = new ArrayList<G>(parentGenomes.get(0).size());
        final List<G> child2 = new ArrayList<G>(parentGenomes.get(0).size());
        
        // Splice the parents and mix them into offspring
        boolean swap = false; // When true, the first child gets a segment from the second parent.  When false, the first child gets a segment from the first parent.
        for(int i = 0; i < cutPoints.length - 1; i++)
        {
            List<List<G>> segments = getSegments(cutPoints[i], cutPoints[i+1], parentGenomes);
            assert(segments.size() == 2);
            child1.addAll(segments.get(swap ? 1 : 0));
            child2.addAll(segments.get(swap ? 0 : 1));
            swap = !swap; // Alternate the source of the segments
        }
        
        assert(child1.size() == parentGenomes.get(0).size());
        assert(child2.size() == parentGenomes.get(0).size());
        List<T> offspring = new ArrayList<T>() {{
           add((T) (parents.get(0).create(child1)));
           add((T) (parents.get(0).create(child2))); 
        }};
        assert(repOK());
        return offspring;
    }
    
    /** Each genome in parents is non-destructively cropped down to the sequence
     * of genes found at the indices given by [start, start + 1 ... end - 1].
     * 
     * @return A new list containing the cropped genomes.
     */
    private List<List<G>> getSegments(final int start, final int end, final List<List<G>> parents)
    {
        assert(parents.size() > 0);
        assert(start <= end);
        List<List<G>> segments = new ArrayList<List<G>>();
        for (int i = 0; i < parents.size(); i++)
        {
            final int p = i;
            segments.add(new ArrayList<G>() {{
                for (int j = start; j < end; j++)
                    add(parents.get(p).get(j));
            }});
        }
        return segments;
    }
    
    /** Compares the length of n Lists. */
    private boolean genomesSameLength(final List<List<G>> genomes)
    {
        assert(genomes.size() > 0);
        final int firstLength = genomes.get(0).size();
        for (int i = 1; i < genomes.size(); i++)
            if (genomes.get(i).size() != firstLength)
                return false;
        return true;
    }
    
    private int[] getNewCutPoints(final int genomeLength)
    { //TODO make use of allowCloning
        int[] cutPoints = new int[numCutPoints + 2];
        cutPoints[0] = 0;
        for(int i = 0; i < numCutPoints; i++)
            cutPoints[i] = random.nextInt(genomeLength+1);
        cutPoints[numCutPoints + 1] = genomeLength;
        Arrays.sort(cutPoints);
        return cutPoints;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return numCutPoints > 0
                && random != null;
    }

    @Override
    public String toString() {
        return String.format("[%s: numCutPoints=%d, allowCloning=%s, random=%s]", this.getClass().getSimpleName(), numCutPoints, Boolean.toString(allowCloning), random.toString());
    }
    
    @Override
    public boolean equals(final Object ref) {
        if (!(ref instanceof NPointCrossoverMator))
            return false;
        NPointCrossoverMator cRef = (NPointCrossoverMator) ref;
        return numCutPoints == cRef.numCutPoints
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.numCutPoints;
        hash = 97 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
    
}
