package SigmaEC;

import SigmaEC.util.Option;
import SigmaEC.util.Parameters;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SRandom extends MersenneTwister {
    private final static String P_SEED = "seed";
    
    public SRandom(final Parameters parameters, final String base) {
        super();
        assert(parameters != null);
        assert(base != null);
        final Option<Long> seedOpt = parameters.getOptionalLongParameter(Parameters.push(base, P_SEED));
        if (seedOpt.isDefined())
            setSeed(seedOpt.get());
    }
}
