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
    public String toString() {
        assert(repOK());
        final StringBuilder sb = new StringBuilder();
        sb.append(strings.get(0));
        for (int i = 1; i < strings.size(); i++)
            sb.append("\n").append(strings.get(i));
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return run >= 0
                && generation >= 0
                && strings != null
                && strings.size() > 0
                && !Misc.containsNulls(strings);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MultipleStringMeasurement))
            return false;
        final MultipleStringMeasurement ref = (MultipleStringMeasurement) o;
        return run == ref.run
                && generation == ref.generation
                && strings.equals(ref.strings);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.run;
        hash = 17 * hash + this.generation;
        hash = 17 * hash + (this.strings != null ? this.strings.hashCode() : 0);
        return hash;
    }
    // </editor-fold>
    
}
