package SigmaEC.util;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Option<T> {
    public static final Option NONE = new Option();
    private final T val;
    
    private Option() { this.val = null; }
    
    public Option(final T val) { assert(val != null); this.val = val; }
    
    public boolean isDefined() { return val != null; }
    
    public T get() { return val; }
    
    @Override
    public String toString() {
        return String.format("[Option: val=%s]", val.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Option))
            return false;
        final Option ref = (Option) o;
        return (val == null ? ref.val == null : val.equals(ref.val));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.val != null ? this.val.hashCode() : 0);
        return hash;
    }
}
