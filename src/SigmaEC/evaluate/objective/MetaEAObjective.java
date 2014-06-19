package SigmaEC.evaluate.objective;

import SigmaEC.experiment.Experiment;
import SigmaEC.represent.Phenotype;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class MetaEAObjective<P extends Phenotype> extends ObjectiveFunction<P> {
    public final static String P_EXPERIMENT = "experiment";
    
    private final Experiment<Double> experiment;
    
    public MetaEAObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        experiment = parameters.getInstanceFromParameter(Parameters.push(base, P_EXPERIMENT), Experiment.class);
        assert(repOK());
    }
    
    @Override
    public double fitness(final P ind) {
        experiment.run();
        return experiment.getResult();
    }

    @Override
    public void setGeneration(int i) { }

    @Override
    public int getNumDimensions() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ": dimensions not defined for this objective.");
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return experiment != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MetaEAObjective))
            return false;
        final MetaEAObjective ref = (MetaEAObjective) o;
        return experiment.equals(ref.experiment);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.experiment != null ? this.experiment.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: experiment=%s]", this.getClass().getSimpleName(), experiment.toString());
    }
    // </editor-fold>
}
