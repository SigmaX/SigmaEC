package SigmaEC.represent;

import SigmaEC.ContractObject;
import SigmaEC.meta.Fitness;
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
public abstract class Individual<F extends Fitness> extends ContractObject
{
    /** An ID that can be used to uniquely identify this individual in logged output.
     * No two individuals should ever have the same ID.
     */
    public abstract long getID();
    
    public abstract F getFitness();
    
    public abstract boolean hasParents();
    
    public abstract boolean isEvaluated();
    
    public abstract Option<List<Individual<F>>> getParents();
    
    /** Produces a copy of this individual with the fitness set or altered. */
    public abstract Individual<F> setFitness(final F fitness);
    
    /** Produces a copy of this individual with the list of parents set or altered.
     * @param parents A list of Individuals that this was produced from.  They must have the same subtype at this. */
    public abstract Individual<F> setParents(final List<? extends Individual<F>> parents);
    
    /** Produces a copy of this individual with an empty set of parents. */
    public abstract Individual<F> clearParents();
    
    /** Produces a copy of this individual with an empty fitness attribute. */
    public abstract Individual<F> clearFitness();
    
}
