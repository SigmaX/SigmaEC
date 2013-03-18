package SigmaEC.operate;

import SigmaEC.represent.Gene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Implements unbiased n-point crossover between m parents.
 * 
 * @author Eric 'Siggy' Scott
 */
public class NPointCrossoverMator<T extends Gene> implements Mator<T>
{
    final private int numCutPoints;
    final private int numParents;
    final private Random random;

    @Override
    public int getNumParents() { return numParents; }
    
    @Override
    public int getNumChildren() { return numParents; }
    
    public NPointCrossoverMator(int numCutPoints, int numParents, Random random)
    {
        if (numCutPoints < 1)
            throw new IllegalArgumentException("NPointCrossoverMator: numCutPoints is " + numCutPoints + ", but must be > 0.");
        if (numParents < 2)
            throw new IllegalArgumentException("NPointCrossoverMator: numParents is " + numParents + ", but must be > 1.");
        if (random == null)
            throw new IllegalArgumentException("NPointCrossoverMator: random is null.");
        this.numCutPoints = numCutPoints;
        this.numParents = numParents;
        this.random = random;
        assert(repOK());
    }
    
    /**
     * Non-destructively recombine parent genomes into children using n-point
     * crossover.  Specifically, this works as follows: Let l be the
     * length of all the genomes in parents.  For a genome G^k, let G^k_i be the
     * ith gene of G^k.
     * 
     * A set C of cut points {c_1, c_2, ..., c_m} is chosen such that c_i &le;
     * c_{i+1}.  Each genomes in the set parents = {G^1, G^2, ... , G^n} is
     * cut into subsequences according to the cut points:  That is, a genome G^k
     * is cut into the sequences S^k_1 = G_1G_2...G_{c_1},
     * S^k_2 = G_{c_1+1}G_{c_1+2}...G_{c_2}, ..., S^k_{m} = G_{c_m}...G_{l}.
     * 
     * Now that the genomes have been cut, for each k \in (1,n) we generate
     * offspring O^k = S^{r_1}_1S^{r_2}_2...S^{r_m}_m, where r_i is a random
     * integer selected from (1,n) without replacement.  That is, we generate
     * offspring by randomly choosing among the ith subsequences of all parents
     * to determine the ith subsequence of the offspring.
     * 
     * PRECONDITIONS: The list of parents must be equal this.numParents, and
     * all the parent genomes must be of the same length.  No exception is
     * caught if these conditions are not met.
     * 
     * @return The set of offspring, which will be the same size as the set of
     * parents.
     */
    @Override
    public List<List<T>> mate(List<List<T>> parents)
    {
        assert(parents.size() == numParents);
        assert(genomesSameLength(parents));
        int[] cutPoints = getNewCutPoints(parents.get(0).size());
        
        // Initialize empty genomes for offspring
        List<List<T>> offspring = new ArrayList<List<T>>(numParents) {{
           for (int i = 0; i < numParents; i++)
               add(new ArrayList<T>());
        }};
        
        // Splice the parents and mix them into offspring
        for(int i = 0; i < cutPoints.length - 1; i++)
        {
            List<List<T>> segments = getSegments(cutPoints[i], cutPoints[i+1], parents);
            for (List<T> o : offspring)
            {
                int parentSegment = random.nextInt(segments.size());
                o.addAll(segments.get(parentSegment));
                segments.remove(parentSegment); // Drawing without replacement
            }
        }
        
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
    {
        int[] cutPoints = new int[numCutPoints + 1];
        for(int i = 0; i < numCutPoints; i++)
            cutPoints[i] = random.nextInt(genomeLength);
        cutPoints[numCutPoints + 1] = genomeLength;
        Arrays.sort(cutPoints);
        return cutPoints;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return numCutPoints > 0
                && numParents > 1
                && random != null;
    }

    @Override
    public String toString()
    {
        return String.format("[NPointCrossoverMator: CutPonts=%d, Parents=%d", numCutPoints, numParents);
    }
    
    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof NPointCrossoverMator))
            return false;
        NPointCrossoverMator cRef = (NPointCrossoverMator) ref;
        return numParents == cRef.numParents
                && numCutPoints == cRef.numCutPoints
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + this.numCutPoints;
        hash = 97 * hash + this.numParents;
        hash = 97 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
    
}
