package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TranslatedDoubleObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    public final static String P_OFFSET = "offset";
    public final static String P_OBJECTIVE = "objective";
    
    private final double[] offset;
    private final ObjectiveFunction<DoubleVectorIndividual> objective;
    
    public TranslatedDoubleObjective(final Parameters parameters, final String base) throws IllegalStateException {
        assert(parameters != null);
        assert(base != null);
        
        this.offset = parameters.getDoubleArrayParameter(Parameters.push(base, P_OFFSET));
        this.objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        
        if (offset.length != objective.getNumDimensions())
            throw new IllegalStateException(String.format("%s: offset vector has %d elements, but objective has %d dimensions.", this.getClass().getSimpleName(), offset.length, objective.getNumDimensions()));
        
        assert(repOK());
    }
    
    public TranslatedDoubleObjective(final double[] offset, final ObjectiveFunction<DoubleVectorIndividual> objective) throws IllegalArgumentException {
        if (objective == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": objective is null.");
        if (offset == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": offset is null.");
        if (Misc.containsNaNs(offset))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offset array contains one or more NaN values.");
        if (!Misc.allFinite(offset))
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": offset array contains one or more infinite values.");
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
    
    private DoubleVectorIndividual translate(final DoubleVectorIndividual ind) {
        final double[] newPoint = ind.getGenomeArray();
        assert(newPoint.length == offset.length);
        for (int i = 0; i < offset.length; i++)
            newPoint[i] += offset[i];
        return new DoubleVectorIndividual(newPoint);
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
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
