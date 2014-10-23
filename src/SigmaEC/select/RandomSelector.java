package SigmaEC.select;

import SigmaEC.represent.Individual;
import java.util.List;
import java.util.Random;

/**
 * A Selector that just returns a random individual from the population.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RandomSelector<T extends Individual> extends Selector<T>
{
    Random random;
    
    public RandomSelector(final Random random) throws NullPointerException {
        if (random == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": random is null.");
        this.random = random;
    }
    
    @Override
    public T selectIndividual(final List<T> population) throws IllegalArgumentException, NullPointerException {
        if (population.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".selectIndividual(): population is empty.");
        else
            return population.get(random.nextInt(population.size()));
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RandomSelector))
            return false;
        return random.equals(((RandomSelector)o).random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.random != null ? this.random.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean repOK() {
        return (random != null);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: random=%s]", this.getClass().getSimpleName(), random);
    }
    // </editor-fold>
}
