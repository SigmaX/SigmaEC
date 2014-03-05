package SigmaEC.experiment;

import java.util.Random;

/**
 * Defines a complete evolutionary experiment which can be called from the
 * program's main method.
 * 
 * @author Eric 'Siggy' Scott
 */
public interface Experiment {
    public void run();
    /** Representation invariant.  If this returns false, there is something
     * invalid about this object's internal state. */
    public abstract boolean repOK();
}
