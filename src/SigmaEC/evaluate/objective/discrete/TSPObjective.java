package SigmaEC.evaluate.objective.discrete;

import SigmaEC.evaluate.objective.ObjectiveFunction;
import SigmaEC.represent.linear.IntVectorIndividual;
import SigmaEC.util.Misc;
import SigmaEC.util.Parameters;
import java.util.Arrays;

/**
 *
 * @author Eric O. Scott
 */
public class TSPObjective extends ObjectiveFunction<IntVectorIndividual> {
    public final static String P_NODE_X_COORDINATES = "nodeXCoordinates";
    public final static String P_NODE_Y_COORDINATES = "nodeYCoordinates";
    private final double[] nodeXCoordinates;
    private final double[] nodeYCoordinates;
    
    public TSPObjective(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        nodeXCoordinates = parameters.getDoubleArrayParameter(Parameters.push(base, P_NODE_X_COORDINATES));
        nodeYCoordinates = parameters.getDoubleArrayParameter(Parameters.push(base, P_NODE_Y_COORDINATES));
        assert(repOK());
    }
    
    public TSPObjective(final double[] nodeXCoordinates, final double[] nodeYCoordinates) {
        assert(nodeXCoordinates != null);
        assert(nodeYCoordinates != null);
        assert(nodeXCoordinates.length == nodeYCoordinates.length);
        this.nodeXCoordinates = nodeXCoordinates;
        this.nodeYCoordinates = nodeYCoordinates;
        assert(repOK());
    }
    
    public int getNodeCount() {
        assert(repOK());
        return nodeXCoordinates.length;
    }
    
    @Override
    public double fitness(final IntVectorIndividual ind) {
        assert(ind != null);
        assert(ind.size() == getNodeCount());
        final int[] tour = ind.getGenomeArray();
        
        float length = 0;
        for (int i = 0; i < getNodeCount() - 1; i++) {
            final double x_delta = Math.abs(nodeXCoordinates[tour[i]] - nodeXCoordinates[tour[i + 1]]);
            final double y_delta = Math.abs(nodeYCoordinates[tour[i]] - nodeYCoordinates[tour[i + 1]]);
            length += Math.sqrt(x_delta * x_delta + y_delta * y_delta);
        }
        final double x_delta = Math.abs(nodeXCoordinates[tour[0]] - nodeXCoordinates[tour[getNodeCount() - 1]]);
        final double y_delta = Math.abs(nodeYCoordinates[tour[0]] - nodeYCoordinates[tour[getNodeCount() - 1]]);
        length += Math.sqrt(x_delta * x_delta + y_delta * y_delta);
        return length;
    }

    @Override
    public void setStep(int i) { /* Do nothing */ }

    @Override
    public int getNumDimensions() {
        return getNodeCount();
    }

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return nodeXCoordinates != null
                && nodeYCoordinates != null
                && nodeXCoordinates.length == nodeYCoordinates.length
                && !Misc.containsNaNs(nodeXCoordinates)
                && !Misc.containsNaNs(nodeYCoordinates);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TSPObjective))
            return false;
        final TSPObjective ref = (TSPObjective)o;
        return Misc.doubleArrayEquals(nodeXCoordinates, ref.nodeXCoordinates)
                && Misc.doubleArrayEquals(nodeYCoordinates, ref.nodeYCoordinates);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Arrays.hashCode(this.nodeXCoordinates);
        hash = 67 * hash + Arrays.hashCode(this.nodeYCoordinates);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: %s=%s, %s=%s]", this.getClass().getSimpleName(),
                P_NODE_X_COORDINATES, Arrays.toString(nodeXCoordinates),
                P_NODE_Y_COORDINATES, Arrays.toString(nodeYCoordinates));
    }
    // </editor-fold>
}
