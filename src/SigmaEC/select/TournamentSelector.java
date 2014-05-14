package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Tournament selection.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TournamentSelector<T extends Individual, P extends Phenotype> extends Selector<T>
{
    final private int tournamentSize;
    final private Random random;
    final private RandomSelector<T> contestantSelector;
    final private ObjectiveFunction<? super P> objective;
    final private Decoder<T, P> decoder;
    
    public TournamentSelector(final ObjectiveFunction<? super P> obj, final Decoder<T, P> decoder, final Random random, final int tournamentSize) throws NullPointerException, IllegalArgumentException
    {
        if (obj == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": obj is null.");
        if (decoder == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": decoder is null.");
        if (random == null)
            throw new NullPointerException(this.getClass().getSimpleName() + ": random is null.");
        else if (tournamentSize <= 0)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": tournamentSize is less than 1.");
        
        this.objective = obj;
        this.decoder = decoder;
        this.tournamentSize = tournamentSize;
        this.random = random;
        this.contestantSelector = new RandomSelector<T>(random);
    }
    
    private TournamentSelector(final Builder<T, P> builder) {
        this(builder.objective, builder.decoder, builder.random, builder.tournamentSize);
    }
    
    public static class Builder<T extends Individual, P extends Phenotype> implements Selector.SelectorBuilder<T> {
        private final static String P_TOURNAMENT_SIZE = "tournamentSize";
        private int tournamentSize;
        private Random random;
        private ObjectiveFunction<? super P> objective;
        private Decoder<T, P> decoder;
        
        public Builder(final Properties properties, final String base) {
            assert(properties != null);
            assert(base != null);
            
            tournamentSize = Parameters.getIntParameter(properties, Parameters.push(base, P_TOURNAMENT_SIZE));
            // The remaining fields must be set by the caller before build() is called.
        }

        @Override
        public TournamentSelector<T, P> build() {
            return new TournamentSelector<T, P>(this);
        }
        
        @Override
        public Builder<T, P> decoder(final Decoder decoder) {
            this.decoder = decoder;
            return this;
        }

        @Override
        public Builder<T, P> objective(final ObjectiveFunction objective) {
            this.objective = objective;
            return this;
        }

        @Override
        public Builder<T, P> random(final Random random) {
            this.random = random;
            return this;
        }
        
        public Builder<T, P> tournamentSize(final int tournamentSize) {
            this.tournamentSize = tournamentSize;
            return this;
        }
    }
    
    @Override
    public T selectIndividual(final List<T> population) throws NullPointerException, IllegalArgumentException
    {
        if (population.isEmpty())
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".selectIndividual: population is empty.");
        
        final List<T> contestants = contestantSelector.selectMultipleIndividuals(population, tournamentSize);
        final TruncationSelector<T, P> finalSelector = new TruncationSelector(objective, decoder);
        final T selectedIndividual = finalSelector.selectIndividual(contestants);
        
        assert(selectedIndividual != null);
        assert(repOK());
        return selectedIndividual;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return tournamentSize > 0
                && contestantSelector != null
                && objective != null;
    }
    @Override
    public String toString()
    {
        return String.format("[TournamentSelector: TournamentSize=%d, Random=%s, Objective=%s", tournamentSize, random, objective);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof TournamentSelector))
            return false;
        
        TournamentSelector cRef = (TournamentSelector) o;
        return tournamentSize == cRef.tournamentSize
                && random.equals(cRef.random)
                && objective.equals(cRef.objective);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.tournamentSize;
        hash = 11 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 11 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
