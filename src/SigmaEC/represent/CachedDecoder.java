package SigmaEC.represent;

import SigmaEC.util.Parameters;
import java.util.HashMap;
import java.util.Map;

/**
 * A cached genotype-to-phenotype decoder, useful when the mapping is performed
 * by an expensive external simulator.
 * 
 * @author Eric 'Siggy' Scott
 */
public class CachedDecoder<T extends Individual, P> extends Decoder<T, P> {
    private final static String P_DECODER = "decoder";
    
    private final Decoder<T, P> decoder;
    private final Map<T, P> cache;
    
    public CachedDecoder(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        this.decoder = parameters.getInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
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

    @Override
    public void reset() {
        cache.clear();
        decoder.reset();
        assert(repOK());
    }
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return decoder != null
                && cache != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CachedDecoder))
            return false;
        final CachedDecoder ref = (CachedDecoder) o;
        return decoder.equals(ref.decoder)
                && decoder.equals(ref.cache);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.decoder != null ? this.decoder.hashCode() : 0);
        hash = 17 * hash + (this.cache != null ? this.cache.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: decoder=%s]", this.getClass().getSimpleName(), decoder);
    }
    // </editor-fold>
    
}
