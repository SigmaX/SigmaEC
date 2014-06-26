package SigmaEC;

import SigmaEC.represent.Individual;
import SigmaEC.util.Misc;
import java.util.List;

/**
 * A main evolution loop
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class CircleOfLife<T extends Individual> extends ContractObject
{
    /** Takes a population of individuals and evolves them */
    public abstract EvolutionResult<T> evolve(int run, List<T> population);
    
    public static class EvolutionResult<T extends Individual> extends ContractObject {
        final private List<T> finalPopulation;
        final private T bestIndividual;
        final private double bestFitness;
        
        public List<T> getFinalPopulation() { return finalPopulation; }
        public T getBestIndividual() { return bestIndividual; }
        public double getBestFitness() { return bestFitness; }
        
        public EvolutionResult(final List<T> finalPopulation, final T bestIndividual, final double bestFitness) {
            assert(finalPopulation != null);
            assert(bestIndividual != null);
            assert(!Double.isNaN(bestFitness));
            this.finalPopulation = finalPopulation;
            this.bestIndividual = bestIndividual;
            this.bestFitness = bestFitness;
            assert(repOK());
        }

        // <editor-fold defaultstate="collapsed" desc="Standard Methods">
        @Override
        public final boolean repOK() {
            return finalPopulation != null
                    && bestIndividual != null
                    && !Double.isNaN(bestFitness);
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof EvolutionResult))
                return false;
            final EvolutionResult ref = (EvolutionResult) o;
            return bestIndividual.equals(ref.bestIndividual)
                    && Misc.doubleEquals(bestFitness, ref.bestFitness)
                    && finalPopulation.equals(ref.finalPopulation);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.finalPopulation != null ? this.finalPopulation.hashCode() : 0);
            hash = 59 * hash + (this.bestIndividual != null ? this.bestIndividual.hashCode() : 0);
            hash = 59 * hash + (int) (Double.doubleToLongBits(this.bestFitness) ^ (Double.doubleToLongBits(this.bestFitness) >>> 32));
            return hash;
        }

        @Override
        public String toString() {
            return String.format("[%s: bestFitness=%f, bestIndividual=%s, finalPopulation=%s]", bestFitness, bestIndividual, finalPopulation);
        }
        // </editor-fold>
        
    }
}
