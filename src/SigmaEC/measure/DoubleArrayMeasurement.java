package SigmaEC.measure;

import SigmaEC.util.Misc;
import java.util.Arrays;

/**
 * A measurement of a population expressed as a vector of real numbers.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleArrayMeasurement extends Measurement {
    public final static String DELIMITER = ",";
    private final int run;
    private final int generation;
    private final int individual;
    private final double[] array;
    
    public DoubleArrayMeasurement(final int run, final int generation, final int individual, final double[] array) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(array != null);
        assert(array.length > 0);
        this.run = run;
        this.generation = generation;
        this.individual = individual;
        this.array = Arrays.copyOf(array, array.length);
        assert(repOK());
    }
    
    public double[] getArray() { return Arrays.copyOf(array, array.length); }
    
    @Override
    public int getRun() { return run; }

    @Override
    public int getGeneration() { return generation; }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    /**
     * @return A CSV string of the form
     * run, generation, array[0], array[1], array[2], ... \n
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(run).append(DELIMITER)
                .append(generation).append(DELIMITER)
                .append(individual).append(DELIMITER)
                .append(array[0]);
        for (int i = 1; i < array.length; i++)
            sb.append(DELIMITER).append(array[i]);
        assert(repOK());
        return sb.toString();
    }

    @Override
    public final boolean repOK() {
        return run >= 0
                && generation >= 0
                && array != null
                && array.length > 0
                && !Misc.containsNaNs(array);
    }
    
    @Override
    public boolean equals(final Object o) {
        if(!(o instanceof DoubleArrayMeasurement))
            return false;
        final DoubleArrayMeasurement ref = (DoubleArrayMeasurement)o;
        return run == ref.run
                && generation == ref.generation
                && Misc.doubleArrayEquals(array, ref.array);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.run;
        hash = 53 * hash + this.generation;
        hash = 53 * hash + Arrays.hashCode(this.array);
        return hash;
    }
    // </editor-fold>
}
