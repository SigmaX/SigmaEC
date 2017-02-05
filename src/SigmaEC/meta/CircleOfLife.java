package SigmaEC.meta;

import SigmaEC.ContractObject;
import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A main evolution loop
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class CircleOfLife<T extends Individual<F>, F extends Fitness> extends ContractObject {
    public abstract EvolutionResult<T, F> evolve(int run);
    
    public static class EvolutionResult<T extends Individual<F>, F extends Fitness> extends ContractObject {
        final private Population<T, F> finalPopulation;
        final private List<T> bestIndividuals;
        final private List<F> bestFitnesses;
        
        public Population<T, F> getFinalPopulation() { return finalPopulation; }
        public List<T> getBestIndividual() { return bestIndividuals; }
        public List<F> getBestFitness() { return bestFitnesses; }
        
        public EvolutionResult(final Population<T, F> finalPopulation, final T bestIndividual, final F bestFitness) {
            assert(finalPopulation != null);
            assert(bestIndividual != null);
            assert(bestFitness != null);
            this.finalPopulation = finalPopulation;
            this.bestIndividuals = new ArrayList<T>() {{ add(bestIndividual); }};
            this.bestFitnesses = new ArrayList<F>() {{ add(bestFitness); }};
            assert(repOK());
        }
        
        public EvolutionResult(final Population<T, F> finalPopulation, final List<T> bestIndividuals, final List<F> bestFitnesses) {
            assert(finalPopulation != null);
            assert(bestIndividuals != null);
            assert(!Misc.containsNulls(bestFitnesses));
            assert(!Misc.containsNulls(bestIndividuals));
            assert(bestIndividuals.size() == bestFitnesses.size());
            this.finalPopulation = finalPopulation;
            this.bestIndividuals = bestIndividuals;
            this.bestFitnesses = bestFitnesses;
            assert(repOK());
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return finalPopulation != null
                    && bestIndividuals != null
                    && bestFitnesses != null
                    && bestIndividuals.size() == bestFitnesses.size()
                    && !Misc.containsNulls(bestIndividuals)
                    && !Misc.containsNulls(bestFitnesses);
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof EvolutionResult))
                return false;
            final EvolutionResult ref = (EvolutionResult) o;
            return bestIndividuals.equals(ref.bestIndividuals)
                    && bestFitnesses.equals(ref.bestFitnesses)
                    && finalPopulation.equals(ref.finalPopulation);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 43 * hash + Objects.hashCode(this.finalPopulation);
            hash = 43 * hash + Objects.hashCode(this.bestIndividuals);
            hash = 43 * hash + Objects.hashCode(this.bestFitnesses);
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: bestFitnesses=%f, bestIndividuals=%s, finalPopulation=%s]", this.getClass().getSimpleName(), bestFitnesses, bestIndividuals, finalPopulation);
        }
        // </editor-fold>
        
    }
}
