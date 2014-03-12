package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
import java.util.List;

/**
 * Decorates a PopulationMetric with the ability to remember the most recent
 * Measurement it took.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MemoryPopulationMetric<T extends Individual> implements PopulationMetric<T> {
    final private PopulationMetric<T> wrappedMetric;
    
    private Measurement mostRecentMeasurement;
    
    public MemoryPopulationMetric(final PopulationMetric<T> wrappedMetric) throws IllegalArgumentException
    {
        if (wrappedMetric == null)
            throw new IllegalArgumentException("WriterPopulationMetric: wrappedMetric is null.");
        this.wrappedMetric = wrappedMetric;
        assert(repOK());
    }
    
    /** @return The value that was returned by the laste call of this.measurePopulation(). */
    public Measurement getMostRecentMeasurement() { return mostRecentMeasurement; }
    
    @Override
    public Measurement measurePopulation(final int run, final int generation, final List<T> population) throws IOException
    {
        assert(population != null);
        final Measurement measurement = wrappedMetric.measurePopulation(run, generation, population);
        mostRecentMeasurement = measurement;
        assert(repOK());
        return measurement;
    }

    @Override
    public void reset() {
        mostRecentMeasurement = null;
    }

    @Override
    public void flush() throws IOException { wrappedMetric.flush(); }

    @Override
    public void close() throws IOException {
        wrappedMetric.close();
        assert(repOK());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return wrappedMetric != null;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%s: mostRecentMeasurement=%s, wrappedMetric=%s]", this.getClass().getSimpleName(), mostRecentMeasurement, wrappedMetric);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof MemoryPopulationMetric))
            return false;
        
        MemoryPopulationMetric cRef = (MemoryPopulationMetric) o;
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
