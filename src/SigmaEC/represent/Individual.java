package SigmaEC.represent;

import SigmaEC.ContractObject;

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
    
    /** Produces a copy of this individual with the fitness set or altered. */
    public abstract Individual setFitness(final double fitness);
    
}
