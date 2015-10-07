package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.linear.BitGene;
import SigmaEC.represent.linear.LinearGenomeIndividual;
import SigmaEC.select.FitnessComparator;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Prints the phenotypes of a population of DoubleVectorIndividual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class BitStringIndividualPopulationMetric<T extends LinearGenomeIndividual<BitGene>> extends PopulationMetric<T> {
    public final static String P_BEST_ONLY = "bestOnly";
    public final static String P_FITNESS_COMPARATOR = "fitnessComparator";
    public final static String P_BITS = "numBits";
    
    private final Option<FitnessComparator<T>> fitnessComparator;
    private final int numBits;
    
    public int numBits() { return numBits; }
    
    public boolean bestOnly() { return fitnessComparator.isDefined(); }
    
    public Option<FitnessComparator<T>> getFitnessComparator() { return fitnessComparator; }
    
    public BitStringIndividualPopulationMetric(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        final boolean bestOnly = parameters.getBooleanParameter(Parameters.push(base, P_BEST_ONLY));
        final Option<FitnessComparator<T>> fitnessComparatorOpt = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_FITNESS_COMPARATOR), FitnessComparator.class);
        if (!bestOnly && fitnessComparatorOpt.isDefined())
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, String.format("ignoring '%s' because '%s' is false.", Parameters.push(base, P_FITNESS_COMPARATOR), Parameters.push(base, P_BEST_ONLY)));
        if (bestOnly && !fitnessComparatorOpt.isDefined())
            throw new IllegalStateException(String.format("%s: '%s' is true, but '%s' is undefined.", this.getClass().getSimpleName(), Parameters.push(base, P_BEST_ONLY), Parameters.push(base, P_FITNESS_COMPARATOR)));
        fitnessComparator = bestOnly ? fitnessComparatorOpt : Option.NONE;
        numBits = parameters.getIntParameter(Parameters.push(base, P_BITS));
        if (numBits < 0)
            throw new IllegalStateException(String.format("%s: parameter '%s' is set to %d, but must be >= 0.", this.getClass().getSimpleName(), Parameters.push(base, P_BITS), numBits));
        assert(repOK());
    }
    
    @Override
    public MultipleStringMeasurement measurePopulation(final int run, final int generation, final Population<T> population) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(population != null);
        final List<String> arrays = new ArrayList<String>() {{
            for (int i = 0; i < population.numSuppopulations(); i++) {
                if (fitnessComparator.isDefined()) { // Record only the best individual in each subpopulation.
                    final T best = population.getBest(i, fitnessComparator.get());
                    add(individualToCSV(run, generation, i, best));
                }
                else // Record all individuals.
                    for(final T ind : population.getSubpopulation(i))
                        add(individualToCSV(run, generation, i, ind));
            }
        }};
        assert(repOK());
        return new MultipleStringMeasurement(run, generation, arrays);
    }
    
    private String individualToCSV(final int run, final int generation, final int subPop, final T ind) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(subPop >= 0);
        assert(ind != null);
        assert(ind.size() == numBits);
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d, %d, %d, %d, %f", run, generation, subPop, ind.getID(), ind.getFitness()));
        for(final BitGene g : ind.getGenome())
            sb.append(", ").append(g.value ? 1 : 0);
        return sb.toString();
    }

    @Override
    public String csvHeader() {
        final StringBuilder sb = new StringBuilder()
                .append("run, generation, subpopulation, individualID, fitness");
        for (int i = 0; i < numBits; i++)
            sb.append(", V").append(i);
        assert(repOK());
        return sb.toString();
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }
    
    @Override
    public void close() { }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_BEST_ONLY != null
                && !P_BEST_ONLY.isEmpty()
                && P_BITS != null
                && !P_BITS.isEmpty()
                && P_FITNESS_COMPARATOR != null
                && !P_FITNESS_COMPARATOR.isEmpty()
                && numBits > 0
                && fitnessComparator != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%B, %s=%s]", this.getClass().getSimpleName(),
                P_BITS, numBits,
                P_BEST_ONLY, bestOnly(),
                P_FITNESS_COMPARATOR, fitnessComparator);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BitStringIndividualPopulationMetric))
            return false;
        final BitStringIndividualPopulationMetric ref = (BitStringIndividualPopulationMetric)o;
        return numBits == ref.numBits
                && fitnessComparator.equals(ref.fitnessComparator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.fitnessComparator);
        hash = 97 * hash + this.numBits;
        return hash;
    }
    //</editor-fold>
}
