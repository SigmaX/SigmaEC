package SigmaEC.represent.linear;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class BitGene implements Gene {
    final public boolean value;
    public BitGene(boolean value) { this.value = value; }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BitGene))
            return false;
        final BitGene ref = (BitGene) o;
        return this.value == ref.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.value ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: value=%B]", this.getClass().getSimpleName(), value);
    }
    
    public final boolean repOK() { return true; }
    // </editor-fold>
    
}
