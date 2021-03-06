package SigmaEC.evaluate.objective.real;

import SigmaEC.evaluate.ScalarFitness;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * Typical bounds are [-10, 10].
 * 
 * See http://www.sfu.ca/~ssurjano/holder.html
 * 
 * @author Eric 'Siggy' Scott
 */
public class HolderTableObjective extends ObjectiveFunction<DoubleVectorIndividual, ScalarFitness> {
    
    public HolderTableObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public ScalarFitness fitness(final DoubleVectorIndividual ind) {
        assert(ind.size() == 2);
        final double[] genome = ind.getGenomeArray();
        final double x1 = genome[0];
        final double x2 = genome[1];
        return new ScalarFitness(-Math.abs(Math.sin(x1)*Math.cos(x2)*Math.exp(Math.abs(1-Math.sqrt(x1*x1 + x2*x2)/Math.PI))));
    }

    @Override
    public void setStep(int i) {
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
