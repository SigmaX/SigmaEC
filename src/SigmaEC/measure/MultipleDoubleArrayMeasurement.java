package SigmaEC.measure;

import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A measurement of a population expressed as a sequence of vectors of real
 * numbers.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MultipleDoubleArrayMeasurement extends Measurement {
    private final int run;
    private final int generation;
    private final List<double[]> vectors;
    
    public MultipleDoubleArrayMeasurement(final int run, final int generation, final List<double[]> vectors) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(vectors != null);
        assert(vectors.size() > 0);
        assert(!Misc.containsNulls(vectors));
        this.run = run;
        this.generation = generation;
        this.vectors = copyListOfArrays(vectors);
        assert(repOK());
    }
    
    private List<double[]> copyListOfArrays(final List<double[]> ref) {
        return new ArrayList<double[]> () {{
            for (final double[] v : ref)
                add(Arrays.copyOf(v, v.length));
        }};
    }
    
    public List<double[]> getDoubleArrays() { return copyListOfArrays(vectors); }
    
    @Override
    public int getRun() { return run; }

    @Override
    public int getGeneration() { return generation; }

    @Override
    public final boolean repOK() {
        return run >= 0
                && generation >= 0
                && vectors != null
                && vectors.size() > 0
                && !Misc.containsNulls(vectors);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final double[] v : vectors)
            sb.append((new DoubleArrayMeasurement(run, generation, v)).toString());
        return sb.toString();
    }
    
}
