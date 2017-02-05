package SigmaEC.meta.island;

import SigmaEC.ContractObject;
import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public abstract class IslandConfiguration<T extends Individual<F>, F extends Fitness> extends ContractObject {
    public abstract EvaluationOperator<T, ?, F> getEvaluator();
    public abstract FitnessComparator<T, F> getFitnessComparator();
}
