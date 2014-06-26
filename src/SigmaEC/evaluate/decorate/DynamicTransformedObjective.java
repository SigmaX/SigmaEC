package SigmaEC.evaluate.decorate;

import SigmaEC.evaluate.TransformedObjectiveGenerator;
import SigmaEC.evaluate.TransformedObjectiveGenerator.Strategy;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import java.util.Random;

/**
 * A dynamic fitness function that applies a new random transformation to the
 * wrapped ObjectiveFunction at every generation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DynamicTransformedObjective extends ObjectiveFunction<DoubleVectorIndividual>
{
    private final TransformedObjectiveGenerator generator;
    private ObjectiveFunction<DoubleVectorIndividual> currentObjective;
    
    public DynamicTransformedObjective(final ObjectiveFunction<DoubleVectorIndividual> objective, final Strategy.TransformationStrategy transformationStrategy, final Random random) {
        if (objective == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": objective is null.");
        if (transformationStrategy == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": transformationStrategy is null.");
        if (random == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": random is null.");
        generator = new TransformedObjectiveGenerator(objective, transformationStrategy, random);
        currentObjective = objective;
        assert(repOK());
    }
    
    @Override
    public int getNumDimensions() {
        return currentObjective.getNumDimensions();
    }
    
    @Override
    public double fitness(final DoubleVectorIndividual ind) {
        return currentObjective.fitness(ind);
    }

    @Override
    public void setGeneration(final int i) {
        currentObjective = generator.getNewInstance();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return generator != null
                && currentObjective != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: generator=%s, currentObjective=%s]", this.getClass().getSimpleName(), generator, currentObjective.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DynamicTransformedObjective))
            return false;
        
        final DynamicTransformedObjective cRef = (DynamicTransformedObjective) o;
        return generator.equals(cRef.generator)
                && currentObjective.equals(cRef.currentObjective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.generator != null ? this.generator.hashCode() : 0);
        hash = 97 * hash + (this.currentObjective != null ? this.currentObjective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
