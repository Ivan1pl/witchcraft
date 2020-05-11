package com.ivan1pl.witchcraft.context.annotations;

import java.lang.annotation.*;

/**
 * Configuration value will be assigned to the parameter annotated with this annotation. If the assignment is not
 * possible, an exception will be thrown (in case of commands, the command will fail).
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigurationValue {
    /**
     * Configuration key.
     */
    String value();
}
