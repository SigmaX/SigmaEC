package SigmaEC.evaluate.decorate;

import SigmaEC.SRandom;
import SigmaEC.evaluate.ObjectiveGenerator;
import SigmaEC.evaluate.TransformedObjectiveGenerator;
import SigmaEC.evaluate.TransformedObjectiveGenerator.Strategy;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Parameters;
import java.util.Random;

/**
 * A dynamic fitness function that applies a new random transformation to the
 * wrapped ObjectiveFunction at every generation.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DynamicTransformedObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_OBJECTIVE = "objective";
    private final static String P_STRATEGY = "strategy";
    private final static String P_RANDOM = "random";
    
    private final ObjectiveGenerator generator;
    private ObjectiveFunction<DoubleVectorIndividual> currentObjective;
    
    public DynamicTransformedObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        currentObjective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        final Strategy.TransformationStrategy strategy = parameters.getInstanceFromParameter(Parameters.push(base, P_STRATEGY), Strategy.TransformationStrategy.class);
        final Random random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        generator = new TransformedObjectiveGenerator(currentObjective, strategy, random);
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
