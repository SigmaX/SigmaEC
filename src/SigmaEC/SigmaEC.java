package SigmaEC;

import SigmaEC.experiment.Experiment;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class SigmaEC {
    private final static String A_PARAMETER_FILE = "p";
    private final static String P_EXPERIMENT = "experiment";
    
    /** Private constructor throws an error if called. */
    private SigmaEC() throws AssertionError {
        throw new AssertionError("Misc: Cannot create instance of static class.");
    }
    
    public static void main(final String[] args) {
        assert(args != null);
        if (!checkOptions(args) || !getOption(A_PARAMETER_FILE, args).isDefined()) {
            System.err.println(usage());
            System.exit(0);
        }
        
        final String parameterFileName = getOption(A_PARAMETER_FILE, args).get();
        assert(parameterFileName != null);
        
        try {
            final Properties properties = new Properties();
            final FileInputStream pInput = new FileInputStream(parameterFileName);
            properties.load(pInput);
            final Parameters parameters = new Parameters(properties);
            
            final Experiment experiment = parameters.getInstanceFromParameter(P_EXPERIMENT, Experiment.class);
            experiment.run();
        }
        catch (final Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="CLI Argument Parsing">
    public static String usage() {
        return String.format("Bad CLI arguments.");
    }
    
    private final static List<String> allowedOptions = new ArrayList<String>() {{ add("p");  }};
    
    private static boolean checkOptions(final String[] args) {
        assert(args != null);
        for (final String option : getOptions(args))
            if (!allowedOptions.contains(option))
                return false;
        return true;
    }
    
    private static Option<String> getOption(final String optionName, final String[] args) {
        assert(optionName != null);
        assert(args != null);
        for (int i = 0; i < args.length - 1; i++) {
            if (!args[i].isEmpty() && args[i].charAt(0) == '-' && args[i].equals(String.format("-%s", optionName)))
                return new Option<String>(args[i+1]);
        }
        return Option.NONE;
    }
    
    private static List<String> getOptions(final String[] args) {
        assert(args != null);
        final List<String> options = new ArrayList<String>() {{
            for (int i = 0; i < args.length; i++) {
                if (!args[i].isEmpty() && args[i].charAt(0) == '-') {
                    add(args[i].substring(1, args[i].length()));
                    i++; // skip the value even if it begins with '-'
                }
            }
        }};
        return options;
    }
    // </editor-fold>
}
