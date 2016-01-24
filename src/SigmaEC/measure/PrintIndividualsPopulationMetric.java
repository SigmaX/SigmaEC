package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 * Prints a population by calling toString() on every individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class PrintIndividualsPopulationMetric<T extends Individual> extends PopulationMetric<T>
{
    public PrintIndividualsPopulationMetric() {}
    
    @Override
    public MultipleStringMeasurement measurePopulation(final int run, final int step, final Population<T> population) {
        final List<String> individualStrings = new ArrayList<>();
        for (int i = 0; i < population.numSuppopulations(); i++) {
            for (final T ind : population.getSubpopulation(i))
                individualStrings.add(i + ", " + ind.toString());
        }
        return new MultipleStringMeasurement(run, step, individualStrings);
    }

    @Override
    public String csvHeader() {
        return "individual";
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK() {
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof PrintIndividualsPopulationMetric);
    }

    @Override
    public int hashCode() {
        return 7;
    }
    //</editor-fold>
}
