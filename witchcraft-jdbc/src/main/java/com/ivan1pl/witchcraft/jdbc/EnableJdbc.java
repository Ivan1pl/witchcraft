package com.ivan1pl.witchcraft.jdbc;

import com.ivan1pl.witchcraft.context.annotations.Module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable JDBC module.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Module
public @interface EnableJdbc {
}
