package SigmaEC.represent.distance;

import SigmaEC.evaluate.objective.function.BooleanFunction;
import SigmaEC.evaluate.objective.function.TruthTableObjective;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class TruthTableDistanceMeasure extends DistanceMeasure<BooleanFunction> {

    public TruthTableDistanceMeasure(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    @Override
    public double distance(final BooleanFunction a, final BooleanFunction b) {
        final BooleanFunction target;
        if (a.arity() < b.arity())
            target = a;
        else if (a.arity() == b.arity())
            target = (a.numOutputs() < b.numOutputs()) ? a : b;
        else
            target = b;
        final BooleanFunction reference = (target == a) ? b : a;
        final TruthTableObjective objective = new TruthTableObjective(target);
        return objective.fitness(reference).asScalar();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof TruthTableDistanceMeasure);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
