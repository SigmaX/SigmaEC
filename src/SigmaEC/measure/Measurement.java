package SigmaEC.measure;

import SigmaEC.ContractObject;

/**
 * Represents a measurement taken of a population at a given generation in an
 * evolutionary algorithm.
 * 
 * This is an abstract class instead of an interface in order to force users to
 * override toString().
 * 
 * @author Eric 'Siggy' Scott
 */
public abstract class Measurement extends ContractObject {
    public abstract int getRun();
    public abstract int getStep();
    
    /** @return A CSV representation of the measurement suitable for
     * printing to a file.  This may be more than one line, but every line
     * ought to be prefixed by the run and generation so that the measurement
     * is uniquely identified from other measurements in the experiment.
     */
    @Override
    public abstract String toString();
}
