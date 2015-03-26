package SigmaEC.represent.linear;

/**
 *
 * @author Eric O. Scott
 */
public class IntGene implements Gene {
    final public int value;
    public IntGene(int value) { this.value = value; }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof IntGene))
            return false;
        final IntGene ref = (IntGene) o;
        return this.value == ref.value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.value;
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: value=%d]", this.getClass().getSimpleName(), value);
    }
    
    public final boolean repOK() { return true; }
    // </editor-fold>
}
