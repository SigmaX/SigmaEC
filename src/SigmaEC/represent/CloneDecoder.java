package SigmaEC.represent;

import SigmaEC.util.Parameters;

/**
 * A dummy decoder for when the genotype and phenotype are identical.
 * 
 * @author Eric 'Siggy' Scott
 */
public class CloneDecoder<T extends Individual> extends Decoder<T, T> {

    public CloneDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public T decode(T individual) {
        return individual;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof CloneDecoder);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
