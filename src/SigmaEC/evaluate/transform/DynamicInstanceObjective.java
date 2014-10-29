package SigmaEC.evaluate.transform;

import SigmaEC.evaluate.problemclass.ProblemClass;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 * Each generation, this dynamic fitness function changes into a completely
 * new instance of its ProblemClass.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DynamicInstanceObjective extends ObjectiveFunction<DoubleVectorIndividual> {
    private final static String P_CLASS = "problemclass";
    
    private final ProblemClass problemClass;
    private ObjectiveFunction<DoubleVectorIndividual> currentObjective;
    
    public DynamicInstanceObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        problemClass = parameters.getInstanceFromParameter(Parameters.push(base, P_CLASS), ProblemClass.class);
        currentObjective = problemClass.getNewInstance();
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
        currentObjective = problemClass.getNewInstance();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return problemClass != null
                && currentObjective != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: problemClass=%s, currentObjective=%s]", this.getClass().getSimpleName(), problemClass, currentObjective.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DynamicInstanceObjective))
            return false;
        
        final DynamicInstanceObjective cRef = (DynamicInstanceObjective) o;
        return problemClass.equals(cRef.problemClass)
                && currentObjective.equals(cRef.currentObjective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.problemClass != null ? this.problemClass.hashCode() : 0);
        hash = 97 * hash + (this.currentObjective != null ? this.currentObjective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
