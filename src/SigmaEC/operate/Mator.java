package SigmaEC.operate;

import SigmaEC.BuilderT;
import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.List;
import java.util.Random;

/**
 * An operator that takes two or more individuals and 'mates' them to produce
 * one or more children.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Mator<T extends Individual> extends ContractObject
{
    /**
     * @param parents Parent genomes.
     * @return Children. */
    public abstract List<T> mate(List<T> parents);
    
    /** @return The number of parents the mate() function takes, or -1 if it
     * takes an arbitrary number of parents. */
    public abstract int getNumParents();
    
    /** @return The number of children the mate() function will produce, or -1
     * if this number varies. */
    public abstract int getNumChildren();
    
    public interface MatorBuilder<T extends Individual> extends BuilderT<Mator<T>> {
        Mator.MatorBuilder<T> random(final Random random);
    }
}
