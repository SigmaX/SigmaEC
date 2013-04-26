package SigmaEC.operate;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * An operator that takes two or more individuals and 'mates' them to produce
 * one or more children.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Mator<T extends Individual>
{
    /**
     * @param parents Parent genomes.
     * @return Children. */
    public List<T> mate(List<T> parents);
    
    /** @return The number of parents the mate() function takes, or -1 if it
     * takes an arbitrary number of parents. */
    public int getNumParents();
    
    /** @return The number of children the mate() function will produce, or -1
     * if this number varies. */
    public int getNumChildren();
    
    /** Representation invariant.  If this returns false, there is something
     * invalid about the Individual's internal state. */
    public boolean repOK();
}
