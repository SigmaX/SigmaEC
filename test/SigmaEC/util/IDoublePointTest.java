package SigmaEC.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class IDoublePointTest
{
    private IDoublePoint SUT;
    private final double X = 3.3;
    private final double Y = 0.1;
    
    public IDoublePointTest()
    {
    }
    
    @Before
    public void setUp()
    {
        SUT = new IDoublePoint(X, Y);
    }

    /** Test of constructor method, of class IDoublePoint. */
    @Test
    public void testConstructor()
    {
        System.out.println("ctor");
        assertEquals(SUT.x, X, 0);
        assertEquals(SUT.y, Y, 0);
    }

    /** Test of toString method, of class IDoublePoint. */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        String expResult = "[IDoublePoint: x=3.3, y=0.1]";
        String result = SUT.toString();
        assertEquals(expResult, result);
    }
}
