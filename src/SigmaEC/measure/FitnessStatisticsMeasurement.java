package SigmaEC.measure;

/**
 * A version of DoubleArrayMeasurement that takes a 5-element array and maps
 * the values to the attributes mean, std, max, min, and bestSoFar.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsMeasurement extends Measurement {
    private final DoubleArrayMeasurement stats;
    private final long bsfIndividualID;
    public FitnessStatisticsMeasurement(final int run, final int generation, final double mean, final double std, final double max, final double min, final double bsf, final long bsfIndividualID) {
        
        stats = new DoubleArrayMeasurement(run, generation, new double[] { mean, std, max, min, bsf });
        this.bsfIndividualID = bsfIndividualID;
    }
    
    @Override public int getRun() { return stats.getRun(); }
    @Override public int getGeneration() { return stats.getGeneration(); }
    public double getMean() { return stats.getArray()[0]; }
    public double getStd() { return stats.getArray()[1]; }
    public double getMax() { return stats.getArray()[2]; }
    public double getMin() { return stats.getArray()[3]; }
    public double getBestSoFar() { return stats.getArray()[4]; }
    public long getBestSoFarID() { return bsfIndividualID; }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public String toString() {
        return String.format("%s, %d", stats.toString(), bsfIndividualID);
    }

    @Override
    public boolean repOK() {
        return stats != null
                && bsfIndividualID >= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FitnessStatisticsMeasurement))
            return false;
        final FitnessStatisticsMeasurement ref = (FitnessStatisticsMeasurement)o;
        return bsfIndividualID == ref.bsfIndividualID
                && stats.equals(ref.stats);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.stats != null ? this.stats.hashCode() : 0);
        hash = 79 * hash + (int) (this.bsfIndividualID ^ (this.bsfIndividualID >>> 32));
        return hash;
    }
    // </editor-fold>
}
