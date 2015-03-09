package SigmaEC;

import SigmaEC.experiment.Experiment;
import SigmaEC.util.Args;
import SigmaEC.util.Pair;
import SigmaEC.util.Parameters;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SigmaEC {
    private final static String A_PARAMETER_FILE = "p";
    private final static String A_PROPERTY = "a";
    private final static String P_EXPERIMENT = "experiment";
    
    /** Private constructor throws an error if called. */
    private SigmaEC() throws AssertionError {
        throw new AssertionError(SigmaEC.class.getSimpleName() + ": Cannot create instance of static class.");
    }
    
    private final static List<String> allowedOptions = new ArrayList<String>() {{ add(A_PARAMETER_FILE); add(A_PROPERTY);}};
    
    public static String usage = "Bad CLI arguments.";
    
    public static void main(final String[] args) {
        assert(args != null);
        if (!Args.checkOptions(args, allowedOptions) || !Args.getOption(A_PARAMETER_FILE, args).isDefined()) {
            System.err.println(usage);
            System.exit(0);
        }
        
        final String parameterFileName = Args.getOption(A_PARAMETER_FILE, args).get();
        assert(parameterFileName != null);
        
        try {
            final Properties properties = new Properties();
            final FileInputStream pInput = new FileInputStream(parameterFileName);
            properties.load(pInput);
            final List<Pair<String>> cliProperties = Args.getAllEqualsOption(A_PROPERTY, args);
            for (final Pair<String> p : cliProperties)
                properties.setProperty(p.getX(), p.getY());
            
            final Parameters parameters = new Parameters(properties);
            final Experiment experiment = parameters.getInstanceFromParameter(P_EXPERIMENT, Experiment.class);
            experiment.run();
        }
        catch (final Exception e) {
            e.printStackTrace(System.err);
            Logger.getLogger(SigmaEC.class.toString()).log(Level.SEVERE, "", e);
            System.exit(1);
        }
    }
}
