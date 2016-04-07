package SigmaEC.represent;

import SigmaEC.ContractObject;
import SigmaEC.util.Option;
import java.util.List;

/**
 * An evolvable individual.  An individual wraps a genome, but also may include
 * supplementary data and/or information about a genotype-to-phenotype mapping.
 * 
 * This is an abstract class instead of an interface in order to force users to
 * override equals(), hashCode() and toString().
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Individual extends ContractObject
{
    /** An ID that can be used to uniquely identify this individual in logged output.
     * No two individuals should ever have the same ID.
     */
    public abstract long getID();
    
    public abstract double getFitness();
    
    public abstract boolean hasParents();
    
    public abstract boolean isEvaluated();
    
    public abstract Option<List<Individual>> getParents();
    
    /** Produces a copy of this individual with the fitness set or altered. */
    public abstract Individual setFitness(final double fitness);
    
    /** Produces a copy of this individual with the list of parents set or altered.
     * @param parents A list of Individuals that this was produced from.  They must have the same subtype at this. */
    public abstract Individual setParents(final List<? extends Individual> parents);
    
    /** Produces a copy of this individual with an empty set of parents. */
    public abstract Individual clearParents();
    
    /** Produces a copy of this individual with an empty fitness attribute. */
    public abstract Individual clearFitness();
    
}
