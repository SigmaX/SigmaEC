package SigmaEC.measure;

import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;

/**
 * Decorates a PopulationMetric with the ability to remember the most recent
 * Measurement it took.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MemoryPopulationMetric<T extends Individual> extends PopulationMetric<T> {
    final private PopulationMetric<T> wrappedMetric;
    
    private Measurement mostRecentMeasurement;
    
    public MemoryPopulationMetric(final PopulationMetric<T> wrappedMetric) throws IllegalArgumentException
    {
        if (wrappedMetric == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": wrappedMetric is null.");
        this.wrappedMetric = wrappedMetric;
        assert(repOK());
    }
    
    /** @return The value that was returned by the last call of this.measurePopulation(). */
    public Measurement getMostRecentMeasurement() { return mostRecentMeasurement; }
    
    @Override
    public synchronized Measurement measurePopulation(final int run, final int step, final Population<T> population) {
        assert(population != null);
        ping(step, population);
        final Measurement measurement = wrappedMetric.measurePopulation(run, step, population);
        mostRecentMeasurement = measurement;
        assert(repOK());
        return measurement;
    }

    @Override
    public void ping(int step, Population<T> population) {
        // Do nothing
    }

    @Override
    public String csvHeader() {
        return wrappedMetric.csvHeader();
    }

    @Override
    public void reset() {
        wrappedMetric.reset();
        mostRecentMeasurement = null;
    }

    @Override
    public void flush() { wrappedMetric.flush(); }

    @Override
    public void close() {
        wrappedMetric.close();
        assert(repOK());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return wrappedMetric != null;
    }
    
    @Override
    public String toString() {
        return String.format("[%s: mostRecentMeasurement=%s, wrappedMetric=%s]", this.getClass().getSimpleName(), mostRecentMeasurement, wrappedMetric);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MemoryPopulationMetric))
            return false;
        
        final MemoryPopulationMetric cRef = (MemoryPopulationMetric) o;
        return mostRecentMeasurement.equals(cRef.mostRecentMeasurement)
                && wrappedMetric.equals(cRef.wrappedMetric);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.wrappedMetric != null ? this.wrappedMetric.hashCode() : 0);
        hash = 41 * hash + (this.mostRecentMeasurement != null ? this.mostRecentMeasurement.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
