package SigmaEC.experiment;

import SigmaEC.measure.Measurement;
import SigmaEC.measure.PopulationMetric;
import SigmaEC.meta.Fitness;
import SigmaEC.meta.Population;
import SigmaEC.represent.Decoder;
import SigmaEC.represent.Individual;
import SigmaEC.represent.format.GenomeFormatter;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric O. Scott
 */
public class MeasurementExperiment<T extends Individual<F>, F extends Fitness> extends Experiment {
    public final static String P_INPUT_FORMATTER = "inputFormatter";
    public final static String P_DECODER = "decoder";
    public final static String P_INPUT = "input";
    public final static String P_INPUT_DELIMITER = "inputDelimiter";
    public final static String P_METRIC = "metric";
    
    private final GenomeFormatter inputFormatter;
    private final Option<Decoder<Individual, Individual>> decoder;
    private final Option<String[]> input;
    private final String inputDelimiter;
    private final PopulationMetric<T, F> metric;
    private Measurement result;
    
    public MeasurementExperiment(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        inputFormatter = parameters.getInstanceFromParameter(Parameters.push(base, P_INPUT_FORMATTER), GenomeFormatter.class);
        decoder = parameters.getOptionalInstanceFromParameter(Parameters.push(base, P_DECODER), Decoder.class);
        inputDelimiter = parameters.getOptionalStringParameter(Parameters.push(base, P_INPUT_DELIMITER), "\\|");
        input = parameters.getOptionalStringArrayParameterWithDelimiter(Parameters.push(base, P_INPUT), inputDelimiter);
        metric = parameters.getInstanceFromParameter(Parameters.push(base, P_METRIC), PopulationMetric.class);
        assert(repOK());
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Population<T, F> readPopulation() {
        final List<T> individuals = new ArrayList<>();
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean repOK() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
