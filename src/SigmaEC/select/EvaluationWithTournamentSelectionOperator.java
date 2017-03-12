package SigmaEC.select;

import SigmaEC.evaluate.EvaluationOperator;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.FitnessComparator;
import SigmaEC.meta.Operator;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A combined selection-evaluation mechanism that reduces the number of average
 * fitness evaluations.
 * 
 * In binary tournament selection, each individual in the population has about a
 * 13.5% chance of never being chosen to compete in a tournament (specifically,
 * the probability approaches $1/e^t$ as the population size goes to infinity,
 * where $t$ is the tournament size).
 * 
 * A typical EA using will never need to know the fitness of these
 * individuals.  Thus, there is no reason to evaluate them.
 * 
 * This class implements this observation through an intelligent
 * evaluation-and-selection operator that only evaluates individuals that have
 * been chosen to compete in a tournament.
 * 
 * @author Eric O. Scott
 */
public class EvaluationWithTournamentSelectionOperator<T extends Individual<F>, P, F extends Fitness> extends Operator<T> {
    public final static String P_EVALUATOR = "evaluator";
    public final static String P_SURVIVOR_POP_SIZE = "survivorPopSize";
    public final static String P_TOURNAMENT_SIZE = "tournamentSize";
    public final static String P_COMPARATOR = "fitnessComparator";
    public final static String P_RANDOM = "random";
    
    private final EvaluationOperator<T, P, F> evaluator;
    private final Option<Integer> survivorPopSize;
    private final int tournamentSize;
    private final FitnessComparator<T, F> fitnessComparator;
    private final Random random;
    
    public EvaluationWithTournamentSelectionOperator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        evaluator = parameters.getInstanceFromParameter(Parameters.push(base, P_EVALUATOR), EvaluationOperator.class);
        survivorPopSize = parameters.getOptionalIntParameter(Parameters.push(base, P_SURVIVOR_POP_SIZE));
        tournamentSize = parameters.getIntParameter(Parameters.push(base, P_TOURNAMENT_SIZE));
        fitnessComparator = parameters.getInstanceFromParameter(Parameters.push(base, P_COMPARATOR), FitnessComparator.class);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), Random.class);
        assert(repOK());
    }
    
    public List<T> operate(final int run, final int step, final List<T> parentPopulation) {
        assert(run >= 0);
        assert(step >= 0);
        assert(parentPopulation != null);
        assert(!parentPopulation.isEmpty());
        assert(!Misc.containsNulls(parentPopulation));
        // Choose contestants
        final int numPairs = survivorPopSize.isDefined() ? survivorPopSize.get() : parentPopulation.size();
        final List<int[]> contestantTuples = chooseContestantTuples(parentPopulation, numPairs);
        final List<T> uniqueContestants = getUniqueContestants(contestantTuples, parentPopulation);
        
        // Evaluate contestants
        final List<T> evaluatedContestants = evaluator.operate(run, step, uniqueContestants);
        
        // Perform tournament
        return doTournaments(contestantTuples, evaluatedContestants);
    }
    
    private List<T> doTournaments(final List<int[]> contestantTuples, final List<T> uniqueContestants) {
        assert(contestantTuples != null);
        assert(!contestantTuples.isEmpty());
        assert(uniqueContestants != null);
        assert(!uniqueContestants.isEmpty());
        final Map<Integer, Integer> uniqueIDMap = contestantIndicesToUniqueIDs(contestantTuples);
        final List<T> winners = new ArrayList<>();
        for (final int[] contestants : contestantTuples)
            winners.add(doRound(contestants, uniqueContestants, uniqueIDMap));
        return winners;
    }
    
    private T doRound(final int[] contestants, final List<T> uniqueContestants, final Map<Integer, Integer> uniqueIDMap) {
        assert(contestants.length == tournamentSize);
        T best = null;
        for (int i = 0; i < contestants.length; i++) {
            assert(uniqueIDMap.containsKey(contestants[i]));
            final int uniqueID = uniqueIDMap.get(contestants[i]);
            assert(uniqueID >= 0);
            assert(uniqueID < uniqueContestants.size());
            final T contestant = uniqueContestants.get(uniqueID);
            assert(contestant.isEvaluated());
            if (fitnessComparator.betterThan(contestant, best)) {
                best = contestant;
            }
        }
        assert(best != null);
        assert(repOK());
        return best;
    }
    
    /** Choose sets of tournament contestants.
     * @return A list of contestant tuples.  Each element of the tuple is the
     * index of an individual in the population.  */
    private List<int[]> chooseContestantTuples(final List<T> parentPopulation, final int numPairs) {
        assert(parentPopulation != null);
        assert(!parentPopulation.isEmpty());
        assert(!Misc.containsNulls(parentPopulation));
        assert(numPairs > 0);
        final Selector<T> rSelector = new RandomSelector<T>(random);
        return new ArrayList<int[]>() {{
            for (int i = 0; i < numPairs; i++)
                add(rSelector.selectMultipleIndividualIndices(parentPopulation, tournamentSize));
        }};
    }
    
    /** @return A list of all the unique individuals that appear in the
     * contestant pool. */
    private List<T> getUniqueContestants(final List<int[]> contestantTuples, final List<T> population) {
        assert(contestantTuples != null);
        assert(!contestantTuples.isEmpty());
        assert(population != null);
        assert(!population.isEmpty());
        assert(contestantTuples.size() == (survivorPopSize.isDefined() ? survivorPopSize.get() : population.size()));
        assert(!Misc.containsNulls(contestantTuples));
        final Map<Integer, Integer> uniqueIDMap = contestantIndicesToUniqueIDs(contestantTuples);
        final List<T> result = new ArrayList<>();
        for (final Integer i : uniqueIDMap.values())
            result.add(population.get(i));
        assert(result.size() <= population.size());
        return result;
    }
    
    /** @return A list of all the unique individuals that appear in the
     * contestant pool (represented by their index in the population). */
    private Map<Integer, Integer> contestantIndicesToUniqueIDs(final List<int[]> contestantTuples) {
        assert(contestantTuples != null);
        assert(!contestantTuples.isEmpty());
        assert(!(survivorPopSize.isDefined() && contestantTuples.size() != survivorPopSize.get()));
        assert(!Misc.containsNulls(contestantTuples));
        final Map<Integer, Integer> indexToID = new HashMap<>();
        int nextID = 0;
        for (final int[] tuple : contestantTuples) {
            assert(tuple.length == tournamentSize);
            for (int i = 0; i < tuple.length; i++) {
                assert(tuple[i] >= 0);
                if (!indexToID.containsKey(tuple[i]))
                    indexToID.put(tuple[i], nextID++);
            }
        }
        return indexToID;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_COMPARATOR != null
                && !P_COMPARATOR.isEmpty()
                && P_EVALUATOR != null
                && !P_EVALUATOR.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_SURVIVOR_POP_SIZE != null
                && !P_SURVIVOR_POP_SIZE.isEmpty()
                && P_TOURNAMENT_SIZE != null
                && !P_TOURNAMENT_SIZE.isEmpty()
                && evaluator != null
                && survivorPopSize != null
                && !(survivorPopSize.isDefined() && survivorPopSize.get() < 1)
                && tournamentSize > 0
                && fitnessComparator != null
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EvaluationWithTournamentSelectionOperator))
            return false;
        final EvaluationWithTournamentSelectionOperator ref = (EvaluationWithTournamentSelectionOperator)o;
        return tournamentSize == ref.tournamentSize
                && survivorPopSize.equals(ref.survivorPopSize)
                && random.equals(ref.random)
                && fitnessComparator.equals(ref.fitnessComparator)
                && evaluator.equals(ref.evaluator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.evaluator);
        hash = 89 * hash + Objects.hashCode(this.survivorPopSize);
        hash = 89 * hash + this.tournamentSize;
        hash = 89 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 89 * hash + Objects.hashCode(this.random);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s, %s=%s, %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_TOURNAMENT_SIZE, tournamentSize,
                P_SURVIVOR_POP_SIZE, survivorPopSize,
                P_COMPARATOR, fitnessComparator,
                P_EVALUATOR, evaluator,
                P_RANDOM, random);
    }
    // </editor-fold>
}
