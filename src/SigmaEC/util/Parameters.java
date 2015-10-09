package SigmaEC.util;

import SigmaEC.ContractObject;
import SigmaEC.util.math.ExpressionParser;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class interprets a properties file as a simple declarative programming
 * language.  Properties can define parameters, objects, or references to
 * other parameters (allowing values and object instances to be reused in
 * many parts of the program).
 * 
 * @author Eric 'Siggy' Scott
 */
public class Parameters extends ContractObject {
    public final static String LIST_DELIMITER = ",";
    public final static String PROPERTY_DELIMITER = ".";
    public final static char REFERENCE_SYMBOL = '%';
    public final static char EXPRESSION_SYMBOL = '$';
    
    private final Properties properties;
    // Used to store instances that are referenced by other parameters with the "%param" syntax.
    private final Map<String, Object> instanceRegistry;
    
    // <editor-fold defaultstate="collapsed" desc="Construction">
    public Parameters(final Properties properties) {
        assert(properties != null);
        this.properties = (Properties) properties.clone();
        this.instanceRegistry = new HashMap<>();
        assert(repOK());
    }
    
    private Parameters(final Builder builder) {
        assert(builder != null);
        this.properties = (Properties) builder.properties.clone();
        this.instanceRegistry = new HashMap<>(builder.instanceRegistry);
    }
    
    public static class Builder {
        final Properties properties;
        final Map<String, Object> instanceRegistry = new HashMap<String, Object>();
        
        public Builder(final Properties properties) {
            assert(properties != null);
            this.properties = (Properties) properties.clone();
        }
        
        /** Add a parameter-instance pair to this Parameters database's registry
         * of objects. */
        public Builder registerInstance(final String parameter, final Object object) {
            assert(parameter != null);
            assert(object != null);
            instanceRegistry.put(parameter, object);
            return this;
        }
        
        /** Add a parameter-value pair to this Parameters database. */
        public Builder setParameter(final String parameter, final String value) {
            assert(parameter != null);
            assert(value != null);
            properties.put(parameter, value);
            return this;
        }
        
        /** Remove an entry from this Parameters database. */
        public Builder clearParameter(final String parameter) {
            assert(parameter != null);
            properties.remove(parameter);
            return this;
        }
        
        public Parameters build() {
            return new Parameters(this);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Helpers">
    public static String push(final String base, final String param) {
        assert(base != null);
        assert(param != null);
        return String.format("%s%s%s", base, PROPERTY_DELIMITER, param);
    }
    
    public boolean isDefined(final String parameterName) {
        assert(parameterName != null);
        return properties.containsKey(parameterName);
    }
    
    private static boolean isReference(final String parameterValue) {
        assert(parameterValue != null);
        assert(!parameterValue.isEmpty());
        return parameterValue.charAt(0) == REFERENCE_SYMBOL && !parameterValue.contains(LIST_DELIMITER);
    }
    
    private String dereferenceToValue(final String parameterValue) {
        assert(parameterValue != null);
        if (!isReference(parameterValue))
            return parameterValue;
        final String targetName = parameterValue.substring(1);
        if (instanceRegistry.containsKey(targetName))
            return targetName;
        final String targetValue = properties.getProperty(targetName);
        if (targetValue == null)
            throw new IllegalStateException(String.format("%s: referenced parameter %s not found.", this.getClass().getSimpleName(), targetName));
        if (isReference(targetValue))
            return dereferenceToValue(targetValue);
        else
            return targetValue;
    }
    
    private String dereferenceToParameter(final String parameterValue) {
        assert(parameterValue != null);
        assert(isReference(parameterValue));
        final String targetName = parameterValue.substring(1);
        final String targetValue = properties.getProperty(targetName);
        if (targetValue == null)
            throw new IllegalStateException(String.format("%s: referenced parameter %s not found.", this.getClass().getSimpleName(), targetName));
        else if (!isReference(targetValue))
            return targetName;
        else
            return dereferenceToParameter(targetValue);
    }
    
    private static boolean isExpression(final String parameterValue) {
        assert(parameterValue != null);
        return parameterValue.charAt(0) == EXPRESSION_SYMBOL;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Expression Evaluation">
    private double evalExpression(final String parameterValue) {
        assert(parameterValue != null);
        if (!(isExpression(parameterValue) || Misc.isNumber(parameterValue)))
                throw new IllegalStateException("Parameter value " + parameterValue + " is not a number or expression.");
        if (!isExpression(parameterValue))
            return Double.valueOf(parameterValue);
        final String expression = deReferenceExpression(parameterValue);
        return ExpressionParser.eval(expression);
    }
    
    /** Replace all references in an expression with their numeric values. */
    private String deReferenceExpression(String expression) {
        assert(expression != null);
        assert(expression.charAt(0) == EXPRESSION_SYMBOL);
        expression = expression.trim();
        final StringBuilder result = new StringBuilder();
        for (int i = 1; i < expression.length(); i++) {
            if (expression.charAt(i) != REFERENCE_SYMBOL)
                result.append(expression.charAt(i));
            else {
                int tokenEnd = findTokenEnd(expression, i);
                final String refToken = expression.substring(i, tokenEnd);
                final double val = evalExpression(dereferenceToValue(refToken));
                if (val < 0)
                    result.append("(0 - ").append(String.valueOf(Math.abs(val))).append(")"); // Hack in negative references, since our parser doesn't support negative numbers
                else
                    result.append(val);
                i = tokenEnd - 1;
            }
        }
        return result.toString();
    }
    
    private static int findTokenEnd(final String expression, final int startIndex) {
        assert(expression != null);
        assert(expression.charAt(startIndex) == REFERENCE_SYMBOL);
        final char[] endChars = Misc.prepend(new char[] { ' ', '(', ')' }, ExpressionParser.operators);
                
        for (int i = startIndex; i < expression.length(); i++)
            if (Misc.in(endChars, expression.charAt(i)))
                return i;
        return expression.length();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Basic Types">
    
    // <editor-fold defaultstate="collapsed" desc="Int">
    public int getIntParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        return (int) evalExpression(dereferenceToValue(value));
    }
    
    public Option<Integer> getOptionalIntParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        if (properties.containsKey(parameterName))
            return new Option<Integer>(getIntParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public int getOptionalIntParameter(final String parameterName, final int deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final Option<Integer> opt = getOptionalIntParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public int getOptionalIntParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Integer> opt = getOptionalIntParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getIntParameter(defaultParameter);
    }
    
    public int getOptionalIntParameter(final String parameterName, final String defaultParameter, final int defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Integer> opt = getOptionalIntParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalIntParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Long">
    public long getLongParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        return (long) evalExpression(dereferenceToValue(value));
    }
    
    public Option<Long> getOptionalLongParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        if (properties.containsKey(parameterName))
            return new Option<Long>(getLongParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public long getOptionalLongParameter(final String parameterName, final long deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final Option<Long> opt = getOptionalLongParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public long getOptionalLongParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Long> opt = getOptionalLongParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getLongParameter(defaultParameter);
    }
    
    public long getOptionalLongParameter(final String parameterName, final String defaultParameter, final long defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Long> opt = getOptionalLongParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalLongParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Boolean">
    public boolean getBooleanParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        value = dereferenceToValue(value);
        if (!value.toLowerCase().equals("true") && !value.toLowerCase().equals("false"))
            throw new IllegalStateException(String.format("%s: Parameter '%s' is set to '%s', but must be either 'true' or 'false'.", Parameters.class.getSimpleName(), parameterName, value));
        return Boolean.parseBoolean(value);
    }
    
    public Option<Boolean> getOptionalBooleanParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        if (properties.containsKey(parameterName))
            return new Option<Boolean>(getBooleanParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public boolean getOptionalBooleanParameter(final String parameterName, final boolean deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final Option<Boolean> opt = getOptionalBooleanParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public boolean getOptionalBooleanParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Boolean> opt = getOptionalBooleanParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getBooleanParameter(defaultParameter);
    }
    
    public boolean getOptionalBooleanParameter(final String parameterName, final String defaultParameter, final boolean defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Boolean> opt = getOptionalBooleanParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalBooleanParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Double">
    public double getDoubleParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        return evalExpression(dereferenceToValue(value));
    }
    
    public Option<Double> getOptionalDoubleParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        if (properties.containsKey(parameterName))
            return new Option<>(getDoubleParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public double getOptionalDoubleParameter(final String parameterName, final double deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final Option<Double> opt = getOptionalDoubleParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public double getOptionalDoubleParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Double> opt = getOptionalDoubleParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getDoubleParameter(defaultParameter);
    }
    
    public double getOptionalDoubleParameter(final String parameterName, final String defaultParameter, final double defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<Double> opt = getOptionalDoubleParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalDoubleParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Double Array">
    
    public double[] getDoubleArrayParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String[] doubleStrings = getStringParameter(parameterName).split(LIST_DELIMITER);
        final double[] array = new double[doubleStrings.length];
        for (int i = 0; i < doubleStrings.length; i++)
            array[i] = Double.parseDouble(doubleStrings[i]);
        return array;
    }
    public Option<double[]> getOptionalDoubleArrayParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        if (properties.containsKey(parameterName))
            return new Option<>(getDoubleArrayParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public double[] getOptionalDoubleArrayParameter(final String parameterName, final double[] deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(deflt != null);
        final Option<double[]> opt = getOptionalDoubleArrayParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public double[] getOptionalDoubleArrayParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<double[]> opt = getOptionalDoubleArrayParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getDoubleArrayParameter(defaultParameter);
    }
    
    public double[] getOptionalDoubleArrayParameter(final String parameterName, final String defaultParameter, final double[] defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        assert(defaultValue != null);
        final Option<double[]> opt = getOptionalDoubleArrayParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalDoubleArrayParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="String">
    public String getStringParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value))
            return dereferenceToValue(value);
        return value;
    }
    
    public Option<String> getOptionalStringParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        return (value == null) ? Option.NONE : new Option<String>(getStringParameter(parameterName));
    }
    
    public String getOptionalStringParameter(final String parameterName, final String deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final Option<String> opt = getOptionalStringParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public String getOptionalStringParameter(final String parameterName, final String defaultParameter, final String defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        assert(defaultValue != null);
        final Option<String> opt = getOptionalStringParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalStringParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="String Array">
    public String[] getStringArrayParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String[] result = getStringParameter(parameterName).split(LIST_DELIMITER);
        for (int i = 0; i < result.length; i++)
            result[i] = result[i].trim();
        return result;
    }
    
    
    public Option<String[]> getOptionalStringArrayParameter(final String parameterName) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        final String value = properties.getProperty(parameterName);
        return (value == null) ? Option.NONE : new Option<String[]>(getStringArrayParameter(parameterName));
    }
    
    public String[] getOptionalStringArrayParameter(final String parameterName, final String[] deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(deflt != null);
        assert(!Misc.containsNulls(deflt));
        final Option<String[]> opt = getOptionalStringArrayParameter(parameterName);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public String[] getOptionalStringArrayParameter(final String parameterName, final String defaultParameter) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<String[]> opt = getOptionalStringArrayParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getStringArrayParameter(defaultParameter);
    }
    
    public String[] getOptionalStringArrayParameter(final String parameterName, final String defaultParameter, final String[] defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        assert(defaultValue != null);
        assert(!Misc.containsNulls(defaultValue));
        final Option<String[]> opt = getOptionalStringArrayParameter(parameterName);
        if (opt.isDefined())
            return opt.get();
        return getOptionalStringArrayParameter(defaultParameter, defaultValue);
    }
    // </editor-fold>
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Instances">
    
    // <editor-fold defaultstate="collapsed" desc="Single Instances">
    private <T> T getInstanceFromClassName(final String className, final String parameterName, final Class expectedSuperClass) {
        // Check that the class exists and is the expected subtype
        final Class c;
        try {
            c = Class.forName(className);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(String.format("%s: No such class '%s', requested by parameter '%s' (%s).", this.getClass().getSimpleName(), className, parameterName, ex.getMessage()));
        }
        if (!expectedSuperClass.isAssignableFrom(c))
            throw new IllegalStateException(String.format("%s: Class '%s', requested by the parameter '%s' is not a subtype of '%s'.", this.getClass().getSimpleName(), className, parameterName, expectedSuperClass.getSimpleName()));
        try {
            final Constructor constructor = c.getDeclaredConstructor(new Class[] { Parameters.class, String.class });
            return (T) constructor.newInstance(this, parameterName);
        } catch (final Exception ex) {
            Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException(String.format("%s: failed to create instance of class '%s' (%s).", Parameters.class.getSimpleName(), className, ex.getMessage()));
        }
    }
   
    private void registerInstance(final String parameterName, final Object instance) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(instance != null);
        instanceRegistry.put(parameterName, instance);
    }
    
    public <T> T getInstanceFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(expectedSuperClass != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            if (instanceRegistry.containsKey(parameterName))
                return (T) instanceRegistry.get(parameterName);
            else
                throw new IllegalStateException(String.format("%s: Parameter '%s' was not found.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value)) {
            final String drefVal = dereferenceToValue(value);
            if (instanceRegistry.containsKey(drefVal))
                return (T) instanceRegistry.get(drefVal);
            else {
                final String drefParam = dereferenceToParameter(value);
                final T result = getInstanceFromClassName(drefVal, drefParam, expectedSuperClass);
                registerInstance(drefParam, result);
                return result;
            }
        }
        final T result;
        if (instanceRegistry.containsKey(parameterName))
            result = (T) instanceRegistry.get(parameterName);
        else
            result = getInstanceFromClassName(value, parameterName, expectedSuperClass);
        registerInstance(parameterName, result);
        return result;
    }
    
    public <T> Option<T> getOptionalInstanceFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(expectedSuperClass != null);
        if (properties.containsKey(parameterName))
            return new Option<>((T) getInstanceFromParameter(parameterName, expectedSuperClass));
        else
            return Option.NONE;
    }
    
    public <T> T getOptionalInstanceFromParameter(final String parameterName, final T deflt) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(deflt != null);
        final Option<T> opt = getOptionalInstanceFromParameter(parameterName, deflt.getClass());
        assert(repOK());
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public <T> T getOptionalInstanceFromParameter(final String parameterName, final String defaultParameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameterName != null);
        assert(!defaultParameterName.isEmpty());
        final Option<T> opt = getOptionalInstanceFromParameter(parameterName, expectedSuperClass);
        assert(repOK());
        if (opt.isDefined())
            return opt.get();
        return getInstanceFromParameter(defaultParameterName, expectedSuperClass);
    }
    
    public <T> T getOptionalInstanceFromParameter(final String parameterName, final String defaultParameterName, final T defaultValue) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameterName != null);
        assert(!defaultParameterName.isEmpty());
        final Option<T> opt = getOptionalInstanceFromParameter(parameterName, defaultValue.getClass());
        assert(repOK());
        if (opt.isDefined())
            return opt.get();
        return getOptionalInstanceFromParameter(defaultParameterName, defaultValue);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="List of Instances">
    public <T> List<T> getInstancesFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(expectedSuperClass != null);
        final String value = properties.getProperty(parameterName);
        if (value == null || value.isEmpty())
            throw new IllegalStateException(String.format("%s: Parameter '%s' was empty or not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value))
            return (List<T>) instanceRegistry.get(dereferenceToValue(value));
        
        final String[] classNames = value.split(LIST_DELIMITER);
        final List<T> result =  new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++) {
                if (classNames[i].trim().isEmpty())
                    throw new IllegalStateException(String.format("%s: encountered an empty element in the list defined by '%s'.", Parameters.class.getSimpleName(), parameterName));
                if (isReference(classNames[i].trim()))
                    add((T) getInstanceFromParameter(dereferenceToParameter(classNames[i].trim()), expectedSuperClass));
                else
                    add((T) getInstanceFromClassName(classNames[i].trim(), push(parameterName, String.valueOf(i)), expectedSuperClass));
            }
        }};
        registerInstance(parameterName, result);
        return result;
    }
    
    public <T> Option<List<T>> getOptionalInstancesFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(expectedSuperClass != null);
        if (properties.containsKey(parameterName))
            return new Option<>((List<T>) getInstancesFromParameter(parameterName, expectedSuperClass));
        else
            return Option.NONE;
    }
    
    public <T> List<T> getOptionalInstancesFromParameter(final String parameterName, final List<T> deflt, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(deflt != null);
        assert(!deflt.isEmpty());
        assert(!Misc.containsNulls(deflt));
        final Option<List<T>> opt = getOptionalInstancesFromParameter(parameterName, expectedSuperClass);
        return opt.isDefined() ? opt.get() : deflt;
    }
    
    public <T> List<T> getOptionalInstancesFromParameter(final String parameterName, final String defaultParameter, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        final Option<List<T>> opt = getOptionalInstancesFromParameter(parameterName, expectedSuperClass);
        if (opt.isDefined())
            return opt.get();
        return getInstancesFromParameter(defaultParameter, expectedSuperClass);
    }
    
    public <T> List<T> getOptionalInstancesFromParameter(final String parameterName, final String defaultParameter, final List<T> deflt, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(!parameterName.isEmpty());
        assert(defaultParameter != null);
        assert(!defaultParameter.isEmpty());
        assert(deflt != null);
        assert(!deflt.isEmpty());
        assert(!Misc.containsNulls(deflt));
        final Option<List<T>> opt = getOptionalInstancesFromParameter(parameterName, expectedSuperClass);
        if (opt.isDefined())
            return opt.get();
        return getOptionalInstancesFromParameter(defaultParameter, deflt, expectedSuperClass);
    }
    // <?editor-fold>
    
    // </editor-fold>
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return LIST_DELIMITER != null
                && !LIST_DELIMITER.isEmpty()
                && PROPERTY_DELIMITER != null
                && !PROPERTY_DELIMITER.isEmpty()
                && properties != null
                && instanceRegistry != null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Parameters))
            return false;
        final Parameters ref = (Parameters)o;
        return properties.equals(ref.properties)
                && instanceRegistry.equals(ref.instanceRegistry);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.properties != null ? this.properties.hashCode() : 0);
        hash = 97 * hash + (this.instanceRegistry != null ? this.instanceRegistry.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("[%s: properties=%s, instanceRegistry=%s]", this.getClass().getSimpleName(), properties, instanceRegistry);
    }
    // </editor-fold>
}
