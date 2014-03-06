package SigmaEC.measure;

/**
 * A version of DoubleArrayMeasurement that takes a 5-element array and maps
 * the values to the attributes mean, std, max, min, and bestSoFar.
 * 
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsMeasurement extends DoubleArrayMeasurement {
    public FitnessStatisticsMeasurement(final int run, final int generation, final double[] array) {
        super(run, generation, array);
        assert(array.length == 5);
    }
    
    public double getMean() { return super.getArray()[0]; }
    public double getStd() { return super.getArray()[1]; }
    public double getMax() { return super.getArray()[2]; }
    public double getMin() { return super.getArray()[3]; }
    public double getBestSoFar() { return super.getArray()[4]; }
}
