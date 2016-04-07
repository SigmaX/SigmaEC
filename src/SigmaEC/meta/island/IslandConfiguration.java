package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.represent.Individual;
import SigmaEC.select.FitnessComparator;

/**
 *
 * @author Eric O. Scott
 */
public abstract class IslandConfiguration<T extends Individual> extends ContractObject {
    public abstract EvaluationOperator<T, ?> getEvaluator();
    public abstract FitnessComparator<T> getFitnessComparator();
}
