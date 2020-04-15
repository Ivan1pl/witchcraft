package com.ivan1pl.witchcraft.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command annotation. Any annotated class in the package-scan base package or its subpackages will be treated as
 * a command and will be automatically added to {@code plugin.yml} file.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Command name.
     */
    String name();

    /**
     * Command aliases. Default is none.
     */
    String[] aliases() default {};

    /**
     * Permission node required to execute this command.
     */
    String permission() default "";

    /**
     * Command description.
     */
    String description() default "";
}
