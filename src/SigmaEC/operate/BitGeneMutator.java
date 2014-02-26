package SigmaEC.operate;

import SigmaEC.represent.BitGene;

/**
 * Bit flip mutation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitGeneMutator implements Mutator<BitGene> {
    
    public BitGeneMutator() {
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
    final public boolean equals(Object o) {
        return (o instanceof BitGeneMutator);
    }
    
    @Override    
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    //</editor-fold>
}
