package SigmaEC.represent;

/**
 * A Gene that wraps a single double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGene implements Gene
{
    final public double value;
    public DoubleGene(double value) { this.value = value; }
}
