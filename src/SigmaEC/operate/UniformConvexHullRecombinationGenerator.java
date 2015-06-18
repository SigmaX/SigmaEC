package SigmaEC.operate;

import SigmaEC.Generator;
import SigmaEC.SRandom;
import SigmaEC.represent.linear.BitStringIndividual;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Recombination operator from
 * 
 * Moraglio, Alberto, and Dirk Sudholt. "Runtime analysis of convex evolutionary search." Proceedings of GECCO 2012. ACM.
 * 
 * @author Eric O. Scott
 */
public class UniformConvexHullRecombinationGenerator extends Generator<BitStringIndividual> {
    public final static String P_RANDOM = "random";
    public final static String P_NUM_CHILDREN = "numChildren";
    
    private enum Fixated { FIXED_TRUE, FIXED_FALSE, NOT_FIXED };
    
    private final Random random;
    private final int numChildren;
    
    public UniformConvexHullRecombinationGenerator(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        numChildren = parameters.getIntParameter(Parameters.push(base, P_NUM_CHILDREN));
        assert(repOK());
    }
    
    @Override
    public List<BitStringIndividual> produceGeneration(final List<BitStringIndividual> parents) {
        assert(parents != null);
        assert(!parents.isEmpty());
        
        final Fixated[] fixated = fixated(parents);
        final List<BitStringIndividual> newPopulation = new ArrayList<BitStringIndividual>() {{
            for (int i = 0; i < numChildren; i++)
                add(generateOffspring(fixated));
        }};
        assert(repOK());
        return newPopulation;
    }
    
    private BitStringIndividual generateOffspring(final Fixated[] fixated) {
        assert(fixated != null);
        assert(fixated.length > 0);
        
        final boolean[] genome = new boolean[fixated.length];
        for (int i = 0; i < fixated.length; i++) {
            switch(fixated[i]) {
                case FIXED_TRUE:
                    genome[i] = true;
                    break;
                case FIXED_FALSE:
                    genome[i] = false;
                    break;
                case NOT_FIXED:
                    genome[i] = random.nextBoolean();
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        return new BitStringIndividual.Builder(genome).build();
    }
    
    private static Fixated[] fixated(final List<BitStringIndividual> parents) {
        assert(parents != null);
        assert(!parents.isEmpty());
        final int numLoci = parents.get(0).size();
        final Fixated[] fixated = new Fixated[numLoci];
        for (int i = 0; i < numLoci; i++)
            fixated[i] = fixated(parents, i);
        return fixated;
    }
            
    /** @return 1 if a locus is fixated to a true allele, 0 if it's fixated to
     *  false, and -1 if it isn't fixated at all.
     */
    private static Fixated fixated(final List<BitStringIndividual> parents, final int locus) {
        assert(parents != null);
        assert(!parents.isEmpty());
        assert(locus >= 0);
        assert(locus < parents.get(0).size());
        final boolean firstAllele = parents.get(0).getElement(locus);
        for (int i = 1; i < parents.size(); i++)
            if (parents.get(i).getElement(locus) != firstAllele)
                return Fixated.NOT_FIXED;
        return firstAllele ? Fixated.FIXED_TRUE : Fixated.FIXED_FALSE;
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && random != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof UniformConvexHullRecombinationGenerator))
            return false;
        final UniformConvexHullRecombinationGenerator ref = (UniformConvexHullRecombinationGenerator)o;
        return numChildren == ref.numChildren
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.random);
        hash = 59 * hash + this.numChildren;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%s]", this.getClass().getSimpleName(),
                P_NUM_CHILDREN, numChildren,
                P_RANDOM, random);
    }
    // </editor-fold>
}
