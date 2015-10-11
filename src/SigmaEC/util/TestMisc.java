package SigmaEC.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Helper methods for testing.
 * 
 * @author Eric O. Scott
 */
public class TestMisc {

    /** Private constructor throws an error if called. */
    private TestMisc() throws AssertionError {
        throw new AssertionError(String.format("%s: Cannot create instance of static class.", Misc.class.getSimpleName()));
    }
    
    /**
     * Generates all combinations of a number of test characters.
     * 
     * @param domainVariables An array defining the input domain model.
     * The array in the ith row defines the values that the ith variable
     * may assume.
     * 
     * @return An array of arrays, where each array contains some combination
     * of the values that the variables may assume.  All the arrays will be
     * of equal length, and the jth element of each array will represent the
     * ith variable of the domain model.
     */
    public static Collection<Object[]> allCombinations(final Object[][] domainVariables) {
        assert(domainVariables != null);
        assert(!Misc.containsNulls(domainVariables));
        final Set<List> combinations = allCombinations(domainVariables, 0);
        final Set<Object[]> combinationArrays = new HashSet<>();
        for (final List l : combinations)
            combinationArrays.add(l.toArray());
        return combinationArrays;
    }
    
    private static Set<List> allCombinations(final Object[][] domainVariables, final int i) {
        assert(domainVariables != null);
        assert(!Misc.containsNulls(domainVariables));
        assert(i >= 0);
        assert(i <= domainVariables.length);
        
        if (i == domainVariables.length)
            return new HashSet<>();
        
        final Set<List> subResult = allCombinations(domainVariables, i + 1);
        
        final Set<List> newResult = new HashSet<>();
        
        final Object[] varValues = domainVariables[i];
        for (final Object value : varValues) {
            if (subResult.isEmpty())
                newResult.add(new LinkedList() {{ add(value); }});
            else {
                for (final List testInput : subResult) {
                    final LinkedList newInput = new LinkedList(testInput);
                    newInput.push(value);
                    newResult.add(newInput);
                }
            }
        }
        return newResult;
    }
}
