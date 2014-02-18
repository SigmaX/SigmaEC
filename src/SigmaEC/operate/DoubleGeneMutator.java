package SigmaEC.operate;

import SigmaEC.represent.DoubleGene;
import SigmaEC.util.Misc;
import java.util.Random;

/**
 * A mutator that adds Gaussian noise to a double.
 * 
 * @author Eric 'Siggy' Scott
 */
public class DoubleGeneMutator implements Mutator<DoubleGene>
{
    final private Random random;
    final private double gaussianStd;
    
    public DoubleGeneMutator(double gaussianStd, Random random)
    {
        this.gaussianStd = gaussianStd;
        this.random = random;
        assert(repOK());
    }
    
    @Override
    public DoubleGene mutate(DoubleGene gene)
    {
        return new DoubleGene(gene.value + Misc.gaussianSample(random)*gaussianStd);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    final public boolean repOK()
    {
        return random != null
                && Double.isNaN(gaussianStd);
    }
    
    @Override
    final public String toString()
    {
        return String.format("[DoubleGeneMutator: GaussianStd=%f, Random=%s]", gaussianStd, random);
    }
    
    @Override
    final public boolean equals(Object o)
    {
        if (o == this)
            return true;
        if (!(o instanceof DoubleGeneMutator))
            return false;
        
        DoubleGeneMutator cRef = (DoubleGeneMutator) o;
        return Misc.doubleEquals(gaussianStd, cRef.gaussianStd)
                && random.equals(cRef.random);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.random != null ? this.random.hashCode() : 0);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.gaussianStd) ^ (Double.doubleToLongBits(this.gaussianStd) >>> 32));
        return hash;
    }
    //</editor-fold>
}
