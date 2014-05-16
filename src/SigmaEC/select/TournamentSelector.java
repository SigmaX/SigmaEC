package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.Phenotype;
import SigmaEC.util.Parameters;
import java.util.List;
import java.util.Random;

/**
 * Tournament selection.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TournamentSelector<T extends Individual, P extends Phenotype> extends Selector<T>
{
    private final static String P_TOURNAMENT_SIZE = "tournamentSize";
    final private static String P_OBJECTIVE = "objective";
    final private static String P_DECODER = "decoder";
    final private static String P_RANDOM = "random";
    
    final private int tournamentSize;
    final private ObjectiveFunction<P> objective;
    final private Decoder<T, P> decoder;
    final private Random random;
    
    final private RandomSelector<T> contestantSelector;
    
    public TournamentSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        tournamentSize = parameters.getIntParameter(Parameters.push(base, P_TOURNAMENT_SIZE));
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        contestantSelector = new RandomSelector<T>(random);
        assert(repOK());
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
