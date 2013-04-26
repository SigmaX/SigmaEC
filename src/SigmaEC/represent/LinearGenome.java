package SigmaEC.represent;

import java.util.List;

/**
 * Gives a class a list of genes to be used as a genome.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface LinearGenome<T extends Gene>
{
    public abstract List<T> getGenome();
}
