package com.ivan1pl.netherite.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Plugin annotation. Annotate your plugin class to provide package-scan path.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
    /**
     * Package-scan path. All classes within this package or any of its subpackages will be scanned for a
     * {@link Command} annotation. Defaults to the package holding the plugin class.
     */
    String basePackage() default "";
}
