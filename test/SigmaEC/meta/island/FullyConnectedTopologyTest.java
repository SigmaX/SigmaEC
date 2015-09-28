package SigmaEC.meta.island;

import SigmaEC.util.Parameters;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Eric O. Scott
 */
public class FullyConnectedTopologyTest {
    private final static String base = "base";
    
    /** Test of numIslands method, of class FullyConnectedTopology. */
    @Test
    public void testNumIslands1() {
        System.out.println("numIslands");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "1")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        int result = instance.numIslands();
        assertEquals(1, result);
        assertNotEquals(2, result);
        assertTrue(instance.repOK());
    }
    
    /** Test of numIslands method, of class FullyConnectedTopology. */
    @Test
    public void testNumIslands2() {
        System.out.println("numIslands");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "2")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        int result = instance.numIslands();
        assertEquals(2, result);
        assertNotEquals(1, result);
        assertTrue(instance.repOK());
    }
    
    /** Test of numIslands method, of class FullyConnectedTopology. */
    @Test(expected = IllegalStateException.class)
    public void testNumIslands3() {
        System.out.println("numIslands ISE");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "0")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
    }
    
    /** Test of numIslands method, of class FullyConnectedTopology. */
    @Test(expected = IllegalStateException.class)
    public void testNumIslands4() {
        System.out.println("numIslands ISE");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "-1")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
    }

    /** Test of isConnectected method, of class FullyConnectedTopology. */
    @Test
    public void testIsConnectected1() {
        System.out.println("isConnectected");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "2")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        assertTrue(instance.isConnectected(0, 1));
        assertTrue(instance.isConnectected(1, 0));
        assertFalse(instance.isConnectected(1, 1));
        assertFalse(instance.isConnectected(0, 0));
        assertTrue(instance.repOK());
    }

    /** Test of isConnectected method, of class FullyConnectedTopology. */
    @Test
    public void testIsConnectected2() {
        System.out.println("isConnectected");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "3")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        assertTrue(instance.isConnectected(0, 1));
        assertTrue(instance.isConnectected(0, 2));
        assertTrue(instance.isConnectected(1, 0));
        assertTrue(instance.isConnectected(1, 2));
        assertTrue(instance.isConnectected(2, 0));
        assertTrue(instance.isConnectected(2, 1));
        assertFalse(instance.isConnectected(0, 0));
        assertFalse(instance.isConnectected(1, 1));
        assertFalse(instance.isConnectected(2, 2));
        assertTrue(instance.repOK());
    }

    /** Test of getNeighbors method, of class FullyConnectedTopology. */
    @Test
    public void testGetNeighbors1() {
        System.out.println("getNeighbors");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "3")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        final Set<Integer> expResult = new HashSet<Integer>() {{ add(1); add(2); }};
        final Set<Integer> result = instance.getNeighbors(0);
        assertEquals(expResult, result);
        assertNotEquals(new HashSet<>(), result);
        assertTrue(instance.repOK());
    }

    /** Test of getNeighbors method, of class FullyConnectedTopology. */
    @Test
    public void testGetNeighbors2() {
        System.out.println("getNeighbors");
        final Parameters parameters = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "5")
                .build();
        final FullyConnectedTopology instance = new FullyConnectedTopology(parameters, base);
        final Set<Integer> expResult = new HashSet<Integer>() {{ add(0); add(1); add(3); add(4); }};
        final Set<Integer> result = instance.getNeighbors(2);
        assertEquals(expResult, result);
        assertNotEquals(new HashSet<>(), result);
        assertTrue(instance.repOK());
    }

    /** Test of equals method, of class FullyConnectedTopology. */
    @Test
    public void testEquals() {
        System.out.println("equals");
        final Parameters parametersA = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "5")
                .build();
        final Parameters parametersB = new Parameters.Builder(new Properties())
                .setParameter(Parameters.push(base, FullyConnectedTopology.P_ISLANDS), "1")
                .build();
        final FullyConnectedTopology instance1 = new FullyConnectedTopology(parametersA, base);
        final FullyConnectedTopology instance2 = new FullyConnectedTopology(parametersA, base);
        final FullyConnectedTopology instance3 = new FullyConnectedTopology(parametersB, base);
        
        assertEquals(instance1, instance1);
        assertEquals(instance2, instance2);
        assertEquals(instance3, instance3);
        
        assertEquals(instance1, instance2);
        assertEquals(instance2, instance1);
        assertEquals(instance1.hashCode(), instance2.hashCode());
        
        assertNotEquals(instance1, instance3);
        assertNotEquals(instance2, instance3);
        assertNotEquals(instance3, instance1);
        assertNotEquals(instance3, instance2);
    }
}
