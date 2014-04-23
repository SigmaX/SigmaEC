package SigmaEC.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Eric 'Siggy' Scott
 */
public class Parameters {
    private final static String LIST_DELIMITER = ",";
    private final static String PROPERTY_DELIMITER = ".";
    private Parameters() {
        throw new AssertionError(String.format("%s: Cannot create instance of static utility class.", this.getClass().getSimpleName()));
    }
    
    public static String push(final String base, final String param) {
        return String.format("%s%s%s", base, PROPERTY_DELIMITER, param);
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
    
    public static Option<String> getOptionalParameter(final Properties properties, final String parameterName) {
        assert(properties != null);
        assert(parameterName != null);
        final String value = properties.getProperty(parameterName);
        return (value == null) ? Option.NONE : new Option<String>(value);
    }
    
    public static <T> T getInstanceFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String className = getParameter(properties, parameterName);
        return getInstanceFromClassName(properties, className, parameterName, expectedSuperClass);
    }
    
    public static <T> Option<T> getOptionalInstanceFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<String> className = getOptionalParameter(properties, parameterName);
        if (!className.isDefined())
            return Option.NONE;
        return new Option<T>((T) getInstanceFromClassName(properties, className.get(), parameterName, expectedSuperClass));
    }
    
    public static <T> List<T> getInstancesFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String[] classNames = getParameter(properties, parameterName).split(LIST_DELIMITER);
        return new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++)
                add((T) getInstanceFromClassName(properties, classNames[i], push(parameterName, String.valueOf(i)), expectedSuperClass));
        }};
    }
    
    public static <T> Option<List<T>> getOptionalInstancesFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<String> classNamesOption = getOptionalParameter(properties, parameterName);
        if (!classNamesOption.isDefined())
            return Option.NONE;
        final String[] classNames = classNamesOption.get().split(LIST_DELIMITER);
        return new Option<List<T>>(new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++)
                add((T) getInstanceFromClassName(properties, classNames[i], push(parameterName, String.valueOf(i)), expectedSuperClass));
        }});
    }

    private static <T> T getInstanceFromClassName(final Properties properties, final String className, final String base, final Class expectedSuperClass) throws IllegalStateException {
        final Class c;
        try {
            c = Class.forName(className);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(String.format("%s: No such class '%s', requested by parameter '%s' (%s).", Parameters.class.getSimpleName(), className, base, ex.getMessage()));
        }
        if (!expectedSuperClass.isAssignableFrom(c))
            throw new IllegalStateException(String.format("%s: The class requested by the parameter '%s' is not a subtype of '%s'.", Parameters.class.getSimpleName(), base, expectedSuperClass.getSimpleName()));
        final T result;
        try {
            final Method factoryMethod = c.getMethod("create", String.class, Properties.class);
            result = (T) factoryMethod.invoke(null, properties, base);
        } catch (final Exception ex) {
            throw new IllegalStateException(String.format("%s: failed to create instance of class '%s' (%s).", Parameters.class.getSimpleName(), className, ex.getMessage()));
        }
        return result;
    }
}
