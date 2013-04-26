package SigmaEC.test;

import SigmaEC.represent.Gene;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class TestGene implements Gene
{
    public final int val;
    public TestGene(int val) { this.val = val; }

    @Override
    public boolean equals(Object ref)
    {
        if (!(ref instanceof TestGene))
            return false;
        return val == ((TestGene)ref).val;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 13 * hash + this.val;
        return hash;
    }

    @Override
    public String toString()
    {
        return ((Integer)val).toString();
    }

    @Override
    public TestGene mutate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
