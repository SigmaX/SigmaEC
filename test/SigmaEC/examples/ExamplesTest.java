package SigmaEC.examples;

import static SigmaEC.SigmaEC.P_EXPERIMENT;
import SigmaEC.experiment.Experiment;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * System tests to make sure that the example applications run without exceptions.
 * 
 * @author Eric O. Scott
 */
@RunWith(Parameterized.class)
public class ExamplesTest {
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {   
            {"src/SigmaEC/examples/GAExample.properties"},
            {"src/SigmaEC/examples/FloatingPointExample.properties"}, 
            {"src/SigmaEC/examples/TestSuiteExample.properties"},
            //{"src/SigmaEC/examples/TestSuiteViewerExample.properties"},
            {"src/SigmaEC/examples/IslandModelExample.properties"},
            {"src/SigmaEC/examples/ACOExample_suite.properties"}
           });
    }
    
    @Parameter
    public String examplePath;

    @Test
    public void testExample() {
        final Properties properties = new Properties();
        final FileInputStream pInput;
        try {
            pInput = new FileInputStream(examplePath);
            properties.load(pInput);
            final SigmaEC.util.Parameters parameters = new SigmaEC.util.Parameters(properties);
            final Experiment experiment = parameters.getInstanceFromParameter(P_EXPERIMENT, Experiment.class);
            experiment.run();
        } catch (final IOException ex) {
            fail(ex.toString());
        }
        // No exception is success.
    }
}
