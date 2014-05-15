package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorPhenotype;
import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TranslatedDoubleObjective extends ObjectiveFunction<DoubleVectorPhenotype>
{
    private final double[] offset;
    private final ObjectiveFunction<DoubleVectorPhenotype> objective;
    
    public TranslatedDoubleObjective(final double[] offset, final ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective is null.");
        if (offset == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offset is null.");
        if (Misc.containsNaNs(offset))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offset array contains one or more NaN values.");
        if (offset.length != objective.getNumDimensions())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offset and objective have different number of dimensions.");
        this.offset = Arrays.copyOf(offset, offset.length);
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions() {
        return offset.length;
    }
    
    public DoubleVectorPhenotype translate(final DoubleVectorPhenotype ind)
    {
        final double[] newPoint = ind.getVector();
        assert(newPoint.length == offset.length);
        for (int i = 0; i < offset.length; i++)
            newPoint[i] += offset[i];
        return new DoubleVectorPhenotype(newPoint);
    }
    
    @Override
    public double fitness(final DoubleVectorPhenotype ind) {
        return objective.fitness(translate(ind));
    }

    @Override
    public void setGeneration(final int i) {
        objective.setGeneration(i);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return offset != null
                && objective != null
                && offset.length == objective.getNumDimensions()
                && !Misc.containsNaNs(offset);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: offset=%s, objective=%s]", this.getClass().getSimpleName(), Arrays.toString(offset), objective);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TranslatedDoubleObjective))
            return false;
        final TranslatedDoubleObjective ref = (TranslatedDoubleObjective) o;
        return objective.equals(ref.objective)
                && Misc.doubleArrayEquals(offset, ref.offset);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Arrays.hashCode(this.offset);
        hash = 13 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
}
