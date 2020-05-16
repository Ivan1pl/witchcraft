package com.ivan1pl.witchcraft.context;

import com.google.common.primitives.Primitives;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValues;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import com.ivan1pl.witchcraft.context.annotations.Module;
import com.ivan1pl.witchcraft.context.exception.*;
import com.ivan1pl.witchcraft.context.proxy.Aspect;
import com.ivan1pl.witchcraft.context.proxy.ProxyInvocationHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

/**
 * WitchCraft's dependency injection context.
 */
public final class WitchCraftContext {
    /**
     * Plugin instance.
     */
    private final JavaPlugin javaPlugin;

    /**
     * Packages to scan.
     */
    private final String[] basePackages;

    /**
     * Annotations indicating managed classes.
     */
    private final List<Class<? extends Annotation>> annotations;

    /**
     * Context containing instances of all managed classes.
     */
    private final Map<String, List<Object>> context = new HashMap<>();

    /**
     * Invocation handler for all managed classes.
     */
    private final ProxyInvocationHandler proxyInvocationHandler = new ProxyInvocationHandler();

    /**
     * Create new context for given plugin.
     * @param javaPlugin plugin instance
     */
    public WitchCraftContext(JavaPlugin javaPlugin) {
        this(javaPlugin, javaPlugin.getClass().getPackage() == null ?
                new String[0] : new String[] { javaPlugin.getClass().getPackage().getName() });
    }

    /**
     * Create new context for given plugin.
     * @param javaPlugin plugin instance
     * @param basePackages package scan path
     */
    public WitchCraftContext(JavaPlugin javaPlugin, String[] basePackages) {
        this(javaPlugin, basePackages, Managed.class);
    }

    /**
     * Create new context for given plugin.
     * @param javaPlugin plugin instance
     * @param basePackages package scan path
     * @param annotations annotations marking managed classes
     */
    @SafeVarargs
    public WitchCraftContext(JavaPlugin javaPlugin, String[] basePackages, Class<? extends Annotation>... annotations) {
        this.javaPlugin = javaPlugin;
        this.basePackages = getBasePackages(javaPlugin, basePackages);
        this.annotations = Arrays.asList(annotations);
    }

    /**
     * Get an array of packages to scan, including provided packages and all enabled module packages.
     * @param javaPlugin plugin instance
     * @param basePackages package scan path
     * @return provided packages and all enabled module packages
     */
    private static String[] getBasePackages(JavaPlugin javaPlugin, String[] basePackages) {
        List<String> packageList = Arrays.asList(basePackages);
        for (Annotation annotation : javaPlugin.getClass().getAnnotations()) {
            Module module = annotation.annotationType().getAnnotation(Module.class);
            if (module != null && annotation.annotationType().getPackage() != null) {
                javaPlugin.getLogger().info(String.format(
                        "Detected module in package: %s", annotation.annotationType().getPackage().getName()));
                packageList.add(annotation.annotationType().getPackage().getName());
            }
        }
        return packageList.toArray(new String[0]);
    }

    /**
     * Initialize context.
     * @throws InitializationFailedException when WitchCraft is unable to initialize context
     */
    public void init() throws InitializationFailedException {
        List<Class<?>> classes = new LinkedList<>();
        try {
            for (String basePackage : basePackages) {
                javaPlugin.getLogger().info(String.format("Starting package scan for package: %s", basePackage));
                for (Class<? extends Annotation> annotation : annotations) {
                    classes.addAll(new Reflections(basePackage).getTypesAnnotatedWith(annotation));
                }
                javaPlugin.getLogger().info(String.format("Package scan completed for package: %s", basePackage));
            }
            Queue<Class<?>> creationQueue = new CreationQueueBuilder(javaPlugin, classes).createQueue();

            add(this);
            add(javaPlugin);

            while (!creationQueue.isEmpty()) {
                Class<?> clazz = creationQueue.poll();
                Object instance = attemptCreate(clazz);
                add(instance);
            }

            initAspects();
        } catch (Exception e) {
            throw new InitializationFailedException("Failed to initialize WitchCraft context", e);
        }
    }

    /**
     * Find all declared aspects and add them to invocation handler.
     */
    private void initAspects() {
        for (Annotation annotation : javaPlugin.getClass().getAnnotations()) {
            Module module = annotation.annotationType().getAnnotation(Module.class);
            if (module != null) {
                for (Class<? extends Aspect> aspect : module.aspects()) {
                    proxyInvocationHandler.addAspect(get(aspect));
                }
            }
        }
    }

    /**
     * Attempt to create new instance of given type.
     * @param clazz requested type
     * @return instance of given type
     */
    private Object attemptCreate(Class<?> clazz) throws IllegalAccessException, InvocationTargetException,
            InstantiationException, InitializationFailedException, NoSuchMethodException {
        Constructor<?> constructor = clazz.getConstructors()[0];
        Parameter[] parameterTypes = constructor.getParameters();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i) {
            ConfigurationValue configurationValue = parameterTypes[i].getAnnotation(ConfigurationValue.class);
            if (configurationValue == null) {
                ConfigurationValues configurationValues = parameterTypes[i].getAnnotation(ConfigurationValues.class);
                if (configurationValues == null) {
                    parameters[i] = get(parameterTypes[i].getType());
                } else if (!parameterTypes[i].getType().isAssignableFrom(Properties.class)) {
                    throw new InitializationFailedException("Parameter " + parameterTypes[i].getName() + " of type " +
                            parameterTypes[i].getType().getCanonicalName() + " cannot be assigned from " +
                            Properties.class.getCanonicalName());
                } else {
                    parameters[i] = createProperties(configurationValues);
                }
            } else {
                parameters[i] = javaPlugin.getConfig().getObject(
                        configurationValue.value(), Primitives.wrap(parameterTypes[i].getType()), null);
            }
        }
        return createProxy(clazz, parameterTypes, parameters);
    }

    /**
     * Create proxy of given class. This will create proxy of any given class except aspects.
     * @param clazz class to proxy
     * @param parameterDefinitions constructor parameter definitions
     * @param parameters constructor parameters
     * @return proxy instance (or just an object of the requested type in case of aspects)
     */
    private Object createProxy(Class<?> clazz, Parameter[] parameterDefinitions, Object[] parameters)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?>[] parameterTypes = Stream.of(parameterDefinitions).map(Parameter::getType).toArray(Class[]::new);
        if (Aspect.class.isAssignableFrom(clazz) || Listener.class.isAssignableFrom(clazz)) {
            return clazz.getConstructor(parameterTypes).newInstance(parameters);
        } else {
            ProxyFactory f = new ProxyFactory();
            f.setSuperclass(clazz);
            Class<?> proxyClass = f.createClass();
            Proxy proxy = (Proxy) proxyClass.getConstructor(parameterTypes).newInstance(parameters);
            proxy.setHandler(proxyInvocationHandler);
            return proxy;
        }
    }

    /**
     * Create and fill {@link Properties} instance from configuration values.
     */
    private Properties createProperties(ConfigurationValues configurationValues) {
        Properties properties = new Properties();
        for (String key : configurationValues.keys()) {
            String configKey = configurationValues.prefix().isEmpty() ? key : configurationValues.prefix() + "." + key;
            String value = javaPlugin.getConfig().getString(configKey);
            if (value != null && !value.isEmpty()) {
                properties.setProperty(key, value);
            }
        }
        return properties;
    }

    /**
     * Add object to WitchCraft dependency injection context.
     * @param object object to add
     */
    private void add(Object object) {
        Class<?> clazz = object.getClass();
        Queue<Class<?>> classes = new LinkedList<>();
        classes.add(clazz);
        while (!classes.isEmpty()) {
            Class<?> c = classes.poll();
            List<Object> objects = context.computeIfAbsent(c.getCanonicalName(), o -> new LinkedList<>());
            objects.add(object);
            classes.addAll(Arrays.asList(c.getInterfaces()));
            c = c.getSuperclass();
            if (c != null) {
                classes.add(c);
            }
        }
    }

    /**
     * Get all managed objects for given type.
     * @param type type
     * @return all managed objects of given type
     */
    private List<Object> getCandidatesForType(Class<?> type) {
        if (type == null) {
            return new ArrayList<>();
        }
        return context.getOrDefault(type.getCanonicalName(), new ArrayList<>());
    }

    /**
     * Get object from context.
     * @param clazz class object
     * @param <T> requested object type
     * @return object of the required type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        List<Object> candidates = getCandidatesForType(clazz);
        if (candidates.size() > 1) {
            throw new NonUniqueCandidateException("Non unique candidate of type " + clazz.getCanonicalName());
        }
        if (candidates.isEmpty()) {
            throw new CandidateNotFoundException(
                    "Candidate of type " + clazz.getCanonicalName() + " could not be found");
        }
        return (T) candidates.get(0);
    }

    /**
     * Clear the context.
     * <p>
     * Warning: the plugin might not work properly after invoking this method.
     */
    public void clear() {
        context.clear();
    }

    /**
     * Builder class for creating instantiation queue.
     */
    private static class CreationQueueBuilder {
        private final Map<Class<?>, List<Class<?>>> before = new HashMap<>();

        /**
         * Create builder instance.
         * @param javaPlugin plugin instance
         * @param classes managed classes
         * @throws InitializationFailedException when queue cannot be initialized
         * @throws UnsatisfiedDependencyException when the builder is unable to determine unique dependency for some
         *                                        class
         */
        CreationQueueBuilder(JavaPlugin javaPlugin, List<Class<?>> classes)
                throws InitializationFailedException, UnsatisfiedDependencyException {
            for (Class<?> clazz : classes) {
                Constructor<?>[] constructors = clazz.getConstructors();
                if (constructors.length != 1) {
                    throw new InitializationFailedException("Unable to instantiate class: " + clazz.getCanonicalName() +
                            ". Invalid number of constructors (required exactly 1)");
                }
                Constructor<?> constructor = constructors[0];
                List<Class<?>> beforeClass = before.computeIfAbsent(clazz, c -> new LinkedList<>());
                for (Parameter parameter : constructor.getParameters()) {
                    if (!parameter.getType().isAssignableFrom(javaPlugin.getClass()) &&
                            !parameter.getType().isAssignableFrom(WitchCraftContext.class) &&
                            parameter.getAnnotation(ConfigurationValue.class) == null &&
                            parameter.getAnnotation(ConfigurationValues.class) == null) {
                        int numElements = 0;
                        for (Class<?> depClass : classes) {
                            if (parameter.getType().isAssignableFrom(depClass)) {
                                beforeClass.add(depClass);
                                numElements++;
                            }
                        }
                        if (numElements == 0) {
                            throw new UnsatisfiedDependencyException("Could not instantiate parameter of type " +
                                    parameter.getType().getCanonicalName() + " while attempting to build an object of type " +
                                    clazz.getCanonicalName() + ": no candidates found");
                        }
                        if (numElements > 1) {
                            throw new UnsatisfiedDependencyException("Could not instantiate parameter of type " +
                                    parameter.getType().getCanonicalName() + " while attempting to build an object of type " +
                                    clazz.getCanonicalName() + ": multiple candidates found");
                        }
                    }
                }
            }
        }

        /**
         * Create queue containing classes in order they should be instantiated.
         * @return instantiation queue
         * @throws DependencyCycleException when there is a dependency cycle
         */
        Queue<Class<?>> createQueue() throws DependencyCycleException {
            Queue<Class<?>> result = new LinkedList<>();
            while (!before.isEmpty()) {
                Class<?> nextElement = getAnySatisfied();
                if (nextElement == null) {
                    throw new DependencyCycleException("Detected cycle in dependency graph, unable to instantiate");
                }
                result.add(nextElement);
                for (List<Class<?>> beforeList : before.values()) {
                    beforeList.remove(nextElement);
                }
                before.remove(nextElement);
            }
            return result;
        }

        /**
         * Get any element with no further unsatisfied dependencies.
         * @return any element with no unsatisfied dependencies
         */
        private Class<?> getAnySatisfied() {
            for (Map.Entry<Class<?>, List<Class<?>>> entry : before.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    return entry.getKey();
                }
            }
            return null;
        }
    }
}
