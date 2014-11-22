package SigmaEC.app.lsystems;

import SigmaEC.ContractObject;
import SigmaEC.util.Args;
import SigmaEC.util.Parameters;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class LSystem extends ContractObject {
    public final static String P_RULES = "rules";
    
    private List<LRule> rules;

    public List<LRule> getRules() {
        return new ArrayList<LRule>(rules);
    }
    
    public LSystem(final Parameters parameters, final String base) {
        assert(parameters != null);
        assert(base != null);
        
        final String[] ruleStrings = parameters.getStringArrayParameter(Parameters.push(base, P_RULES));
        rules = new ArrayList(){{
            for (final String rs : ruleStrings)
                add(LRule.fromString(rs));
        }};
        assert(repOK());
    }
    
    public String apply(final String input, final int numSteps) {
        assert(input != null);
        assert(numSteps >= 0);
        String result = input;
        for (int i = 0; i < numSteps; i++)
            for (final LRule r : rules)
                result = r.apply(result);
        assert(repOK());
        return result;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Main">
    
    private final static String A_PARAMETER_FILE = "p";
    private final static String A_NUM_STEPS = "n";
    private final static String A_INPUT_STRING = "i";
    private final static List<String> allowedOptions = new ArrayList<String>() {{ add("p"); add("n"); add("i"); }};
    private final static String usage = "-p parameterFile -n numSteps -i inputString";
    
    public static void main(final String[] args) {
        assert(args != null);
        if (!Args.checkOptions(args, allowedOptions) || !Args.getOption(A_PARAMETER_FILE, args).isDefined()) {
            System.err.println(usage);
            System.exit(0);
        }
        
        final String parameterFileName = Args.getOption(A_PARAMETER_FILE, args).get();
        final int numSteps = Integer.valueOf(Args.getRequiredOption(A_NUM_STEPS, args, allowedOptions, usage));
        final String inputString = Args.getRequiredOption(A_INPUT_STRING, args, allowedOptions, usage);
        
        assert(parameterFileName != null);
        assert(numSteps >= 0);
        assert(inputString != null);
        
        try {
            final Properties properties = new Properties();
            final FileInputStream pInput = new FileInputStream(parameterFileName);
            properties.load(pInput);
            final Parameters parameters = new Parameters(properties);
            final LSystem lSystem = new LSystem(parameters, "lsystem");
            System.out.println(lSystem.apply(inputString, numSteps));
        }
        catch (final Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return rules != null
                && !rules.isEmpty()
                && P_RULES != null
                && !P_RULES.isEmpty();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LSystem))
            return false;
        final LSystem ref = (LSystem) o;
        return rules.equals(ref.rules);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.rules != null ? this.rules.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: rules=%s]", this.getClass().getSimpleName(), rules.toString());
    }
    // </editor-fold>
}
