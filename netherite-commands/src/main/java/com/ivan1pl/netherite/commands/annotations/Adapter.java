package com.ivan1pl.netherite.commands.annotations;

import com.ivan1pl.netherite.commands.base.TypeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to provide a way to convert {@code String} command parameters to method parameters of any
 * desired type.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
    /**
     * Adapter class that should be used to convert the value.
     */
    Class<? extends TypeAdapter> value();
}
