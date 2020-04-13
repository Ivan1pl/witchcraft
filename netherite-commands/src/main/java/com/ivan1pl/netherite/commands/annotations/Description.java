package com.ivan1pl.netherite.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to provide a description for a command or its subcommand. The provided description will
 * be used in generated help message.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    /**
     * Command or subcommand short description.
     */
    String shortDescription();

    /**
     * Command or subcommand long description.
     */
    String detailedDescription();
}
