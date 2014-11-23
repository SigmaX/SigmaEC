package SigmaEC.app.lsystems;

import SigmaEC.ContractObject;
import SigmaEC.util.Args;
import SigmaEC.util.Option;
import SigmaEC.util.Parameters;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JFrame;

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
    private final static String BASE = "lsystem";
    private final static String A_PARAMETER_FILE = "p";
    private final static String A_NUM_STEPS = "n";
    private final static String A_INPUT_STRING = "i";
    private final static String A_SHOW_TURTLE = "t";
    
    private final static List<String> allowedOptions = new ArrayList<String>() {{ add("p"); add("n"); add("i"); add("t"); }};
    private final static String usage = "-p parameterFile -n numSteps -i inputString -t showTurtle?";
    
    public static void main(final String[] args) {
        assert(args != null);
        if (!Args.checkOptions(args, allowedOptions) || !Args.getOption(A_PARAMETER_FILE, args).isDefined()) {
            System.err.println(usage);
            System.exit(0);
        }
        
        final String parameterFileName = Args.getOption(A_PARAMETER_FILE, args).get();
        final int numSteps = Integer.valueOf(Args.getRequiredOption(A_NUM_STEPS, args, allowedOptions, usage));
        final String inputString = Args.getRequiredOption(A_INPUT_STRING, args, allowedOptions, usage);
        final Option<String> showTurtleOpt = Args.getOption(A_SHOW_TURTLE, args);
        final boolean showTurtle = showTurtleOpt.isDefined() ? showTurtleOpt.get().equals("true") : false;
        assert(parameterFileName != null);
        assert(numSteps >= 0);
        assert(inputString != null);
        
        try {
            final Properties properties = new Properties();
            final FileInputStream pInput = new FileInputStream(parameterFileName);
            properties.load(pInput);
            final Parameters parameters = new Parameters(properties);
            final LSystem lSystem = new LSystem(parameters, BASE);
            final String result = lSystem.apply(inputString, numSteps);
            System.out.println(result);
            if (showTurtle) {
                System.out.println("Showing turtle...");
                final TurtleGraphics tg = new TurtleGraphics(parameters, Parameters.push(BASE, "turtle"), result);
                final Shape path = tg.getPath();
                final JFrame frame = new JFrame();
                frame.setPreferredSize(new Dimension(1024, 768));
                frame.setContentPane(new Container() {
                    @Override
                    public void paint(final Graphics g) {
                        super.paint(g);
                        final Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(Color.BLUE);
                        g2.draw(path);
                    }
                });

                frame.pack();
                frame.setVisible(true);
            }
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
