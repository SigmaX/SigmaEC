package SigmaEC.represent;

import java.util.HashMap;
import java.util.Map;

/**
 * A cached genotype-to-phenotype decoder, useful when the mapping is performed
 * by an expensive external simulator.
 * 
 * @author Eric 'Siggy' Scott
 */
public class CachedDecoder<T extends Individual, P extends Phenotype> implements Decoder<T, P> {
    private final Decoder<T, P> decoder;
    private final Map<T, P> cache;
    
    public CachedDecoder(final Decoder<T, P> decoder) {
        assert(decoder != null);
        this.decoder = decoder;
        this.cache = new HashMap<T, P>();
        assert(repOK());
    }
    
    @Override
    public P decode(final T individual) {
        if (cache.containsKey(individual))
            return cache.get(individual);
        final P phenotype = decoder.decode(individual);
        cache.put(individual, phenotype);
        return phenotype;
    }
    
    public final boolean repOK() {
        return decoder != null
                && cache != null;
    }
    
}
