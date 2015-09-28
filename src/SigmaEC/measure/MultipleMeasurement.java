package SigmaEC.measure;

import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Eric O. Scott
 */
public class MultipleMeasurement extends Measurement {
    private final List<Measurement> measurements;
    
    public MultipleMeasurement(final List<? extends Measurement> measurements) {
        assert(measurements != null);
        assert(!measurements.isEmpty());
        assert(!Misc.containsNulls(measurements));
        this.measurements = new ArrayList<>(measurements); // Defensive copy
        assert(repOK());
    }
    
    @Override
    public int getRun() {
        return measurements.get(0).getRun();
    }

    @Override
    public int getGeneration() {
        return measurements.get(0).getGeneration();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Measurement m : measurements)
            sb.append(m.toString()).append("\n");
        return sb.toString();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return measurements != null
                && measurementsOK();
    }
    
    private boolean measurementsOK() {
        if (measurements.isEmpty() || Misc.containsNulls(measurements))
            return false;
        final int run = measurements.get(0).getRun();
        final int generation = measurements.get(0).getGeneration();
        for (final Measurement m : measurements)
            if (m.getRun() != run || m.getGeneration() != generation)
                return false;
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MultipleMeasurement))
            return false;
        final MultipleMeasurement ref = (MultipleMeasurement)o;
        return measurements.equals(ref.measurements);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.measurements);
        return hash;
    }
    // </editor-fold>
}
