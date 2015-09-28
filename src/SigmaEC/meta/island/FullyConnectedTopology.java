package SigmaEC.meta.island;

import SigmaEC.util.Parameters;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a fully connected network topology for use with an island model.
 * 
 * @author Eric O. Scott
 */
public class FullyConnectedTopology extends Topology {
    public final static String P_ISLANDS = "numIslands";
    
    private final int numIslands;
    
    public FullyConnectedTopology(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        numIslands = parameters.getIntParameter(Parameters.push(base, P_ISLANDS));
        if (numIslands <= 0)
            throw new IllegalStateException(String.format("%s: parameter '%s' was %d.  Must be greater than zero.", this.getClass().getSimpleName(), Parameters.push(base, P_ISLANDS), numIslands));
        assert(repOK());
    }
    
    @Override
    public int numIslands() {
        return numIslands;
    }

    @Override
    public boolean isConnectected(final int i, final int j) {
        assert(i >= 0);
        assert(j >= 0);
        assert(i < numIslands);
        assert(j < numIslands);
        return (i != j);
    }

    @Override
    public Set<Integer> getNeighbors(final int i) {
        assert(i >= 0);
        assert(i < numIslands);
        final Set<Integer> neighbors = new HashSet<Integer>() {{
           for (int j = 0; j < numIslands; j++)
               if (j != i)
                   add(j);
        }};
        assert(repOK());
        return neighbors;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_ISLANDS != null
                && !P_ISLANDS.isEmpty()
                && numIslands > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof FullyConnectedTopology))
            return false;
        final FullyConnectedTopology ref = (FullyConnectedTopology)o;
        return numIslands == ref.numIslands;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.numIslands;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d]", this.getClass().getSimpleName(),
                P_ISLANDS, numIslands);
    }
    // </editor-fold>
}
