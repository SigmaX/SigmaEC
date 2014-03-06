package SigmaEC.measure;

import java.util.Arrays;

/**
 * A measurement of a population expressed as a vector of real numbers.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleArrayMeasurement extends Measurement {
    private final int run;
    private final int generation;
    private final double[] array;
    
    public DoubleArrayMeasurement(final int run, final int generation, final double[] array) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(array != null);
        assert(array.length > 0);
        this.run = run;
        this.generation = generation;
        this.array = Arrays.copyOf(array, array.length);
        assert(repOK());
    }
    
    public double[] getArray() { return Arrays.copyOf(array, array.length); }
    
    @Override
    public int getRun() { return run; }

    @Override
    public int getGeneration() { return generation; }

    /**
     * @return A CSV string of the form
     * run, generation, array[0], array[1], array[2], ... \n
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(run).append(", ").append(generation).append(", ").append(array[0]);
        for (int i = 1; i < array.length; i++)
            sb.append(", ").append(array[i]);
        sb.append("\n");
        assert(repOK());
        return sb.toString();
    }

    @Override
    public final boolean repOK() {
        return run >= 0
                && generation >= 0
                && array != null
                && array.length > 0;
    }
    
}
