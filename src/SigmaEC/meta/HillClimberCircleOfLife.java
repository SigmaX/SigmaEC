package SigmaEC.meta;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.operate.MutatingOperator;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Initializer;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class HillClimberCircleOfLife<T extends Individual, P> extends CircleOfLife<T>  {
    public final static String P_MUTATION_OPERATOR = "mutationOperator";
    public final static String P_EVALUATOR = "evaluator";
    public final static String P_STOPPING_CONDITION = "stoppingCondition";
    public final static String P_INITIALIZER = "initializer";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    
    private final MutatingOperator<T> mutationOperator;
    private final EvaluationOperator<T, ?> evaluator;
    private final Initializer<T> initializer;
    private final StoppingCondition<T> stoppingCondition;
    private final FitnessComparator<T> fitnessComparator;
    
    public HillClimberCircleOfLife(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        mutationOperator = parameters.getInstanceFromParameter(Parameters.push(base, P_MUTATION_OPERATOR), MutatingOperator.class);
        stoppingCondition = parameters.getInstanceFromParameter(Parameters.push(base, P_STOPPING_CONDITION), StoppingCondition.class);
        evaluator = parameters.getInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationOperator.class); 
        initializer = parameters.getInstanceFromParameter(Parameters.push(base, P_INITIALIZER), Initializer.class);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        assert(repOK());
    }

    @Override
    public EvolutionResult<T> evolve(final int run) {
        assert(run >= 0);
        T individual = evaluator.evaluate(initializer.generateIndividual());
        stoppingCondition.reset();
        int step = 0;
        while (!stoppingCondition.stopInd(individual, step)) {
            T candidate = evaluator.evaluate(mutationOperator.operateInd(individual, step));
            if (fitnessComparator.betterThan(candidate, individual))
                individual = candidate;
            step++;
            //TODO Add metrics
        }
        return new EvolutionResult<>(new Population<>(new ArrayList<T>()), individual, individual.getFitness());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
                && P_FITNESS_COMPARATOR != null
                && !P_FITNESS_COMPARATOR.isEmpty()
                && P_INITIALIZER != null
                && !P_INITIALIZER.isEmpty()
                && P_STOPPING_CONDITION != null
                && !P_STOPPING_CONDITION.isEmpty()
                && P_MUTATION_OPERATOR != null
                && !P_MUTATION_OPERATOR.isEmpty()
                && mutationOperator != null
                && evaluator != null
                && fitnessComparator != null
                && initializer != null
                && stoppingCondition != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HillClimberCircleOfLife))
            return false;
        final HillClimberCircleOfLife ref = (HillClimberCircleOfLife)o;
        return stoppingCondition.equals(ref.stoppingCondition)
                && mutationOperator.equals(ref.mutationOperator)
                && fitnessComparator.equals(ref.fitnessComparator)
                && evaluator.equals(ref.evaluator)
                && initializer.equals(ref.initializer);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.mutationOperator);
        hash = 53 * hash + Objects.hashCode(this.evaluator);
        hash = 53 * hash + Objects.hashCode(this.initializer);
        hash = 53 * hash + Objects.hashCode(this.stoppingCondition);
        hash = 53 * hash + Objects.hashCode(this.fitnessComparator);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_STOPPING_CONDITION, stoppingCondition,
                P_MUTATION_OPERATOR, mutationOperator,
                P_EVALUATOR, evaluator,
                P_FITNESS_COMPARATOR, fitnessComparator,
                P_INITIALIZER, initializer);
    }
    // </editor-fold>
}
