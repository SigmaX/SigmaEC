package SigmaEC.represent;

/**
 * Defines a mapping that converts and individual (genotype) to a phenotype.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Decoder<T extends Individual, P extends Phenotype> {
    P decode(T individual);
}
