package SigmaEC.represent;

/**
 * A dummy decoder for when the genotype and phenotype are identical.
 * 
 * @author Eric 'Siggy' Scott
 */
public class CloneDecoder<T extends Individual & Phenotype> implements Decoder<T, T> {

    @Override
    public T decode(T individual) {
        return individual;
    }
    
}
