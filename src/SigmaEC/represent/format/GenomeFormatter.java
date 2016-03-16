package SigmaEC.represent.format;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public abstract class GenomeFormatter<T extends Individual> extends ContractObject {
    public abstract String genomeToString(final T individual);
    public abstract T stringToGenome(final String individual);
}
