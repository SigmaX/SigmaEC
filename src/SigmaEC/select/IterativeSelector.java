package SigmaEC.select;

import SigmaEC.represent.Individual;
import java.util.List;

/**
 * A selector that simply iterates through the population, in order.
 * 
 * @author Eric 'Siggy' Scott
 */
public class IterativeSelector<T extends Individual> extends Selector<T> {
    private int i = 0;
    
    public IterativeSelector() {}
    
    @Override
    public T selectIndividual(final List<T> population) {
        return population.get(i++%population.size());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return i >= 0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override 
    public boolean equals(final Object o) {
        if (!(o instanceof IterativeSelector))
            return false;
        return i == ((IterativeSelector)o).i;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.i;
        return hash;
    }
    
    // </editor-fold>
}
