package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A data structure that holds the machinery necessary to evolve a subpopulation.
 * 
 * @author Eric O. Scott
 */
public class IslandConfiguration<T extends Individual<F>, P, F extends Fitness> extends ContractObject {
    final private int islandID;
    final private EvaluationOperator<T, P, F> evaluator;
    final private List<Operator<T>> operators;
    final private ObjectiveFunction<P, F> objective;
    final private FitnessComparator<T, F> fitnessComparator;
    final private boolean isDynamic;
    
    public IslandConfiguration(final int islandID,
            final EvaluationOperator<T, P, F> evaluator,
            final List<Operator<T>> operators,
            final ObjectiveFunction<P, F> objective,
            final FitnessComparator<T, F> fitnessComparator,
            final boolean isDynamic) {
        this.islandID = islandID;
        this.evaluator = evaluator;
        this.operators = new ArrayList<>(operators); // Defensive copy
        this.objective = objective;
        this.fitnessComparator = fitnessComparator;
        this.isDynamic = isDynamic;
    }
    
    public int getIslandID() { return islandID; }
    public boolean isDynamic() { return isDynamic; }
    public ObjectiveFunction<P, F> getObjective() { return objective; }

    public EvaluationOperator<T, P, F> getEvaluator() { return evaluator; }
    public FitnessComparator<T, F> getFitnessComparator() { return fitnessComparator; }
    public List<Operator<T>> getOperators() { return new ArrayList<>(operators); }

    // <editor-fold defaultstate="collapsed" desc="Stanard Methods">
    @Override
    public boolean repOK() {
        return islandID >= 0
                && evaluator != null
                && operators != null
                && !operators.isEmpty()
                && objective != null
                && fitnessComparator != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IslandConfiguration))
            return false;
        final IslandConfiguration ref = (IslandConfiguration)o;
        return islandID == ref.islandID
                && isDynamic == ref.isDynamic
                && evaluator.equals(ref.evaluator)
                && fitnessComparator.equals(ref.fitnessComparator)
                && objective.equals(ref.objective)
                && operators.equals(ref.operators);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.islandID;
        hash = 29 * hash + Objects.hashCode(this.evaluator);
        hash = 29 * hash + Objects.hashCode(this.operators);
        hash = 29 * hash + Objects.hashCode(this.objective);
        hash = 29 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 29 * hash + (this.isDynamic ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: islandID=%d, isDynamic=%B, evaluator=%s, fitnessComparator=%s, objective=%s, operators=%s]", this.getClass().getSimpleName(),
                islandID, isDynamic, evaluator, fitnessComparator, objective, operators);
    }
    // </editor-fold>
}
