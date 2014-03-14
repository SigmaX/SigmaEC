package SigmaEC.evaluate;

import SigmaEC.represent.DoubleVectorPhenotype;
import java.util.Arrays;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TranslatedDoubleObjective implements ObjectiveFunction<DoubleVectorPhenotype>
{
    private final double[] offset;
    private final ObjectiveFunction<DoubleVectorPhenotype> objective;
    
    public TranslatedDoubleObjective(final double[] offset, final ObjectiveFunction<DoubleVectorPhenotype> objective) throws IllegalArgumentException {
        if (objective == null)
            throw new IllegalArgumentException("TranslatedDoubleObjective: objective is null.");
        if (offset == null)
            throw new IllegalArgumentException("TranslatedDoubleObjective: offset is null.");
        if (offset.length != objective.getNumDimensions())
            throw new IllegalArgumentException("TranslatedDoubleObjective: offset and objective have different number of dimensions.");
        this.offset = Arrays.copyOf(offset, offset.length);
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions()
    {
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
    public double fitness(DoubleVectorPhenotype ind)
    {
        return objective.fitness(translate(ind));
    }

    @Override
    public void setGeneration(int i) {
        objective.setGeneration(i);
    }

    @Override
    public final boolean repOK() {
        return offset != null
                && objective != null
                && offset.length == objective.getNumDimensions();
    }
    
    @Override
    public String toString() {
        return String.format("[%s: offset=%s, objective=%s]", this.getClass().getSimpleName(), Arrays.toString(offset), objective);
    }
}
