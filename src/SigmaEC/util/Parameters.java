package SigmaEC.util;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Parameters {
    private Parameters() {
        throw new AssertionError(String.format("%s: Cannot create instance of static utility class.", this.getClass().getSimpleName()));
    }
    
    public static int getIntParameter(final Properties properties, final String parameterName) {
        assert(properties != null);
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was no found in properties.", Parameters.class.getSimpleName(), parameterName));
        return Integer.parseInt(value);
    }
    
    public static String getParameter(final Properties properties, final String parameterName) {
        assert(properties != null);
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        if (value == null)
            throw new IllegalStateException(String.format("%s: Parameter '%s' was no found in properties.", Parameters.class.getSimpleName(), parameterName));
        return value;
    }
    
    public static <T> T getInstanceFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        final String className = getParameter(properties, parameterName);
        
        final Class c;
        try {
            c = Class.forName(className);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(String.format("%s: No such class '%s', requested by parameter '%s' (%s).", Parameters.class.getSimpleName(), className, parameterName, ex.getMessage()));
        }
        
        if (!expectedSuperClass.isAssignableFrom(c))
            throw new IllegalStateException(String.format("%s: The class requested by the parameter '%s' is not a subtype of '%s'.", Parameters.class.getSimpleName(), parameterName, expectedSuperClass.getSimpleName()));

        final T result;
        try {
            final Method factoryMethod = c.getMethod("create", String.class, Properties.class);
            result = (T) factoryMethod.invoke(null, "", properties);
        } catch (final Exception ex) {
            throw new IllegalStateException(String.format("%s: failed to create instance of class '%s' (%s).", Parameters.class.getSimpleName(), className, ex.getMessage()));
        }
        return result;
    }
}
