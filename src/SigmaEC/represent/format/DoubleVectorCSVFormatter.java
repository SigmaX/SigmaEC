package SigmaEC.represent.format;

import SigmaEC.represent.linear.DoubleVectorIndividual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class DoubleVectorCSVFormatter extends GenomeFormatter<DoubleVectorIndividual> {

    public DoubleVectorCSVFormatter(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public String genomeToString(final DoubleVectorIndividual individual) {
        assert(individual != null);
        final StringBuilder sb = new StringBuilder(String.valueOf(individual.getElement(0)));
        for (int i = 1; i < individual.size(); i++)
            sb.append(",").append(String.valueOf(individual.getElement(i)));
        assert(repOK());
        return sb.toString();
    }
    
    @Override
    public DoubleVectorIndividual stringToGenome(final String individual) {
        assert(individual != null);
        assert(!individual.isEmpty());
        final String[] split = individual.split(",");
        final double[] genome = new double[split.length];
        for (int i = 0; i < split.length; i++)
            genome[i] = Double.valueOf(split[i]);
        assert(repOK());
        return new DoubleVectorIndividual.Builder(genome).build();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof DoubleVectorCSVFormatter);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s]", this.getClass().getSimpleName());
    }
    // </editor-fold>
}
