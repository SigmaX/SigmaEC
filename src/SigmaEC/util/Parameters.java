package SigmaEC.util;

import SigmaEC.BuilderT;
import java.lang.reflect.Constructor;
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
    
    // <editor-fold defaultstate="collapsed" desc="Builders">
    public static <T extends BuilderT> T getBuilderFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String className = getParameter(properties, parameterName);
        return getBuilderFromClassName(properties, className, parameterName, expectedSuperClass);
    }
    
    public static <T extends BuilderT> Option<T> getOptionalBuilderFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<String> className = getOptionalParameter(properties, parameterName);
        if (!className.isDefined())
            return Option.NONE;
        return new Option<T>((T) getBuilderFromClassName(properties, className.get(), parameterName, expectedSuperClass));
    }
    
    public static <T extends BuilderT> List<T> getBuildersFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String[] classNames = getParameter(properties, parameterName).split(LIST_DELIMITER);
        return new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++)
                add((T) getBuilderFromClassName(properties, classNames[i], push(parameterName, String.valueOf(i)), expectedSuperClass));
        }};
    }
    
    public static <T extends BuilderT> Option<List<T>> getOptionalBuildersFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<String> classNamesOption = getOptionalParameter(properties, parameterName);
        if (!classNamesOption.isDefined())
            return Option.NONE;
        final String[] classNames = classNamesOption.get().split(LIST_DELIMITER);
        return new Option<List<T>>(new ArrayList<T>() {{
            for (int i = 0; i < classNames.length; i++)
                add((T) getBuilderFromClassName(properties, classNames[i], push(parameterName, String.valueOf(i)), expectedSuperClass));
        }});
    }

    private static <T extends BuilderT> T getBuilderFromClassName(final Properties properties, final String className, final String base, final Class expectedSuperClass) throws IllegalStateException {
        // Check that the class exists and is the expected subtype
        final Class c, builder;
        try {
            c = Class.forName(className);
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(String.format("%s: No such class '%s', requested by parameter '%s' (%s).", Parameters.class.getSimpleName(), className, base, ex.getMessage()));
        }
        if (!expectedSuperClass.isAssignableFrom(c))
            throw new IllegalStateException(String.format("%s: Class '%s', requested by the parameter '%s' is not a subtype of '%s'.", Parameters.class.getSimpleName(), className, base, expectedSuperClass.getSimpleName()));
        
        // Check that the builder exists and is the expected subtype
        try {
            builder = Class.forName(className + ".Builder");
        } catch (final ClassNotFoundException ex) {
            throw new IllegalStateException(String.format("%s: Class '%s', requested by parameter '%s', has no Builder (%s).", Parameters.class.getSimpleName(), className, base, ex.getMessage()));
        }
        if (!BuilderT.class.isAssignableFrom(builder))
            throw new IllegalStateException(String.format("%s: Builder '%s', requested by the parameter '%s' is not a subtype of '%s'.", Parameters.class.getSimpleName(), className, base, BuilderT.class.getSimpleName()));
        
        final T result;
        try {
            final Constructor constructor = builder.getDeclaredConstructor(new Class[] { Properties.class, String.class });
            result = (T) constructor.newInstance(properties, base);
        } catch (final Exception ex) {
            throw new IllegalStateException(String.format("%s: failed to create instance of class '%s' (%s).", Parameters.class.getSimpleName(), className, ex.getMessage()));
        }
        return result;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Instances">
    public static <T> T getInstanceFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final String className = getParameter(properties, parameterName);
        return (T) getBuilderFromClassName(properties, className, parameterName, expectedSuperClass).build();
    }
    
    public static <T> Option<T> getOptionalInstanceFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<BuilderT<T>> builder = getOptionalBuilderFromParameter(properties, parameterName, expectedSuperClass);
        if (builder.isDefined())
            return new Option<T>((T) builder.get().build());
        else
            return Option.NONE;
    }
    
    public static <T> List<T> getInstancesFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final List<BuilderT<T>> builders = getBuildersFromParameter(properties, parameterName, expectedSuperClass);
        return new ArrayList<T>() {{
           for (final BuilderT<T> b : builders)
               add((T) b.build());
        }};
    }
    
    public static <T> Option<List<T>> getOptionalInstancesFromParameter(final Properties properties, final String parameterName, final Class expectedSuperClass) {
        assert(properties != null);
        assert(parameterName != null);
        assert(expectedSuperClass != null);
        final Option<List<BuilderT<T>>> builders = getOptionalBuildersFromParameter(properties, parameterName, expectedSuperClass);
        if (builders.isDefined())
            return new Option(new ArrayList<T>() {{
                for (final BuilderT<T> b : builders.get())
                    add((T) b.build());
            }});
        else
            return Option.NONE;
    }
    // </editor-fold>
}
