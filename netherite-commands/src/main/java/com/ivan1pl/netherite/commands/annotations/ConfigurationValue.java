package com.ivan1pl.netherite.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration value will be assigned to the parameter annotated with this annotation. If the assignment is not
 * possible, command will fail.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationValue {
    /**
     * Configuration key.
     */
    String value();
}
