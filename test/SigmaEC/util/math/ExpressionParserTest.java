package SigmaEC.util.math;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Parameterized happy-path tests for ExpressionParseTest that satisfy production rule coverage.
 * 
 * @author Eric 'Siggy' Scott
 */
@RunWith(Parameterized.class)
public class ExpressionParserTest {
    private final String expression;
    private final double expected;
    
    public ExpressionParserTest(final String expression, final double expected) {
        this.expression = expression;
        this.expected = expected;
    }
    
    @Parameters
    public static Collection<Object[]> addedNumbers() {
        return Arrays.asList(new Object[][] {
            { "3", 3.0},
            { "3.0", 3.0 },
            { "0", 0.0 },
            { "23459087", 23459087.0 },
            { " 2345.9087 ", 2345.9087 },
            { "0.882", 0.882 },
            
            { "3+2", 5.0 },
            { "3 + 2", 5.0 },
            { "3 - 2", 1.0 },
            { "3 - 3", 0.0},
            { " 3 - 4", -1.0},
            
            { "3+2*8", 19.0},
            { "2*8-3", 13.0},
            { "3+2-4*8", -27.0},
            { "3 + (2-4) *8", -13.0 },
            { "3 + 2 - 8", -3.0},
            { "3 - 8 + 2", -3.0},
            { "3/2 + 8", 9.5},
            { "8+3/2", 9.5},
            { "10.882/(0.02 + 19)", 0.572134595 },
            { "3.745 * (6.7 - 0)", 25.0915 }
        });
    }


    /** Test of eval method, of class ExpressionParser. */
    @Test
    public void testEval() {
        System.out.println("eval");
        double result = ExpressionParser.eval(expression);
        assertEquals(expected, result, 0.000001);
    }
}