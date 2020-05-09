package com.ivan1pl.witchcraft.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that the annotated parameter is a command option.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
    /**
     * Short option name.
     */
    char shortName() default '\0';

    /**
     * Long option name.
     */
    String longName() default "";

    /**
     * Description.
     */
    String description() default "";

    /**
     * How many times this option can be used in a command.
     */
    int max() default 1;
}
