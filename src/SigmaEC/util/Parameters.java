package SigmaEC.util;

import SigmaEC.ContractObject;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Parameters extends ContractObject {
    private final static String LIST_DELIMITER = ",";
    private final static String PROPERTY_DELIMITER = ".";
    private final static char REFERENCE_SYMBOL = '%';
    
    private final Properties properties;
    // Used to store instances that are referenced by other parameters with the "%param" syntax.
    private final Map<String, Object> instanceRegistry = new HashMap<String, Object>();
    
    public Parameters(final Properties properties) {
        assert(properties != null);
        this.properties = (Properties) properties.clone();
        assert(repOK());
    }
    
    public static String push(final String base, final String param) {
        assert(base != null);
        assert(param != null);
        return String.format("%s%s%s", base, PROPERTY_DELIMITER, param);
    }
    
    private static boolean isReference(final String parameterValue) {
        assert(parameterValue != null);
        return parameterValue.charAt(0) == REFERENCE_SYMBOL;
    }
    
    private static String dereference(final String parameterValue) {
        assert(parameterValue != null);
        assert(isReference(parameterValue));
        return parameterValue.substring(1);
    }
    
    public boolean isDefined(final String parameterName) {
        assert(parameterName != null);
        return properties.containsKey(parameterName);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Basic Types">
    public int getIntParameter(final String parameterName) {
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (value.charAt(0) == REFERENCE_SYMBOL)
            return getIntParameter(value.substring(1));
        return Integer.parseInt(value);
    }
    
    public Option<Integer> getOptionalIntParameter(final String parameterName) {
        assert(parameterName != null);
        if (properties.containsKey(parameterName))
            return new Option<Integer>(getIntParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public boolean getBooleanParameter(final String parameterName) {
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (value.charAt(0) == REFERENCE_SYMBOL)
            return getBooleanParameter(value.substring(1));
        return Boolean.parseBoolean(value);
    }
    
    public Option<Boolean> getOptionalBooleanParameter(final String parameterName) {
        assert(parameterName != null);
        if (properties.containsKey(parameterName))
            return new Option<Boolean>(getBooleanParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public double getDoubleParameter(final String parameterName) {
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value))
            return getDoubleParameter(dereference(value));
        return Double.parseDouble(value);
    }
    
    public Option<Double> getOptionalDoubleParameter(final String parameterName) {
        assert(parameterName != null);
        if (properties.containsKey(parameterName))
            return new Option<Double>(getDoubleParameter(parameterName));
        else
            return Option.NONE;
    }
    
    public double[] getDoubleArrayParameter(final String parameterName) {
        final String[] doubleStrings = getStringParameter(parameterName).split(LIST_DELIMITER);
        final double[] array = new double[doubleStrings.length];
        for (int i = 0; i < doubleStrings.length; i++)
            array[i] = Double.parseDouble(doubleStrings[i]);
        return array;
    }
    
    public String getStringParameter(final String parameterName) {
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (value.charAt(0) == REFERENCE_SYMBOL)
            return getStringParameter(value.substring(1));
        return value;
    }
    
    public Option<String> getOptionalStringParameter(final String parameterName) {
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        return (value == null) ? Option.NONE : new Option<String>(value);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Instances">
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
   
    private void registerInstanceIfReferenced(final String parameterName, final Object instance) {
        assert(parameterName != null);
        assert(instance != null);
        if (properties.containsValue(REFERENCE_SYMBOL + parameterName))
            instanceRegistry.put(parameterName, instance);
    }
    
    public <T> T getInstanceFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value))
            return (T) instanceRegistry.get(dereference(value));

        final T result = getInstanceFromClassName(value, parameterName, expectedSuperClass);
        registerInstanceIfReferenced(parameterName, result);
        return result;
    }
    
    public <T> Option<T> getOptionalInstanceFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        if (properties.containsKey(parameterName))
            return new Option<T>((T) getInstanceFromParameter(parameterName, expectedSuperClass));
        else
            return Option.NONE;
    }
    
    public <T> List<T> getInstancesFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was not found in properties.", Parameters.class.getSimpleName(), parameterName));
        if (isReference(value))
            return (List<T>) instanceRegistry.get(dereference(value));
        
        final String[] classNames = value.split(LIST_DELIMITER);
        final List<T> result =  new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++)
                add((T) getInstanceFromClassName(classNames[i], push(parameterName, String.valueOf(i)), expectedSuperClass));
        }};
        registerInstanceIfReferenced(parameterName, result);
        return result;
    }
    
    public <T> Option<List<T>> getOptionalInstancesFromParameter(final String parameterName, final Class expectedSuperClass) {
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String value = properties.getProperty(parameterName);
        if (properties.containsKey(parameterName))
            return new Option<List<T>>((List<T>) getInstancesFromParameter(parameterName, expectedSuperClass));
        else
            return Option.NONE;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Standard Methods">
    @Override
    public final boolean repOK() {
        return properties != null
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
