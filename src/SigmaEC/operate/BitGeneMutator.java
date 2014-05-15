package SigmaEC.operate;

import SigmaEC.represent.BitGene;
import java.util.Properties;
import java.util.Random;

/**
 * Bit flip mutation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitGeneMutator extends Mutator<BitGene> {
    
    private BitGeneMutator(final Builder builder) {
        assert(repOK());
    }
    
    @Override
    public BitGene mutate(final BitGene gene) {
        return new BitGene(!gene.value);
    }
    
    public static class Builder implements MutatorBuilder<BitGene> {
        
        public Builder(final Properties properties, final String base) { }
        
        @Override
        public MutatorBuilder<BitGene> random(final Random random) {
            // This mutator doesn' need PRNGs.
            return this;
        }

        @Override
        public Mutator<BitGene> build() {
            return new BitGeneMutator(this);
        }
    
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
