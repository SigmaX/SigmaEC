package SigmaEC.measure;

import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;

/**
 * A measurement of a population that consists in a sequence of Strings.
 * 
 * @author Eric 'Siggy' Scott
 */
public class MultipleStringMeasurement extends Measurement {
    private final int run;
    private final int generation;
    private final List<String> strings;
    
    public MultipleStringMeasurement(final int run, final int generation, final List<String> strings) {
        assert(run >= 0);
        assert(generation >= 0);
        assert(strings != null);
        assert(strings.size() > 0);
        assert(!Misc.containsNulls(strings));
        this.run = run;
        this.generation = generation;
        this.strings = new ArrayList<String>(strings);
        assert(repOK());
    }
    
    public List<String> getStrings() { return new ArrayList<String>(strings); }
    
    @Override
    public int getRun() { return run; }

    @Override
    public int getGeneration() { return generation; }

    @Override
    public final boolean repOK() {
        return run >= 0
                && generation >= 0
                && strings != null
                && strings.size() > 0
                && !Misc.containsNulls(strings);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final String s : strings)
            sb.append(s).append("\n");
        return sb.toString();
    }
    
}
