package SigmaEC.measure;

import SigmaEC.evaluate.ObjectiveFunction;
import SigmaEC.represent.Individual;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class FitnessStatisticsPopulationMetric<T extends Individual> implements PopulationMetric<T>
{
    final private ObjectiveFunction<T> objective;
    
    public FitnessStatisticsPopulationMetric(ObjectiveFunction<T> objective)
    {
        if (objective == null)
            throw new IllegalArgumentException("FitnessStatisticsPopulationMetric: objective was null.");
        this.objective = objective;
        assert(repOK());
    }
    
    @Override
    public String measurePopulation(List<T> population) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flush() throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    final public boolean repOK()
    {
        return objective != null;
    }
    
}
