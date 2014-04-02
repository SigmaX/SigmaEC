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
    
    public RandomSelector(Random random) throws NullPointerException
    {
        if (random == null)
            throw new NullPointerException("RandomSelector: random is null.");
        this.random = random;
    }
    
    @Override
    public T selectIndividual(List<T> population) throws IllegalArgumentException, NullPointerException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException("RandomSelector.selectIndividual(): population is empty.");
        else
            return population.get(random.nextInt(population.size()));
    }
    
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
    public boolean repOK()
    {
        return (random != null);
    }
    
    @Override
    public String toString()
    {
        return "[RandomSelector: Random=" + random.toString() + "]";
    }
}
