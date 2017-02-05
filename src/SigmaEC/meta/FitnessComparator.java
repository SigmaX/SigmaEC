package SigmaEC.meta;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import java.util.Comparator;

/**
 * A Comparator that encodes whether we are minimizing or maximizing.
 * Typically you want one Comparator object per EA, to be used everywhere
 * fitnesses need to be compared.
 * 
 * @author Eric O. Scott
 */
public abstract class FitnessComparator<T extends Individual, F extends Fitness> extends ContractObject implements Comparator<T> {
    public abstract boolean betterThan(final T ind, final T ind1);
    public abstract FitnessComparator<T, F> invert();
}
