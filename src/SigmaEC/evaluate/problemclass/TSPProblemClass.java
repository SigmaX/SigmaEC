package SigmaEC.evaluate.problemclass;

import SigmaEC.SRandom;
import SigmaEC.evaluate.objective.discrete.TSPObjective;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Random;

/**
 * Based on code written by Ken De Jong.
 *
 * @author Eric O. Scott
 */
public class TSPProblemClass extends ProblemClass<TSPObjective, IntVectorIndividual> {
    public final static String P_RANDOM = "random";
    public final static String P_NUM_NODES = "numNodes";
    public final static String P_SPATIAL_BOUND = "spatialBound";
    
    private final Random random;
    private final int numNodes;
    private final double spatialBound;

    /* Creates a new instance of BB_TSP_Generator */
    public TSPProblemClass(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        random = parameters.getInstanceFromParameter(Parameters.push(base, P_RANDOM), SRandom.class);
        numNodes = parameters.getIntParameter(Parameters.push(base, P_NUM_NODES));
        spatialBound = parameters.getDoubleParameter(Parameters.push(base, P_SPATIAL_BOUND));
        assert(repOK());
    }

    public int[] random_tour() {
        int i, j, k, tmp;
        int[] tour = new int[numNodes];

        for (i = 0; i < numNodes; i++) {
            tour[i] = i;
        }

        for (i = 1; i < 20 * numNodes; i++) {  // quick & dirty!                                                                                                                                               
            j = random.nextInt(numNodes - 1);
            k = random.nextInt(numNodes - 1);
            tmp = tour[j + 1];
            tour[j + 1] = tour[k + 1];
            tour[k + 1] = tmp;
        }

        return (tour);
    }

    @Override
    public TSPObjective getNewInstance() {
        final double[] x_coordinates = new double[numNodes];
        final double[] y_coordinates = new double[numNodes];
        for (int i = 0; i < numNodes; i++) {
            x_coordinates[i] = -spatialBound + 2 * spatialBound * random.nextDouble();
            y_coordinates[i] = -spatialBound + 2 * spatialBound * random.nextDouble();
        }
        assert(repOK());
        return new TSPObjective(x_coordinates, y_coordinates);
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return P_NUM_NODES != null
                && !P_NUM_NODES.isEmpty()
                && P_RANDOM != null
                && !P_RANDOM.isEmpty()
                && P_SPATIAL_BOUND != null
                && !P_SPATIAL_BOUND.isEmpty()
                && numNodes > 1
                && random != null
                && !Double.isInfinite(spatialBound)
                && !Double.isNaN(spatialBound);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TSPProblemClass))
            return false;
        final TSPProblemClass ref = (TSPProblemClass)o;
        return numNodes == ref.numNodes
                && Misc.doubleEquals(spatialBound, ref.spatialBound)
                && random.equals(ref.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 83 * hash + this.numNodes;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.spatialBound) ^ (Double.doubleToLongBits(this.spatialBound) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%d, %s=%f, %s=%s]", this.getClass().getSimpleName(),
                P_NUM_NODES, numNodes,
                P_SPATIAL_BOUND, spatialBound,
                P_RANDOM, random);
    }
    // </editor-fold>
}
