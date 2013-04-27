package SigmaEC.measure;

import SigmaEC.represent.Individual;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Decorates a PopulationMetric with a Writer which is written to every time
 * a measurement is made.
 * 
 * @author Eric 'Siggy' Scott
 */
public class WriterPopulationMetric<T extends Individual> implements PopulationMetric<T>
{
    final private Writer writer;
    final private PopulationMetric<T> wrappedMetric;
    
    public WriterPopulationMetric(Writer writer, PopulationMetric<T> wrappedMetric) throws IllegalArgumentException
    {
        if (writer == null)
            throw new IllegalArgumentException("WriterPopulationMetric: writer is null.");
        if (wrappedMetric == null)
            throw new IllegalArgumentException("WriterPopulationMetric: wrappedMetric is null.");
        this.writer = writer;
        this.wrappedMetric = wrappedMetric;
        assert(repOK());
    }
    
    @Override
    public String measurePopulation(int generation, List<T> population) throws IOException
    {
        assert(population != null);
        String measurement = wrappedMetric.measurePopulation(generation, population);
        writer.write(measurement);
        assert(repOK());
        return measurement;
    }

    @Override
    public void flush() throws IOException
    {
        writer.flush();
        wrappedMetric.flush();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return writer != null
                && wrappedMetric != null
                && wrappedMetric.repOK();
    }
    
    @Override
    public String toString()
    {
        return String.format("[WriterPopulationMetric: Writer=%s, WrappedMetric=%s]", writer, wrappedMetric);
    }
    
    @Override
    public boolean equals(Object o)
    {
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
