package SigmaEC.util;

import java.util.Collection;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class TestMiscTest {
    
    public TestMiscTest() { }

    @Test
    public void testAllCombinations1() {
        System.out.println("allCombinations");
        final Object[][] domainVariables = new Object[][] {
            { "T", "F" },
            { "1", "2" }
        };
        final Collection<Object[]> expResult = new HashSet<Object[]>() {{
            add(new Object[] { "T", "1" });
            add(new Object[] { "T", "2" });
            add(new Object[] { "F", "1" });
            add(new Object[] { "F", "2" });
        }};
        final Collection result = TestMisc.allCombinations(domainVariables);
        assertTrue(Misc.collectionOfArraysEquals(expResult, result));
    }

    @Test
    public void testAllCombinations2() {
        System.out.println("allCombinations");
        final Object[][] domainVariables = new Object[][] {
            { "T", "F" },
            { "1", "2", "3" }
        };
        final Collection<Object[]> expResult = new HashSet<Object[]>() {{
            add(new Object[] { "T", "1" });
            add(new Object[] { "T", "2" });
            add(new Object[] { "T", "3" });
            add(new Object[] { "F", "1" });
            add(new Object[] { "F", "2" });
            add(new Object[] { "F", "3" });
        }};
        final Collection result = TestMisc.allCombinations(domainVariables);
        assertTrue(Misc.collectionOfArraysEquals(expResult, result));
    }

    @Test
    public void testAllCombinations3() {
        System.out.println("allCombinations");
        final Object[][] domainVariables = new Object[][] {
            { "T" },
            { "1", "2", "3" }
        };
        final Collection<Object[]> expResult = new HashSet<Object[]>() {{
            add(new Object[] { "T", "1" });
            add(new Object[] { "T", "2" });
            add(new Object[] { "T", "3" });
        }};
        final Collection result = TestMisc.allCombinations(domainVariables);
        assertTrue(Misc.collectionOfArraysEquals(expResult, result));
    }

    @Test
    public void testAllCombinations4() {
        System.out.println("allCombinations");
        final Object[][] domainVariables = new Object[][] {
            { "T", null },
            { "1", "2", "3" }
        };
        final Collection<Object[]> expResult = new HashSet<Object[]>() {{
            add(new Object[] { "T", "1" });
            add(new Object[] { "T", "2" });
            add(new Object[] { "T", "3" });
            add(new Object[] { null, "1" });
            add(new Object[] { null, "2" });
            add(new Object[] { null, "3" });
        }};
        final Collection result = TestMisc.allCombinations(domainVariables);
        assertTrue(Misc.collectionOfArraysEquals(expResult, result));
    }
    
}
