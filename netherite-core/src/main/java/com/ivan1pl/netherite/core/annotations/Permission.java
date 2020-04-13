package com.ivan1pl.netherite.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for generating permissions section in {@code plugin.yml} file.
 */
@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface Permission {
    /**
     * Permission node name.
     */
    String node();

    /**
     * A short description of what this permission allows.
     */
    String description() default "";

    /**
     * Sets the default value of the permission.
     */
    PermissionDefault defaultValue() default PermissionDefault.DEFAULT;

    /**
     * Children nodes for the permission. Child nodes are permission names.
     */
    ChildPermission[] children() default {};
}
