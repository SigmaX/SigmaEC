package SigmaEC.measure;

import SigmaEC.meta.Fitness;
import SigmaEC.meta.Operator;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Objects;

/**
 * Uses a PopulationMetric to take measurements of a subpopulation without
 * modifying it.  This allows a measurement to be inserted anywhere in an
 * Operator pipeline.
 * 
 * @author Eric O. Scott
 */
public class MeasurementOperator<T extends Individual<F>, F extends Fitness> extends Operator<T> {
    public final static String P_METRIC = "metric";
    
    private final PopulationMetric<T, F> metric;

    public MeasurementOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        metric = parameters.getInstanceFromParameter(Parameters.push(base, P_METRIC), PopulationMetric.class);
        assert(repOK());
    }
    
    @Override
    public List<T> operate(final int run, final int generation, final List<T> subpopulation) {
        final Population<T, F> pop = new Population<T, F>(new List[] { subpopulation });
        metric.measurePopulation(run, generation, pop);
        assert(repOK());
        return subpopulation;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_METRIC != null
                && !P_METRIC.isEmpty()
                && metric != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MeasurementOperator))
            return false;
        final MeasurementOperator ref = (MeasurementOperator)o;
        return metric.equals(ref.metric);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.metric);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s]", this.getClass().getSimpleName(),
                P_METRIC, metric);
    }
    // </editor-fold>
}
