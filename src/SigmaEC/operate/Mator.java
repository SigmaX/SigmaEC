package SigmaEC.operate;

import SigmaEC.represent.Gene;
import java.util.List;

/**
 * An operator that takes two or more genomes and 'mates' them to produce one or
 * more children.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Mator<T extends Gene>
{
    /**
     * @param parents Parent genomes.
     * @return Children. */
    public List<T> mate(List<T> parents);
    
    /** @return The number of parents the mate() function takes, or -1 if it
     * takes an arbitrary number of parents. */
    public int getNumParents();
}
