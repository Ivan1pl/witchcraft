package com.ivan1pl.witchcraft.jdbc.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods in managed classes can be annotated with this annotation to enable automatic transaction management. Method
 * invocations will be wrapped, a new transaction will be started before the invocation and committed after it (or
 * rolled back if an exception occurred).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    /**
     * Transaction isolation level. If not set, default isolation level of the database connection will be used.
     */
    Isolation isolation() default Isolation.DEFAULT;

    /**
     * Transaction mode. Default is {@link TransactionMode#REQUIRED}, which will start a new transaction only if there
     * are no active ones.
     */
    TransactionMode transactionMode() default TransactionMode.REQUIRED;

    /**
     * Exception types that will cause a rollback when thrown within the transactional method invocation. By default all
     * thrown exceptions will cause rollbacks.
     */
    Class<? extends Throwable>[] rollbackFor() default { Throwable.class };
}
