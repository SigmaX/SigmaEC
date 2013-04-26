package SigmaEC.operate;

import SigmaEC.represent.Gene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Implements unbiased n-point crossover between 2 parents.
 * 
 * @author Eric 'Siggy' Scott
 */
public class NPointCrossoverMator<T extends Gene> implements LinearGenomeMator<T>
{
    final private int numCutPoints;
    final private Random random;
    final private boolean allowCloning;

    @Override
    public int getNumParents() { return 2; }
    
    @Override
    public int getNumChildren() { return 2; }

    public int getNumCutPoints() { return numCutPoints; }
    
    public NPointCrossoverMator(int numCutPoints, boolean allowCloning, Random random)
    {
        if (numCutPoints < 1)
            throw new IllegalArgumentException("NPointCrossoverMator: numCutPoints is " + numCutPoints + ", but must be > 0.");
        if (random == null)
            throw new IllegalArgumentException("NPointCrossoverMator: random is null.");
        this.numCutPoints = numCutPoints;
        this.random = random;
        this.allowCloning = allowCloning;
        assert(repOK());
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
    public List<List<T>> mate(List<List<T>> parents)
    {
        assert(parents.size() == 2);
        assert(genomesSameLength(parents));
        int[] cutPoints = getNewCutPoints(parents.get(0).size());
        
        final List<T> child1 = new ArrayList<T>(parents.get(0).size());
        final List<T> child2 = new ArrayList<T>(parents.get(0).size());
        
        // Splice the parents and mix them into offspring
        boolean swap = false; // When true, the first child gets a segment from the second parent.  When false, the first child gets a segment from the first parent.
        for(int i = 0; i < cutPoints.length - 1; i++)
        {
            List<List<T>> segments = getSegments(cutPoints[i], cutPoints[i+1], parents);
            assert(segments.size() == 2);
            child1.addAll(segments.get(swap ? 1 : 0));
            child2.addAll(segments.get(swap ? 0 : 1));
            swap = !swap; // Alternate the source of the segments
        }
        
        List<List<T>> offspring = new ArrayList<List<T>>() {{
           add(child1);
           add(child2);
        }};
        
        assert(offspring.size() == 2);
        assert(offspring.get(0).size() == parents.get(0).size());
        assert(genomesSameLength(offspring));
        assert(repOK());
        return offspring;
    }
    
    /** Each genome in parents is non-destructively cropped down to the sequence
     * of genes found at the indices given by [start, start + 1 ... end - 1].
     * 
     * @return A new list containing the cropped genomes.
     */
    private List<List<T>> getSegments(final int start, final int end, final List<List<T>> parents)
    {
        assert(parents.size() > 0);
        assert(start <= end);
        List<List<T>> segments = new ArrayList<List<T>>();
        for (int i = 0; i < parents.size(); i++)
        {
            final int p = i;
            segments.add(new ArrayList<T>() {{
                for (int j = start; j < end; j++)
                    add(parents.get(p).get(j));
            }});
        }
        return segments;
    }
    
    /** Compares the length of n Lists. */
    private boolean genomesSameLength(List<List<T>> genomes)
    {
        assert(genomes.size() > 0);
        final int firstLength = genomes.get(0).size();
        for (int i = 1; i < genomes.size(); i++)
            if (genomes.get(i).size() != firstLength)
                return false;
        return true;
    }
    
    private int[] getNewCutPoints(int genomeLength)
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
    final public boolean repOK()
    {
        return numCutPoints > 0
                && random != null;
    }

    @Override
    public String toString()
    {
        return String.format("[NPointCrossoverMator: CutPoints=%d]", numCutPoints);
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof NPointCrossoverMator))
            return false;
        NPointCrossoverMator cRef = (NPointCrossoverMator) ref;
        return numCutPoints == cRef.numCutPoints
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + this.numCutPoints;
        hash = 97 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
    
}
