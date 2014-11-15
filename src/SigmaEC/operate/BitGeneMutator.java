package SigmaEC.operate;

import SigmaEC.represent.linear.BitGene;
import SigmaEC.util.Parameters;

/**
 * Bit flip mutation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitGeneMutator extends Mutator<BitGene> {
    
    public BitGeneMutator(final Parameters parameters, final String base) {
        assert(repOK());
    }
    
    @Override
    public BitGene mutate(final BitGene gene) {
        return new BitGene(!gene.value);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return true;
    }
    
    @Override
    final public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override
    final public boolean equals(final Object o) {
        return (o instanceof BitGeneMutator);
    }
    
    @Override    
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    //</editor-fold>
}
