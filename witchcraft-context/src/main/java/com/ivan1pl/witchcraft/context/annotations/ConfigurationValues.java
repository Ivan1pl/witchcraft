package com.ivan1pl.witchcraft.context.annotations;

import java.lang.annotation.*;

/**
 * Multiple configuration values will be assigned to the parameter annotated with this annotation. It is only applicable
 * to parameters of type {@link java.util.Properties} in constructor parameters of managed classes. This will always
 * produce an object of type {@link java.util.Properties}, even if no configuration values were found. Only found values
 * will be assigned to that object.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationValues {
    /**
     * Configuration key prefix.
     */
    String prefix();

    /**
     * Configuration keys.
     */
    String[] keys();
}
