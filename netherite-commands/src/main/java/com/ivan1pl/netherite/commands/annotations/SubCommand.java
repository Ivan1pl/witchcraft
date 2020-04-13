package com.ivan1pl.netherite.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subcommand annotation. Any annotated method inside a class annotated with {@link Command} annotation will be treated
 * as that command's subcommand. Help for the subcommand will be automatically generated and added to {@code plugin.yml}
 * file.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
    /**
     * Subcommand name. Default is empty string, which means the user does not need to provide a subcommand name to
     * execute it. There may be only one such subcommand.
     */
    String value() default "";

    /**
     * Permission node required to execute this subcommand.
     */
    String permission() default "";
}
