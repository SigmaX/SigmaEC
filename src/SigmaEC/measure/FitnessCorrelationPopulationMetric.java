package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;

/**
 *
 * @author Eric O. Scott
 */
public class FitnessCorrelationPopulationMetric<T extends Individual> extends PopulationMetric<T> {

    @Override
    public Measurement measurePopulation(final int run, final int step, final Population<T> population) {
        ping(step, population);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() { }

    @Override
    public void flush() { }

    @Override
    public void close() { }

    @Override
    public String csvHeader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ping(int step, Population<T> population) { }

    @Override
    public final boolean repOK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(final Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
