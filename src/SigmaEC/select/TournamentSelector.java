package SigmaEC.select;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.util.Parameters;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Tournament selection.
 * 
 * @author Eric 'Siggy' Scott
 */
public class TournamentSelector<T extends Individual, P> extends Selector<T>
{
    private final static String P_TOURNAMENT_SIZE = "tournamentSize";
    final private static String P_OBJECTIVE = "objective";
    final private static String P_COMPARATOR = "fitnessComparator";
    final private static String P_DECODER = "decoder";
    final private static String P_RANDOM = "random";
    
    final private int tournamentSize;
    final private ObjectiveFunction<P> objective;
    private final Comparator<Double> fitnessComparator;
    final private Decoder<T, P> decoder;
    final private Random random;
    
    final private RandomSelector<T> contestantSelector;
    
    public TournamentSelector(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        tournamentSize = parameters.getIntParameter(Parameters.push(base, P_TOURNAMENT_SIZE));
        objective = parameters.getInstanceFromParameter(Parameters.push(base, P_OBJECTIVE), ObjectiveFunction.class);
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), Comparator.class);
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
        final TruncationSelector<T, P> finalSelector = new TruncationSelector(objective, decoder, fitnessComparator);
        final T selectedIndividual = finalSelector.selectIndividual(contestants);
        
        assert(selectedIndividual != null);
        assert(repOK());
        return selectedIndividual;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return tournamentSize > 0
                && contestantSelector != null
                && objective != null
                && fitnessComparator != null
                && P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_DECODER != null
                && !P_DECODER.isEmpty()
                && P_OBJECTIVE != null
                && !P_OBJECTIVE.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_TOURNAMENT_SIZE != null
                && !P_TOURNAMENT_SIZE.isEmpty();
    }
    @Override
    public String toString() {
        return String.format("[%s: tournamentSize=%d, random=%s, objective=%s, fitnessComparator=%s]", this.getClass().getSimpleName(), tournamentSize, random, objective, fitnessComparator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof TournamentSelector))
            return false;
        
        TournamentSelector cRef = (TournamentSelector) o;
        return tournamentSize == cRef.tournamentSize
                && random.equals(cRef.random)
                && objective.equals(cRef.objective)
                && fitnessComparator.equals(cRef.fitnessComparator)
                && decoder.equals(cRef.decoder)
                && contestantSelector.equals(cRef.contestantSelector);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.tournamentSize;
        hash = 61 * hash + (this.objective != null ? this.objective.hashCode() : 0);
        hash = 61 * hash + (this.fitnessComparator != null ? this.fitnessComparator.hashCode() : 0);
        hash = 61 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 61 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 61 * hash + (this.contestantSelector != null ? this.contestantSelector.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
