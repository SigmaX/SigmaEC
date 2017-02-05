package SigmaEC.measure;

import SigmaEC.meta.Fitness;
import SigmaEC.meta.Population;
import SigmaEC.represent.Individual;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decorates a PopulationMetric with a Writer which is written to every time
 * a measurement is made.
 * 
 * @author Eric 'Siggy' Scott
 */
public class WriterPopulationMetric<T extends Individual<F>, F extends Fitness> extends PopulationMetric<T, F> {
    final public static String P_PREFIX = "prefix";
    final public static String P_FILE = "file";
    final public static String P_ROW_PREFIX = "rowPrefix";
    final public static String P_HEADER = "header";
    final public static String P_METRIC = "metric";
    public final static String P_MODULO = "modulo";
    
    private final boolean header;
    private final Writer writer;
    private final int modulo;
    private final PopulationMetric<T, F> wrappedMetric;
    private final Option<String> rowPrefix;
    
    public WriterPopulationMetric(final Parameters parameters, final String base) throws IllegalArgumentException {
        assert(parameters != null);
        assert(base != null);
        
        final Option<String> file = parameters.getOptionalStringParameter(Parameters.push(base, P_FILE));
        if (file.isDefined()) {
            final String prefix = parameters.getOptionalStringParameter(Parameters.push(base, P_PREFIX), "");
            final String fileName = prefix + file.get();
            try {
                writer = new FileWriter(fileName);
            }
            catch (final IOException e) {
                throw new IllegalArgumentException(this.getClass().getSimpleName() +": could not open file " + fileName, e);
            }
        }
        else
            writer = new OutputStreamWriter(System.out);
        wrappedMetric = parameters.getInstanceFromParameter(Parameters.push(base, P_METRIC), PopulationMetric.class);
        
        if (writer == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": writer is null.");
        if (wrappedMetric == null)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": wrappedMetric is null.");
        
        rowPrefix = parameters.getOptionalStringParameter(Parameters.push(base, P_ROW_PREFIX));
        header = parameters.getOptionalBooleanParameter(Parameters.push(base, P_HEADER), true);
        if (header) {
            try {
                writer.write(csvHeader() + "\n");
            } catch (final IOException ex) {
                Logger.getLogger(WriterPopulationMetric.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        modulo = parameters.getOptionalIntParameter(Parameters.push(base, P_MODULO), 1);
        if (modulo <= 0)
            throw new IllegalStateException(String.format("%s: %s is %d, must be positive.", this.getClass().getSimpleName(), P_MODULO, modulo));
        assert(repOK());
    }
    
    @Override
    public Measurement measurePopulation(final int run, final int step, final Population<T, F> population) {
        assert(run >= 0);
        assert(step >= 0);
        assert(population != null);
        if (step % modulo == 0) {
            final Measurement measurement = wrappedMetric.measurePopulation(run, step, population);
            try {
                final String prefix = rowPrefix.isDefined() ? rowPrefix.get() + ", " : "";
                writer.write(String.format("%s%s\n", prefix, measurement.toString()));
            } catch (final IOException ex) {
                Logger.getLogger(WriterPopulationMetric.class.getName()).log(Level.SEVERE, null, ex);
            }
            assert(repOK());
            return measurement;
        }
        assert(repOK());
        ping(step, population);
        return null;
    }

    @Override
    public void ping(final int step, final Population<T, F> population) {
        wrappedMetric.ping(step, population);
    }

    @Override
    public final String csvHeader() {
        return rowPrefix.isDefined() ? "experiment, " + wrappedMetric.csvHeader()
                : wrappedMetric.csvHeader();
    }

    @Override
    public void reset() {
        wrappedMetric.reset();
    }

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
                && P_PREFIX != null
                && !P_PREFIX.isEmpty()
                && P_FILE != null
                && !P_FILE.isEmpty()
                && P_METRIC != null
                && !P_METRIC.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("[%s: writer=%s, wrappedMetric=%s]", this.getClass().getSimpleName(), writer, wrappedMetric);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof WriterPopulationMetric))
            return false;
        
        final WriterPopulationMetric cRef = (WriterPopulationMetric) o;
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
