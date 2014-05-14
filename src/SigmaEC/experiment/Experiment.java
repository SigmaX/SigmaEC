package SigmaEC.experiment;

import SigmaEC.ContractObject;

/**
 * Defines a complete evolutionary experiment which can be called from the
 * program's main method.
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Experiment extends ContractObject {
    public abstract void run();
}
