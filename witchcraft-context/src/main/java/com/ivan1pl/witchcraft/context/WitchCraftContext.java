package com.ivan1pl.witchcraft.context;

import com.google.common.primitives.Primitives;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValue;
import com.ivan1pl.witchcraft.context.annotations.ConfigurationValues;
import com.ivan1pl.witchcraft.context.annotations.Managed;
import com.ivan1pl.witchcraft.context.exception.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * WitchCraft's dependency injection context.
 */
public final class WitchCraftContext {
    private final JavaPlugin javaPlugin;
    private final String[] basePackages;
    private final List<Class<? extends Annotation>> annotations;
    private final Map<String, List<Object>> context = new HashMap<>();

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
        this.basePackages = basePackages;
        this.annotations = Arrays.asList(annotations);
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
            }
            Queue<Class<?>> creationQueue = new CreationQueueBuilder(javaPlugin, classes).createQueue();

            add(this);
            add(javaPlugin);

            while (!creationQueue.isEmpty()) {
                Class<?> clazz = creationQueue.poll();
                Object instance = attemptCreate(clazz);
                add(instance);
            }
        } catch (Exception e) {
            throw new InitializationFailedException("Failed to initialize WitchCraft context", e);
        }
    }

    /**
     * Attempt to create new instance of given type.
     * @param clazz requested type
     * @return instance of given type
     */
    private Object attemptCreate(Class<?> clazz) throws IllegalAccessException, InvocationTargetException,
            InstantiationException, InitializationFailedException {
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
        return constructor.newInstance(parameters);
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
     *
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
