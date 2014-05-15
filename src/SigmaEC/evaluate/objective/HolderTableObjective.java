package SigmaEC.evaluate.objective;

import SigmaEC.represent.DoubleVectorPhenotype;

/**
 * Typical bounds are [-10, 10].
 * 
 * See http://www.sfu.ca/~ssurjano/holder.html
 * 
 * @author Eric 'Siggy' Scott
 */
public class HolderTableObjective extends ObjectiveFunction<DoubleVectorPhenotype> {
    
    public HolderTableObjective() {
        assert(repOK());
    }
    
    @Override
    public double fitness(final DoubleVectorPhenotype ind) {
        assert(ind.size() == 2);
        final double x1 = ind.getVector()[0];
        final double x2 = ind.getVector()[1];
        return -Math.abs(Math.sin(x1)*Math.cos(x2)*Math.exp(Math.abs(1-Math.sqrt(x1*x1 + x2*x2)/Math.PI)));
    }

    @Override
    public void setGeneration(int i) {
        // Do nothing
    }

    @Override
    public int getNumDimensions() { return 2; }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof HolderTableObjective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
    // </editor-fold>
}
