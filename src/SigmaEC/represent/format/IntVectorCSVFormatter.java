package SigmaEC.represent.format;

import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric O. Scott
 */
public class IntVectorCSVFormatter extends GenomeFormatter<IntVectorIndividual> {

    public IntVectorCSVFormatter(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        assert(repOK());
    }
    
    @Override
    public String genomeToString(final IntVectorIndividual individual) {
        assert(individual != null);
        final StringBuilder sb = new StringBuilder(String.valueOf(individual.getElement(0)));
        for (int i = 1; i < individual.size(); i++)
            sb.append(",").append(String.valueOf(individual.getElement(i)));
        assert(repOK());
        return sb.toString();
    }
    
    @Override
    public IntVectorIndividual stringToGenome(final String individual) {
        assert(individual != null);
        assert(!individual.isEmpty());
        final String[] split = individual.split(",");
        final int[] genome = new int[split.length];
        for (int i = 0; i < split.length; i++)
            genome[i] = Integer.valueOf(split[i]);
        assert(repOK());
        return new IntVectorIndividual.Builder(genome).build();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof IntVectorCSVFormatter);
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
