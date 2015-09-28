package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import java.util.Set;

/**
 * Defines a connection topology for a number of islands.
 * 
 * @author Eric O. Scott
 */
public abstract class Topology extends ContractObject {
    public abstract int numIslands();
    public abstract boolean isConnectected(final int i, final int j);
    public abstract Set<Integer> getNeighbors(final int i);
}
