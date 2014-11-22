package SigmaEC.app.lsystems;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class LRuleTest {
    
    public LRuleTest() { }
    
    @Before
    public void setUp() {
    }

    
    @Test
    public void testConstructor1() {
        System.out.println("constructor");
        final String pred = "a";
        final String succ = "b";
        final LRule sut = new LRule(pred, succ);
        assertEquals(pred, sut.getPredecessor());
        assertEquals(succ, sut.getSuccessor());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testConstructor2() {
        System.out.println("constructor");
        final String pred = " ";
        final String succ = "b";
        final LRule sut = new LRule(pred, succ);
        assertEquals(pred, sut.getPredecessor());
        assertEquals(succ, sut.getSuccessor());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testConstructor3() {
        System.out.println("constructor");
        final String pred = "a";
        final String succ = "bc dddq3p957h0";
        final LRule sut = new LRule(pred, succ);
        assertEquals(pred, sut.getPredecessor());
        assertEquals(succ, sut.getSuccessor());
        assertTrue(sut.repOK());
    }
    
    @Test
    public void testConstructor4() {
        System.out.println("constructor");
        final String pred = "a";
        final String succ = "";
        final LRule sut = new LRule(pred, succ);
        assertEquals(pred, sut.getPredecessor());
        assertEquals(succ, sut.getSuccessor());
        assertTrue(sut.repOK());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor5() {
        System.out.println("constructor");
        final String pred = "ab";
        final String succ = "b";
        final LRule sut = new LRule(pred, succ);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor6() {
        System.out.println("constructor");
        final String pred = "";
        final String succ = "b";
        final LRule sut = new LRule(pred, succ);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor7() {
        System.out.println("constructor");
        final String pred = ">";
        final String succ = "b";
        final LRule sut = new LRule(pred, succ);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor8() {
        System.out.println("constructor");
        final String pred = "a";
        final String succ = "b>c";
        final LRule sut = new LRule(pred, succ);
    }

    /** Test of fromString method, of class LRule. */
    @Test
    public void testFromString1() {
        System.out.println("fromString");
        final String ruleString = "a>b";
        final LRule expResult = new LRule("a", "b");
        final LRule result = LRule.fromString(ruleString);
        assertEquals(expResult, result);
        assertTrue(result.repOK());
    }

    /** Test of fromString method, of class LRule. */
    @Test
    public void testFromString2() {
        System.out.println("fromString");
        final String ruleString = "a>beprhg8q0 ";
        final LRule expResult = new LRule("a", "beprhg8q0 ");
        final LRule result = LRule.fromString(ruleString);
        assertEquals(expResult, result);
        assertTrue(result.repOK());
    }

    /** Test of fromString method, of class LRule. */
    @Test(expected = IllegalArgumentException.class)
    public void testFromString3() {
        System.out.println("fromString");
        final String ruleString = "a>beprh>g8q0 ";
        final LRule result = LRule.fromString(ruleString);
    }

    /** Test of fromString method, of class LRule. */
    @Test(expected = IllegalArgumentException.class)
    public void testFromString4() {
        System.out.println("fromString");
        final String ruleString = ">beprhg8q0 ";
        final LRule result = LRule.fromString(ruleString);
    }

    /** Test of fromString method, of class LRule. */
    @Test(expected = IllegalArgumentException.class)
    public void testFromString5() {
        System.out.println("fromString");
        final String ruleString = "aD>beprhg8q0 ";
        final LRule result = LRule.fromString(ruleString);
    }
    
    /** Test of apply method, of class LRule. */
    @Test
    public void testApply1() {
        System.out.println("apply");
        final LRule instance = new LRule("a", "b");
        final String input = "abc";
        final String expResult = "bbc";
        final String result = instance.apply(input);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    /** Test of apply method, of class LRule. */
    @Test
    public void testApply2() {
        System.out.println("apply");
        final LRule instance = new LRule("a", "b");
        final String input = "abqgrpweurgaghwupa";
        final String expResult = "bbqgrpweurgbghwupb";
        final String result = instance.apply(input);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
    
    /** Test of apply method, of class LRule. */
    @Test
    public void testApply3() {
        System.out.println("apply");
        final LRule instance = new LRule("a", "HAHA");
        final String input = "The apple tree fell down around dawn.";
        final String expResult = "The HAHApple tree fell down HAHAround dHAHAwn.";
        final String result = instance.apply(input);
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }

    /** Test of equals method, of class LRule. */
    @Test
    public void testEquals1() {
        System.out.println("equals");
        final LRule instance1 = new LRule("a", "b");
        final LRule instance2 = new LRule("a", "b");
        
        assertTrue(instance1.equals(instance2));
        assertEquals(instance1.hashCode(), instance2.hashCode());
        assertTrue(instance1.repOK());
        assertTrue(instance2.repOK());
    }

    /** Test of equals method, of class LRule. */
    @Test
    public void testEquals2() {
        System.out.println("equals");
        final LRule instance1 = new LRule("a", "b");
        final LRule instance2 = new LRule("a", "");
        
        assertFalse(instance1.equals(instance2));
        assertTrue(instance1.repOK());
        assertTrue(instance2.repOK());
    }

    /** Test of equals method, of class LRule. */
    @Test
    public void testEquals3() {
        System.out.println("equals");
        final LRule instance1 = new LRule("a", "b");
        final LRule instance2 = new LRule("b", "b");
        
        assertFalse(instance1.equals(instance2));
        assertTrue(instance1.repOK());
        assertTrue(instance2.repOK());
    }

    /** Test of equals method, of class LRule. */
    @Test
    public void testEquals4() {
        System.out.println("equals");
        final LRule instance1 = new LRule("a", "b");
        final String instance2 = "a>b";
        
        assertFalse(instance1.equals(instance2));
        assertTrue(instance1.repOK());
    }

    /** Test of toString method, of class LRule. */
    @Test
    public void testToString() {
        System.out.println("toString");
        final LRule instance = new LRule("a", "b");
        final String expResult = "[LRule: predecessor=a, successor=b]";
        final String result = instance.toString();
        assertEquals(expResult, result);
        assertTrue(instance.repOK());
    }
}