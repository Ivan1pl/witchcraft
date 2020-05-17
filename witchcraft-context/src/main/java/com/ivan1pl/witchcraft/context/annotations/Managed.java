package com.ivan1pl.witchcraft.context.annotations;

import java.lang.annotation.*;

/**
 * Annotation marking classes as managed by WitchCraft's dependency injection.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Managed {
}
