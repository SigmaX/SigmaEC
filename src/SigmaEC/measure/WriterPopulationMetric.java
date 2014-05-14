package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decorates a PopulationMetric with a Writer which is written to every time
 * a measurement is made.
 * 
 * @author Eric 'Siggy' Scott
 */
public class WriterPopulationMetric<T extends Individual> extends PopulationMetric<T>
{
    final private Writer writer;
    final private PopulationMetric<T> wrappedMetric;
    
    public WriterPopulationMetric(final Writer writer, final PopulationMetric<T> wrappedMetric) throws IllegalArgumentException {
        if (writer == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": writer is null.");
        if (wrappedMetric == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": wrappedMetric is null.");
        this.writer = writer;
        this.wrappedMetric = wrappedMetric;
        assert(repOK());
    }
    
    @Override
    public Measurement measurePopulation(final int run, final int generation, final List<T> population) {
        assert(population != null);
        final Measurement measurement = wrappedMetric.measurePopulation(run, generation, population);
        try {
            writer.write(measurement.toString());
        } catch (IOException ex) {
            Logger.getLogger(WriterPopulationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }
        assert(repOK());
        return measurement;
    }

    @Override
    public void reset() { }

    @Override
    public void flush() {
        try {
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(WriterPopulationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }
        wrappedMetric.flush();
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(WriterPopulationMetric.class.getName()).log(Level.SEVERE, null, ex);
        }
        wrappedMetric.close();
        assert(repOK());
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK() {
        return writer != null
                && wrappedMetric != null
                && wrappedMetric.repOK();
    }
    
    @Override
    public String toString() {
        return String.format("[%s: Writer=%s, WrappedMetric=%s]", this.getClass().getSimpleName(), writer, wrappedMetric);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WriterPopulationMetric))
            return false;
        
        WriterPopulationMetric cRef = (WriterPopulationMetric) o;
        return writer.equals(cRef.writer)
                && wrappedMetric.equals(cRef.wrappedMetric);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.writer != null ? this.writer.hashCode() : 0);
        hash = 29 * hash + (this.wrappedMetric != null ? this.wrappedMetric.hashCode() : 0);
        return hash;
    }
    //</editor-fold>
}
