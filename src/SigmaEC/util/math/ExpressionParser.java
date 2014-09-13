package SigmaEC.util.math;

import SigmaEC.util.Misc;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Parses and evaluates arithmetic expressions given in String form.
 * 
 * @author Eric 'Siggy' Scott
 */
public class ExpressionParser {
    public final static char[] operators = new char[] { '+', '-', '/', '*' };
    
    /** Private constructor throws an error if called. */
    private ExpressionParser() throws AssertionError {
        throw new AssertionError(String.format("%s: Cannot create instance of static class.", ExpressionParser.class.getSimpleName()));
    }
    
    public static double eval(final String expression) {
        assert(expression != null);
        assert(!expression.isEmpty());
        return evalRPN(infixToRPN(expression));
    }
    
    /** Convert an expression given in infix notation to reverse Polish notation. */
    private static Queue<String> infixToRPN(final String expression) {
        assert(expression != null);
        assert(!expression.isEmpty());
        final Queue<String> result = new LinkedList<String>();
        final Stack<String> operatorStack = new Stack<String>();
        
        // We proceed via Dijkstra's shunting-yard algorithm, without support for functions.
        for (final String t : tokenize(expression)) {
            if (Misc.isNumber(t))
                result.add(t);
            else if (isOperator(t)) {
                while (!operatorStack.empty() && isOperator(operatorStack.peek()) && 1 != compareOperators(t, operatorStack.peek()))
                    result.add(operatorStack.pop());
                operatorStack.add(t);
            }
            else if (t.equals("("))
                operatorStack.add(t);
            else if (t.equals(")")) {
                while (!operatorStack.peek().equals("(") && !operatorStack.isEmpty())
                    result.add(operatorStack.pop());
                if (operatorStack.isEmpty())
                    throw new IllegalArgumentException(String.format("%s: mismatched parantheses.", ExpressionParser.class.getSimpleName()));
                assert(operatorStack.peek().equals("("));
                operatorStack.pop();
            }
            else
                throw new IllegalArgumentException(String.format("%s: unrecognized token.", ExpressionParser.class.getSimpleName()));
        }
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek().equals("(") || operatorStack.peek().equals(")"))
                throw new IllegalArgumentException(String.format("%s: mismatched parantheses.", ExpressionParser.class.getSimpleName()));
            result.add(operatorStack.pop());
        }
        return result;
    }
    
    /** Divide the String into tokens, where each token is a number,
     *  operator, or a parenthesis. */
    private static List<String> tokenize(final String expression) {
        assert(expression != null);
        assert(!expression.isEmpty());
        
        final List<String> result = new ArrayList<String>();
        String numberToken = "";
        for (int i = 0; i < expression.length(); i++) {
            final char c = expression.charAt(i);
            if (isPartOfNumber(c)) {
                numberToken = numberToken + c;
                continue;
            }
            if (!numberToken.isEmpty()) {
                result.add(numberToken);
                numberToken = "";
            }
            
            if (isOperator(c) || c == '(' || c == ')')
                result.add(String.valueOf(c));
            else
                assert(c == ' ');
        }
        if (!numberToken.isEmpty())
            result.add(numberToken);
        return result;
    }
    
    /** Evaluate an expression given in reverse Polish notation. */
    private static double evalRPN(final Queue<String> rpnExpression) {
        assert(rpnExpression != null);
        assert(!rpnExpression.isEmpty());
        
        final Stack<Double> argumentStack = new Stack<Double>();
        for (final String t : rpnExpression) {
            if (Misc.isNumber(t))
                argumentStack.push(Double.valueOf(t));
            else if (isOperator(t)) {
                // The arguments are sitting on the stack in reverse order
                final double arg2 = argumentStack.pop();
                final double arg1 = argumentStack.pop();
                final double result = evaluate(arg1, arg2, t);
                argumentStack.push(result);
            }
        }
        assert(argumentStack.size() == 1);
        return argumentStack.pop();
    }
    
    private static double evaluate(final double arg1, final double arg2, final String operator) {
        assert(isOperator(operator));
        if (operator.equals("+")) return arg1 + arg2;
        if (operator.equals("-")) return arg1 - arg2;
        if (operator.equals("*")) return arg1 * arg2;
        if (operator.equals("/")) return arg1 / arg2;
        else
            throw new IllegalStateException();
    }
    
    private static boolean isPartOfNumber(final char c) {
        final int ascii = (int) c;
        return ascii >= 0x30 && ascii <= 0x39 || c == '.'; // 0-9 or '.'
    }
    
    private static boolean isOperator(final String s) {
        assert(s != null);
        if (s.length() != 1)
            return false;
        final char c = s.charAt(0);
        return isOperator(c);
    }
    
    private static boolean isOperator(final char c) {
        for (final char o : operators)
            if (c == o)
                return true;
        return false;
    }
    
    private static int compareOperators(final String o1, final String o2) {
        assert(isOperator(o1));
        assert(isOperator(o2));
        if (o1.equals("+"))
            return o2.equals("-") ? 0 : -1;
        if (o1.equals("-"))
            return o2.equals("+") ? 0 : -1;
        if (o1.equals("*"))
            return o2.equals("/") ? 0 : 1;
        if (o1.equals("/"))
            return o2.equals("*") ? 0 : 1;
        throw new IllegalStateException();
    }
}
