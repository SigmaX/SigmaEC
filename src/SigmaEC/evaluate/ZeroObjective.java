package SigmaEC.evaluate;

import SigmaEC.represent.Phenotype;

/**
 * Returns zero fitness for any individual.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ZeroObjective implements ObjectiveFunction
{
    public ZeroObjective() { };
    
    @Override
    public double fitness(Phenotype ind)
    {
        return 0.0;
    }

    //<editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public boolean repOK()
    {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "[ZeroObjective]";
    }
    
    @Override
    public boolean equals(Object o)
    {
        return (o instanceof ZeroObjective);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
    //</editor-fold>
}
