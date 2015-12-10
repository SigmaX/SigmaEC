package SigmaEC.operate;

import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class CloneMator<T extends LinearGenomeIndividual> extends Mator<T> {
    
    public CloneMator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        assert(repOK());
    }
    
    @Override
    public List<T> mate(final List<T> parents) {
        assert(parents != null);
        assert(parents.size() == 1);
        assert(!Misc.containsNulls(parents));
        final T parent = parents.get(0);
        final T child = (T) parents.get(0).create(parent.getGenome(), new ArrayList<T>() {{ add(parent); }});
        assert(repOK());
        return new ArrayList<T>() {{ add(child); }};
    }

    @Override
    public int getNumParents() {
        return 1;
    }

    @Override
    public int getNumChildren() {
        return 1;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof CloneMator);
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
