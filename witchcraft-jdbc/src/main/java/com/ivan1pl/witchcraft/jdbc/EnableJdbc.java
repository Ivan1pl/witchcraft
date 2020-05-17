package com.ivan1pl.witchcraft.jdbc;

import com.ivan1pl.witchcraft.context.annotations.Module;
import com.ivan1pl.witchcraft.jdbc.transaction.TransactionAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable JDBC module.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Module(aspects = TransactionAspect.class)
public @interface EnableJdbc {
}
