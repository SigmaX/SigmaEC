package SigmaEC.app.lsystems;

import SigmaEC.ContractObject;

/**
 * A single re-writing rule for a simple Lindenmayer system.
 * 
 * @author Eric 'Siggy' Scott
 */
public class LRule extends ContractObject {
    public final static String PRODUCTION_SYMBOL = ">";
    
    private final String predecessor;
    private final String successor;
    
    public String getPredecessor() {
        return predecessor;
    }
    
    public String getSuccessor() {
        return successor;
    }
    
    public LRule(final String predecessor, final String successor) {
        assert(predecessor != null);
        assert(successor != null);
        if (predecessor.isEmpty())
            throw new IllegalArgumentException(String.format("%s: predecessor is empty.  Must contain exactly one character.", this.getClass().getSimpleName()));
        if (predecessor.length() > 1)
            throw new IllegalArgumentException(String.format("%s: predecessor contains multiple symbols.  Must contain exactly one character.", this.getClass().getSimpleName()));
        if (predecessor.contains(PRODUCTION_SYMBOL))
            throw new IllegalArgumentException(String.format("%s: predecessor contains the production symbol.", this.getClass().getSimpleName()));
        if (successor.contains(PRODUCTION_SYMBOL))
            throw new IllegalArgumentException(String.format("%s: successor contains the production symbol.", this.getClass().getSimpleName()));
        this.predecessor = predecessor;
        this.successor = successor;
        assert(repOK());
    }
    
    public static LRule fromString(final String ruleString) {
        assert(ruleString != null);
        if (!ruleString.contains(PRODUCTION_SYMBOL))
            throw new IllegalArgumentException(String.format("%s: rule string does not contain the production character ('%s').", LRule.class.getSimpleName(), PRODUCTION_SYMBOL));
        final String[] parts = ruleString.split(PRODUCTION_SYMBOL);
        if (parts.length != 2)
            throw new IllegalArgumentException(String.format("%s: rule string contains more than one instance of the production character ('%s').  It must contain exactly one instance.", LRule.class.getSimpleName(), PRODUCTION_SYMBOL));
        return new LRule(parts[0], parts[1]);
    }
    
    /** Apply the rule by reading symbols from left to right and replacing them
     *  with the successor if they match the predecessor.
     */
    public String apply(final String input) {
        assert(input != null);
        
        final StringBuilder result = new StringBuilder();
        for (final char c : input.toCharArray()) {
            if (predecessor.equals(String.valueOf(c)))
                result.append(successor);
            else
                result.append(c);
        }
        assert(repOK());
        return result.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return predecessor != null
                && predecessor.length() == 1
                && !predecessor.contains(PRODUCTION_SYMBOL)
                && successor != null
                && !successor.contains(PRODUCTION_SYMBOL)
                && PRODUCTION_SYMBOL != null
                && PRODUCTION_SYMBOL.length() == 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LRule))
            return false;
        final LRule ref = (LRule)o;
        return predecessor.equals(ref.predecessor)
                && successor.equals(ref.successor);
    }

    @Override
    public String toString() {
        return String.format("[%s: predecessor=%s, successor=%s]", this.getClass().getSimpleName(), predecessor, successor);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
    // </editor-fold>
}
