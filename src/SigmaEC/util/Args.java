package SigmaEC.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for parsing command-line arguments.
 * 
 * @author Eric 'Siggy' Scott
 */
public class Args {
    /** Private constructor throws an error if called. */
    private Args() throws AssertionError {
        throw new AssertionError(String.format("%s: Cannot create instance of static class.", Misc.class.getSimpleName()));
    }
    
    public static boolean checkOptions(final String[] args, final List<String> allowedOptions) {
        assert(args != null);
        for (final String option : getOptions(args))
            if (!allowedOptions.contains(option))
                return false;
        return true;
    }
    
    public static Option<String> getOption(final String optionName, final String[] args) {
        assert(optionName != null);
        assert(args != null);
        for (int i = 0; i < args.length - 1; i++) {
            if (!args[i].isEmpty() && args[i].charAt(0) == '-' && args[i].equals(String.format("-%s", optionName)))
                return new Option<String>(args[i+1]);
        }
        return Option.NONE;
    }
    
    public static String getRequiredOption(final String optionName, final String[] args, final List<String> allowedOptions, final String usage) {
        assert(optionName != null);
        assert(args != null);
        
        final Option<String> opt = getOption(optionName, args);
        if (!opt.isDefined() || !checkOptions(args, allowedOptions)) {
            System.err.println(usage);
            System.exit(0);
        }
        return opt.get();
    }
    
    public static List<String> getOptions(final String[] args) {
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
}
