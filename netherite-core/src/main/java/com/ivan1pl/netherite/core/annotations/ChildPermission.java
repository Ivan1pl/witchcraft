package com.ivan1pl.netherite.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for generating child permissions section in {@code plugin.yml} file.
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface ChildPermission {
    /**
     * Permission node name.
     */
    String node();

    /**
     * Indicates whether the child permission should inherit the parent permission or its inverse.
     */
    boolean inherit();
}
