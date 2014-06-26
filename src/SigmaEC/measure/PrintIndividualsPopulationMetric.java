package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
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
    public MultipleStringMeasurement measurePopulation(int run, int generation, final List<T> population) {
        final List<String> individualStrings = new ArrayList<String>(population.size()) {{
           for (final Individual ind : population)
               add(ind.toString());
        }};
        return new MultipleStringMeasurement(run, generation, individualStrings);
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
